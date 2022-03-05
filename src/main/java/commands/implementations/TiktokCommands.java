package commands.implementations;

import commands.dependencies.tiktok.TiktokVideoUrlResolver;
import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.EmbedMessage;
import commands.responses.ErrorMessage;
import commands.responses.ResponseMessage;
import commands.responses.VanillaMessage;
import discord4j.discordjson.json.*;
import discord4j.discordjson.possible.Possible;

import java.net.URL;
import java.util.Objects;

public class TiktokCommands {

    public static final TiktokVideoUrlResolver tiktokResolver = new TiktokVideoUrlResolver();
    
    //@CommandAttributes.Command(
    //        helpText = "Embeds a tiktok video"
    //)
    public static ResponseMessage tiktok(MessageContext context, URL url) {
        String videoUrl = tiktokResolver.resolve(url);
        
        if (Objects.isNull(videoUrl) || videoUrl.isEmpty()) {
            return new ErrorMessage("invalid tiktok url");
        }
        
        System.out.println("Resolved url : " + videoUrl);
        
        ImmutableEmbedVideoData videoData = EmbedVideoData.builder()
                                                          .url(videoUrl)
                                                          .build();
    
        ImmutableEmbedData videoEmbed = EmbedData.builder()
                                                 .url(videoUrl)
                                                 .build();
        
        return new EmbedMessage(videoEmbed);
    }
    
}
