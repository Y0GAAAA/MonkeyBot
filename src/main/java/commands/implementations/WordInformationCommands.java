package commands.implementations;

import commands.dependencies.synonym.DirtySynonymParser;
import commands.invocation.CommandAttributes;
import commands.invocation.MessageContext;
import commands.responses.ResponseMessage;
import commands.responses.ErrorMessage;
import commands.responses.VanillaMessage;
import cosmetics.Static;

import java.util.List;
import java.util.Objects;

public class WordInformationCommands {

    public static final DirtySynonymParser synonymEngine = new DirtySynonymParser();
    
    @CommandAttributes.Command(
            helpText = "Displays known synonyms for a specified english word"
    )
    @CommandAttributes.SingleInstance(
            mode = CommandAttributes.SingleInstanceMode.Guild
    )
    public static ResponseMessage synonyms(MessageContext context, String word) {
        if (word.isEmpty())
            return new ErrorMessage("Word can not be empty.");
    
        List<String> synonyms = synonymEngine.getSynonyms(word, DirtySynonymParser.Language.English);
        
        if (Objects.isNull(synonyms) || synonyms.isEmpty())
            return new ErrorMessage("No synonyms were found for " + Static.encapsulate(word, "`"));
        
        String body = "Found " + Static.encapsulate(String.valueOf(synonyms.size()), "`") + " synonyms for " + Static.encapsulate(word, "`") + " :\n\n" + String.join("\n", synonyms);
    
        return new VanillaMessage(body);
    }
    
}