package commands.implementations;

import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.CodeBlockMessage;
import commands.responses.ErrorMessage;
import commands.responses.ResponseMessage;
import commands.responses.VanillaMessage;
import cosmetics.Static;
import discord4j.common.util.Snowflake;
import storage.DbUser;
import storage.UserFactory;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CreditsCommands {

    @CommandAttributes.Command(
            helpText = "Shows user balance"
    )
    public static ResponseMessage credits(MessageContext context) {
     
        final long credits = UserFactory.fromId(context.getUserId())
                                        .getCredits();
    
        return new VanillaMessage(
                "Current balance : " + Static.encapsulate(String.valueOf(credits), "`") + "\uD83D\uDCB5"
        );
    }

    public static final String TRANSFER_REPORT_TEMPLATE =
            "Transaction report\n\n" +
            "Source      : {0}\n" +
            "Destination : {1}\n" +
            "Amount : {2}\uD83D\uDCB5\n\n" +
            "Occurred at : {3}\n";
    
    @CommandAttributes.Command(
            helpText = "Give a specified amount of credits to an user"
    )
    public static ResponseMessage transfer(MessageContext context, Long credits, Snowflake destinationId) {
    
        final DbUser sourceUser = UserFactory.fromId(context.getUserId());
        
        if (credits > sourceUser.getCredits())
            return new ErrorMessage("insufficient funds");
        
        final DbUser destinationUser = UserFactory.fromId(destinationId);
    
        sourceUser.setCredits(sourceUser.getCredits() - credits);
        destinationUser.setCredits(destinationUser.getCredits() + credits);
        
        sourceUser.commit();
        destinationUser.commit();
    
        final String commitTimeString = LocalDateTime.now()
                                                     .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        final String transferReport = MessageFormat.format(
                TRANSFER_REPORT_TEMPLATE,
                context.getUserId().asString(),
                destinationId.asString(),
                String.valueOf(credits),
                commitTimeString
        );
        
        return new CodeBlockMessage(
                transferReport,
                "js"
        );
    }
    
    public static final int BOOP_REWARD = 25_000;
    
    @CommandAttributes.Command(
            helpText = "Boop on time and get a reward"
    )
    public static ResponseMessage boop(MessageContext context) {
        final LocalTime currentTime = LocalTime.now();
        
        if (currentTime.getHour() == currentTime.getMinute()) {
        
            final DbUser user = UserFactory.fromId(context.getUserId());
            
            user.setCredits(user.getCredits() + BOOP_REWARD)
                .commit();
            
            return new VanillaMessage("You have been rewarded " + BOOP_REWARD + " for booping.");
        } else {
            return new ErrorMessage("not currently in a boop time");
        }
    }
    
    @CommandAttributes.Command(
            helpText = "Display users with the most credits (TODO)"
    )
    public static ResponseMessage top(MessageContext context) {
        return new ErrorMessage("TODO");
    }
    
    @CommandAttributes.Command(
            helpText = "Give credits to a specified user"
    )
    @CommandAttributes.AdministratorCommand
    public static ResponseMessage give(MessageContext context, Long credits, Snowflake destinationId) {
        DbUser user = UserFactory.fromId(destinationId);
        
        user.setCredits(user.getCredits() + credits)
            .commit();
    
        return new VanillaMessage("Gave `" + credits + "` credits to " + destinationId.asString());
    }
    
}
