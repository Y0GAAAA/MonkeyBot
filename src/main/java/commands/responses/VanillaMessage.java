package commands.responses;

import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;

public class VanillaMessage extends ResponseMessage {

    public VanillaMessage(String text) {
        super(text);
    }

    @Override
    public RestMessage send(RestChannel channel) {
        return sendText(channel, (String) getContent());
    }

}
