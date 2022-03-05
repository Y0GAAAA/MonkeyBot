package commands.responses;

import discord4j.discordjson.json.ImmutableEmbedData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;

public class EmbedMessage extends ResponseMessage {

    public EmbedMessage(ImmutableEmbedData embed) {
        super(embed);
    }

    @Override
    public RestMessage send(RestChannel channel) {
        return sendEmbed(channel, (ImmutableEmbedData) getContent());
    }

}
