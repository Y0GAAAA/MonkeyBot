package commands.implementations;

import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.ErrorMessage;
import commands.responses.ResponseMessage;
import commands.responses.VanillaMessage;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import parsing.arguments.Sentence;

import java.util.List;
import java.util.Objects;

public class MemberMutationCommands {
    
    @CommandAttributes.Command(
            helpText = "Rename all non-administrator members to the specified username"
    )
    @CommandAttributes.SingleInstance(
            mode = CommandAttributes.SingleInstanceMode.Guild
    )
    public static ResponseMessage renameall(MessageContext context, Sentence username) {
        int renamedCount = 0;
    
        List<Member> members = context.getGuild()
                                      .requestMembers()
                                      .collectList()
                                      .block();
        
        if (Objects.isNull(members))
            return new ErrorMessage("could not retrieve users");
        
        for (Member m : members) {
            try {
                m.edit(member -> member.setNickname(username.toString())).block();
                renamedCount++;
            } catch (Exception ignored) { /* */ }
        }
        return new VanillaMessage("Renamed " + renamedCount + " members.");
    }
    
    @CommandAttributes.Command(
            helpText = "Rename a member to the specified username"
    )
    @CommandAttributes.AdministratorCommand
    public static void rename(MessageContext context, Snowflake id, Sentence username) {
        context.getGuild()
               .getMemberById(id)
               .subscribe(m ->
                       m.edit(member -> member.setNickname(username.toString()))
                        .subscribe()
               );
    }
    
}
