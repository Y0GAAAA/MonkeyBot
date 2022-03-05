package commands.responses;

import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;

import java.text.MessageFormat;

public class CodeBlockMessage extends ResponseMessage {
    
    private final static String CODEBLOCK_PATTERN =
            "```{0}\n" +
            "{1}\n" +
            "```";
    
    private final String language;
    
    public CodeBlockMessage(String code, String language) {
        super(code);
        this.language = language;
    }
    
    @Override
    public RestMessage send(RestChannel channel) {
        final String text = MessageFormat.format(CODEBLOCK_PATTERN, language, (String)getContent());
        return sendText(channel, text);
    }
    
}