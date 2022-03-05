package commands.implementations;

import commands.dependencies.chess.BufferedPuzzleClient;
import commands.dependencies.chess.PuzzleData;
import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.ResponseMessage;
import commands.responses.EmbedMessage;
import commands.responses.ErrorMessage;
import commands.responses.VanillaMessage;
import cosmetics.Static;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFooterData;
import discord4j.discordjson.json.EmbedImageData;
import discord4j.discordjson.json.ImmutableEmbedData;
import parsing.arguments.Sentence;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PuzzleCommands {

    public static final Random puzzleIdRandomizer = new Random();
    public static final HashMap<Long, String> cachedPuzzleSolutions = new HashMap<>();
    public static final BufferedPuzzleClient bufferedPuzzleClient = new BufferedPuzzleClient(
            10,
            puzzleResponse -> puzzleResponse.data.forcedLine.size() == 1
    );

    @CommandAttributes.Command(
            helpText = "Chess puzzle go brrr"
    )
    public static ResponseMessage puzzle(MessageContext context) {
        long puzzleId = Math.abs(puzzleIdRandomizer.nextLong());

        ImmutableEmbedData.Builder puzzleFrameBuilder = EmbedData.builder();

        PuzzleData puzzleData = bufferedPuzzleClient.popPuzzle().data;

        String blunderString = "You opponent blundered with " + Static.encapsulate(puzzleData.blunderMove, "`");
        String dynboardUrl = getDynboardUrl(puzzleData.fenBefore, puzzleData.whiteToMove());

        puzzleFrameBuilder.title(blunderString);
        puzzleFrameBuilder.image(EmbedImageData.builder().url(dynboardUrl).build());
        puzzleFrameBuilder.description("Solve using :\n" + Static.encapsulate("::solve " + puzzleId + " <move>", "`"));
        puzzleFrameBuilder.footer(EmbedFooterData.builder().text("\uD83C\uDFB1 " + puzzleData.elo + " elo rated puzzle\n\uD83D\uDD79 Expecting " + puzzleData.forcedLine.size() + " move").build());

        cachedPuzzleSolutions.put(puzzleId, puzzleData.getNormalizedSolutionString());

        return new EmbedMessage(puzzleFrameBuilder.build());
    }

    @CommandAttributes.Command(
            helpText = "Validate a puzzle solution"
    )
    public static ResponseMessage solve(MessageContext context, Long puzzleId, Sentence solution) {
        String realSolution = cachedPuzzleSolutions.get(puzzleId);

        if (Objects.isNull(realSolution)) {
            return new ErrorMessage("Invalid puzzle ID");
        }

        final String answerString = solution.toString()
                                            .trim();

        final String resultString = answerString.equals(realSolution) ? "LESSGOOO ‼️\n" + Static.encapsulate(answerString, "`") + " was the right answer." : "Wrong answer, the solution was : " + Static.encapsulate(realSolution, "`");

        cachedPuzzleSolutions.remove(puzzleId);

        return new VanillaMessage(resultString);

    }

    private static String getDynboardUrl(String fen, boolean whiteToMove) {
        try {

            StringBuilder dynboardUrlBuilder = new StringBuilder("https://www.chess.com/dynboard?size=2&fen=");

            dynboardUrlBuilder.append(URLEncoder.encode(fen, "UTF-8"));
            dynboardUrlBuilder.append("&coordinates=true&board=glass&piece=glass");

            if (!whiteToMove) {
                dynboardUrlBuilder.append("&flip=true");
            }

            return dynboardUrlBuilder.toString();
        } catch (UnsupportedEncodingException ignored) {
            throw new IllegalStateException("unreachable");
        }
    }

}