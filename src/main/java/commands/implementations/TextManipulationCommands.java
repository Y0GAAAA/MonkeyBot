package commands.implementations;

import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.ResponseMessage;
import commands.responses.ErrorMessage;
import commands.responses.VanillaMessage;
import cosmetics.Clantag;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.ChannelData;
import parsing.Constants;
import parsing.arguments.Sentence;

import java.nio.charset.StandardCharsets;

public class TextManipulationCommands {

    @CommandAttributes.Command(
            helpText = "Reverses a given sentence"
    )
    public static ResponseMessage reverse(MessageContext context, Sentence s) {
        String reversed = new StringBuilder(s.toString())
                .reverse()
                .toString();

        return new VanillaMessage(reversed);
    }

    @CommandAttributes.Command(
            helpText = "Creates a wall for a given sentence"
    )
    public static ResponseMessage textwall(MessageContext context, Sentence s) {

        String text = s.toString();

        // width and height of the wall
        final int WALL_SIDE = text.length();
        final int size = (int) Math.pow(WALL_SIDE, 2) + (WALL_SIDE * 2);

        if (size >= 2000) {
            return new ErrorMessage("textwall string too long");
        }

        StringBuilder wallBuilder = new StringBuilder(size);
        for (int layerIndex = 0; layerIndex < WALL_SIDE + 1; layerIndex++) {
            wallBuilder.append(text, layerIndex, WALL_SIDE);
            wallBuilder.append(' ');
            wallBuilder.append(text, 0, layerIndex);
            wallBuilder.append('\n');
        }

        return new VanillaMessage(wallBuilder.toString());
    }

    @CommandAttributes.Command(
            helpText = "Creates a pyramid for a given sentence"
    )
    public static ResponseMessage pyramid(MessageContext context, Sentence s) {

        String pyramidString = s.toString();
        
        // S = 2y + char_count
        //   = 2(char_count(char_count - 1) / 2) + char_count
        //   = char_count ^ 2 - char_count + char_count
        //
        // S = char_count ^ 2
        int pyramidSize = (int) (Math.pow(pyramidString.length(), 2) + pyramidString.length() * 2); // additional newline characters

        if (pyramidSize >= 2000) {
            return new ErrorMessage("pyramid string too long");
        }

        return new VanillaMessage(Clantag.getPyramid(pyramidString).toString());
    }

    @CommandAttributes.Command(
            helpText = "Let the bot say whatever you want"
    )
    public static ResponseMessage repeat(MessageContext context, Sentence s) {
        context.getMessage()
               .delete()
               .subscribe();
    
        return new VanillaMessage(s.toString());
    }
    
    @CommandAttributes.Command(
            helpText = "Let the bot say whatever you want IN EVERY CHANNEL"
    )
    public static void widerepeat(MessageContext context, Sentence s) {
        context.getMessage()
               .delete()
               .subscribe();
        
        context.getGuild()
               .getChannels()
               .map(Channel::getRestChannel)
               .filter(c -> {
                   final ChannelData data = c.getData().block();
                   return data.type() == 0 && !data.name().get().equals(Constants.BOOP_REMINDER_CHANNEL_NAME);
               })
               .subscribe(c -> c.createMessage(s.toString()).subscribe());
    }
    
    @CommandAttributes.Command(
            helpText = "\uD83E\uDD13"
    )
    public static ResponseMessage nerd(MessageContext context, Long messageId) {
        Snowflake quoteMessage = Snowflake.of(messageId);
        
        String content = context.getChannel()
                                .getRestMessage(quoteMessage)
                                .getData()
                                .block()
                                .content();
    
        return new VanillaMessage("“" + content + "” \uD83E\uDD13");
    }
    
    @CommandAttributes.Command(
            helpText = "Get a string's binary representation"
    )
    public static ResponseMessage bin(MessageContext context, Sentence s) {
        byte[] sentenceBytes = s.toString()
                                .getBytes(StandardCharsets.UTF_8);
    
        StringBuilder value = new StringBuilder();
        
        for (byte b : sentenceBytes) {
            value.append(byteToString(b));
            value.append(' ');
        }
        
        return new VanillaMessage(value.toString());
    }
    
    // stackoverflow <3
    private static String byteToString(byte b) {
        byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }
    
}