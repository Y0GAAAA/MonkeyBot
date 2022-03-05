package commands.implementations;

import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.*;
import cosmetics.Static;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFieldData;
import discord4j.discordjson.json.ImmutableEmbedData;
import discord4j.rest.http.client.ClientException;
import parsing.arguments.Sentence;
import storage.Database;
import storage.DbUser;
import storage.UserFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

public class AdministrationCommands {

    @CommandAttributes.Command(
            helpText = "Ban a user"
    )
    @CommandAttributes.AdministratorCommand
    public static ResponseMessage ban(MessageContext context, Snowflake userId) {
    
        final Member user = context.getGuild()
                                   .getMemberById(userId)
                                   .block();
        
        user.ban(r -> r.setDeleteMessageDays(7))
            .block();
               
        final String username = user.getNickname()
                                    .orElse("<unknown>");
        
        return new VanillaMessage("Banned " + username + " (id:" + userId.asString() + "), bye bye \uD83D\uDC4B");
        
    }
    
    @CommandAttributes.Command(
            helpText = "Ban silently \uD83D\uDC0D"
    )
    @CommandAttributes.AdministratorCommand
    public static void ban_silent(MessageContext context, Snowflake userId) {
        
        context.getMessage()
               .delete()
               .block();
        
        final Member user = context.getGuild()
                .getMemberById(userId)
                .block();
        
        user.ban(r -> r.setDeleteMessageDays(7))
            .block();
        
    }
    
    @CommandAttributes.Command(
            helpText = "Sets the admin state for a specified user"
    )
    @CommandAttributes.AdministratorCommand
    public static ResponseMessage set_admin(MessageContext context, Snowflake id, Boolean value) {
    
        UserFactory.fromId(id)
                   .setIsAdmin(value)
                   .commit();
    
        return new VanillaMessage("Set admin for *" + id.asString() + "* to `" + value.toString() + "`");
        
    }
    
    @CommandAttributes.Command(
        helpText = "List users that can access the bot administrator commands"
    )
    public static ResponseMessage admins(MessageContext context) {
    
        final DbUser[] administrators = UserFactory.fromWhereClause("admin=1");
    
        final ImmutableEmbedData.Builder embedBuilder = EmbedData.builder()
                                                                 .title("Administrators :");
        
        Arrays.stream(administrators).map(DbUser::getId)
                                     .map(Snowflake::of)
                                     .map(id -> {
                                             try {
                                                 return context.getGuild()
                                                               .getMemberById(id)
                                                               .block();
                                             } catch (ClientException ignored) { return null; }
                                         })
                                     .filter(Objects::nonNull)
                                     .forEach(member -> {
                                     
                                         final String username = member.getUsername();
                                         final String id = member.getId().asString();
                                         
                                         final EmbedFieldData field = EmbedFieldData.builder()
                                                                                    .name(username)
                                                                                    .value(Static.encapsulate(id, "*"))
                                                                                    .build();
                                         
                                         embedBuilder.addField(field);
                                         
                                     });
        
        return new EmbedMessage(embedBuilder.build());
        
    }
    
    @CommandAttributes.Command(
            helpText = "Query User Database"
    )
    // very unsafe but just wanted to do as much as possible
    public static ResponseMessage qud(MessageContext context, Sentence queryString) {
    
        final String result = Database.withStatement(statement -> {
            
            try {
                
                StringBuilder queryResultString = new StringBuilder();
    
                ResultSet queryResult = statement.executeQuery(queryString.toString());
    
                while (queryResult.next()) {
        
                    final DbUser user = new DbUser(queryResult);
    
                    queryResultString.append(user.toString());
                    queryResultString.append('\n');
        
                }
                
                return queryResultString.toString();
                
            } catch (SQLException ignored) {
                return null;
            }
            
        });
        
        if (Objects.isNull(result)) {
            return new ErrorMessage("invalid SQL Server query");
        }
        
        return new CodeBlockMessage(result, "json");
        
    }
    
}