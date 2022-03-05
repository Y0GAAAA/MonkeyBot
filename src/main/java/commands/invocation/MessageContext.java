package commands.invocation;

import commands.responses.ResponseMessage;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;
import storage.DbUser;
import storage.UserFactory;

import java.util.Objects;

public class MessageContext {

    private final RestChannel messageChannel;
    private final Snowflake messageGuildId;
    private final Guild messageGuild;
    private final Message message;
    private final Snowflake userId;
    private final DbUser dbUser;
    
    MessageContext(MessageCreateEvent messageEvent) {
        messageChannel = messageEvent.getMessage()
                                     .getRestChannel();
        
        messageGuild = messageEvent.getGuild()
                                   .block();
        
        messageGuildId = Objects.requireNonNull(messageGuild.getId());
    
        message = messageEvent.getMessage();
        
        userId = message.getAuthor()
                        .get()
                        .getId();
            
        dbUser = UserFactory.fromId(userId);
    }
    
    public Snowflake getGuildId() {
        return messageGuildId;
    }
    public Guild getGuild() { return messageGuild; }
    public Message getMessage() { return message; }
    public RestChannel getChannel() { return messageChannel; }
    public Snowflake getUserId() { return userId; }
    public DbUser getDbUser() { return dbUser; }
    
    public RestMessage send(ResponseMessage message) {
        return message.send(this.messageChannel);
    }
    
}
