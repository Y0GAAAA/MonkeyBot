package commands.responses;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.ImmutableEmbedData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;

public abstract class ResponseMessage {

    private final Object content;

    ResponseMessage(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }

    public abstract RestMessage send(RestChannel channel);

    static RestMessage sendEmbed(RestChannel channel, ImmutableEmbedData embed) {
        Snowflake messageId = Snowflake.of(
            channel.createMessage(embed)
                    .block()
                    .id()
        );
    
        return channel.getRestMessage(messageId);
    }
    static RestMessage sendText(RestChannel channel, String text) {
        Snowflake messageId = Snowflake.of(
                channel.createMessage(text)
                       .block()
                       .id()
        );
        return channel.getRestMessage(messageId);
    }

}