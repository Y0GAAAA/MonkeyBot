package commands.implementations;

import commands.dependencies.games.RouletteEngine;
import commands.dependencies.games.SlotsEngine;
import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.ErrorMessage;
import commands.responses.ResponseMessage;
import commands.responses.VanillaMessage;
import cosmetics.Static;
import discord4j.discordjson.json.MessageEditRequest;
import discord4j.rest.entity.RestMessage;
import storage.DbUser;
import storage.UserFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameCommands {

    public static final int SLOTS_SIZE = 3;
    
    public static final String SLOTS_LOSE_MESSAGE = "You lost \uD83C\uDFC3\uD83C\uDFFF\u200D♀️";
    public static final String SLOTS_WIN_MESSAGE = "Nice luck \uD83D\uDE29";
    public static final int SLOTS_WIN_MULTIPLIER = 5;
    
    public static final String[] SLOTS_TOKENS = new String[] {"\uD83C\uDF49", "\uD83C\uDF47", "\uD83D\uDC8E", "\uD83C\uDF52", "\uD83D\uDD14"};
    
    private static void editVanillaMessage(RestMessage message, String newContent) {
    
        MessageEditRequest nextRouletteFrame = MessageEditRequest.builder()
                                                                 .content(newContent)
                                                                 .build();
    
        message.edit(nextRouletteFrame)
               .block();
    }
    
    @CommandAttributes.Command(
            helpText = "Slots machine"
    )
    public static ResponseMessage slots(MessageContext context, Long bet) {
        final DbUser player = UserFactory.fromId(context.getUserId());

        if (bet > player.getCredits())
            return new ErrorMessage("insufficient funds");
    
        final SlotsEngine slotsEngine = new SlotsEngine(
                SLOTS_SIZE,
                SLOTS_SIZE,
                SLOTS_TOKENS
        );
        
        if (bet <= 0)
            return new ErrorMessage("bet value cannot be negative");
        
        slotsEngine.fullSpin();
    
        RestMessage message = context.send(
                new VanillaMessage(slotsEngine.getSlotsString(false))
        );
        
        while (true) {
    
            boolean isLast = !slotsEngine.spinNextColumn();
            
            if (isLast) {
                
                slotsEngine.endSpinSequence();
    
                final int matches = slotsEngine.getMatchingLineCount();
                final boolean lost = matches == 0;
                
                final long creditsWon = bet * matches * SLOTS_WIN_MULTIPLIER;
                
                final String finalString = slotsEngine.getSlotsString(true) + '\n' + (lost ? SLOTS_LOSE_MESSAGE : SLOTS_WIN_MESSAGE + "\n" + getWonCreditsString(creditsWon));
                
                editVanillaMessage(
                        message,
                        finalString
                );
                
                player.setCredits(player.getCredits() - bet);
                
                if (!lost)
                    player.setCredits(player.getCredits() + creditsWon);
                
                player.commit();
                
                return null;
            }
            
            editVanillaMessage(
                    message,
                    slotsEngine.getSlotsString(false)
            );
            
        }
        
    }
    
    public static final int ROULETTE_LENGTH = 15;
    public static final int CENTER_TILE_INDEX = ROULETTE_LENGTH / 2;
    
    public static final int SQUARE_EMOJI_SPACE_WIDTH = 6;
    
    private static RouletteEngine.Tile[] getNextTiles(int count) {
        return Stream.generate(
            new RouletteEngine.TileGenerator()
        )
        .limit(count)
        .toArray(RouletteEngine.Tile[]::new);
    }
    
    public static final String ROULETTE_LOSE_MESSAGE = "You lost \uD83D\uDC6F\u200D♀️";
    public static final String ROULETTE_WIN_MESSAGE = "Winning spin \uD83D\uDD04";
    
    @CommandAttributes.Command(
            helpText = "Casino roulette"
    )
    public static ResponseMessage roulette(MessageContext context, Long bet, String color) {
        final RouletteEngine.Tile userSelectedTile = RouletteEngine.Tile.fromColorString(color);
    
        if (Objects.isNull(userSelectedTile))
            return new ErrorMessage("invalid color string : " + color);
        
        final DbUser player = UserFactory.fromId(context.getUserId());
        
        if (bet > player.getCredits())
            return new ErrorMessage("insufficient funds");
        
        player.setCredits(player.getCredits() - bet);
        
        final RouletteEngine.Tile[] tiles = getNextTiles(ROULETTE_LENGTH);
        final RouletteEngine.Tile winningTile = tiles[CENTER_TILE_INDEX];
        
        final boolean won = (winningTile == userSelectedTile);
        final long creditsWon = bet * winningTile.getMultiplier();

        if (won) {
            player.setCredits(player.getCredits() + creditsWon);
        }
        
        player.commit();
        
        final String rouletteString = Arrays.stream(tiles)
                                            .map(RouletteEngine.Tile::getDisplay)
                                            .collect(Collectors.joining());
        
        final String arrowSpaceOffset = String.valueOf(
                new char[SQUARE_EMOJI_SPACE_WIDTH * CENTER_TILE_INDEX - 1]
        ).replace('\0', ' ');
        
        return new VanillaMessage("⁃" + arrowSpaceOffset + "⬇\n" + rouletteString + "\n" + (won ? ROULETTE_WIN_MESSAGE + "\n" + getWonCreditsString(creditsWon) : ROULETTE_LOSE_MESSAGE));
    }
    
    private static String getWonCreditsString(long credits) {
        return "You won " + Static.encapsulate(String.valueOf(credits), "`") + "\uD83D\uDCB5";
    }
    
}