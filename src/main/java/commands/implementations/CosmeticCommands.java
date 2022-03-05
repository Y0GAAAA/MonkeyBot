package commands.implementations;

import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.ResponseMessage;
import commands.responses.VanillaMessage;

import java.util.Random;

public class CosmeticCommands {

    public static final Random chickenCoopRandomizer = new Random();

    @CommandAttributes.Command(
            helpText = "Creates a randomly generated chicken-coop"
    )
    public static ResponseMessage chickencoop(MessageContext context) {

        final int CHICKEN_WIDTH = 5;

        // the emoji count in a message before which the message will appear with big emojis
        final int DISCORD_EMOJI_CAP_UNTIL_SMALL_MESSAGE = 28;

        final String CHICKEN_CHAR = "\uD83D\uDC13";
        final String EMPTY_CHICKEN_CHAR = "      ";
        final String BRICK_CHAR = "\uD83E\uDDF1";

        StringBuilder coopBuilder = new StringBuilder();

        int emojiCount = 0;
        while (emojiCount < DISCORD_EMOJI_CAP_UNTIL_SMALL_MESSAGE) {

            coopBuilder.append(BRICK_CHAR);
            coopBuilder.append(' ');

            for (int j = 0; j < CHICKEN_WIDTH; j++) {
                if (chickenCoopRandomizer.nextBoolean()) {
                    coopBuilder.append(CHICKEN_CHAR);
                    emojiCount++;
                } else {
                    coopBuilder.append(EMPTY_CHICKEN_CHAR);
                }
                coopBuilder.append(' ');
            }

            coopBuilder.append(BRICK_CHAR);
            coopBuilder.append('\n');

            emojiCount += 2; // the two brick emojis per layer

        }

        return new VanillaMessage(coopBuilder.toString());
    }
    
}
