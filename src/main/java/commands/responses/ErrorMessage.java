package commands.responses;

import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;

public class ErrorMessage extends ResponseMessage {

    public ErrorMessage(String errorMessage) {
        super(errorMessage);
    }
    
    // TODO: 25/07/2021 BETTER ERROR DISPLAY WHEN COMING FROM THE EXCEPTION CONSTRUCTOR ESPECIALLY
    
    public ErrorMessage(Exception ex) {
        super(ex.getClass().getTypeName() + " : " +  ex.getMessage());
    }
    
    private String getErrorString() {
        return "❌ **" + getContent() + "**";
    }

    @Override
    public RestMessage send(RestChannel channel) {
        return sendText(channel, getErrorString());
    }

}