package commands.implementations;

import com.google.common.base.Suppliers;
import commands.invocation.CommandAttributes;
import commands.invocation.CommandInvoker;
import commands.invocation.MessageContext;
import commands.responses.CodeBlockMessage;
import commands.responses.ResponseMessage;
import commands.responses.EmbedMessage;
import discord4j.discordjson.json.*;
import parsing.Constants;
import parsing.exceptions.ReportableException;
import parsing.exceptions.reportable.MethodNotFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommands {

    public static final String ADMINISTRATOR_COMMAND_PREFIX = "(\uD83D\uDD12)";
    
    @CommandAttributes.Command(
            helpText = "Displays this help message"
    )
    public static ResponseMessage help(MessageContext context) {
        final ArrayList<EmbedFieldData> helpTexts = new ArrayList<>();
        
        for (Method callableMethod : CommandInvoker.CALLBACK_METHODS) {
            CommandAttributes.Command defAnnotation = callableMethod.getAnnotation(CommandAttributes.Command.class);
            
            if (defAnnotation == null) {
                continue;
            }
    
            String command = Constants.PREFIX + callableMethod.getName();
            
            if (callableMethod.isAnnotationPresent(CommandAttributes.AdministratorCommand.class)) {
                command = ADMINISTRATOR_COMMAND_PREFIX + ' ' + command;
            }
            
            helpTexts.add(EmbedFieldData.builder()
                    .name(command)
                    .value(defAnnotation.helpText())
                    .inline(false)
                    .build()
            );
        }
    
        ImmutableEmbedFooterData footer = EmbedFooterData.builder()
                                                         .text(ADMINISTRATOR_COMMAND_PREFIX + " administrator command")
                                                         .build();
        
        ImmutableEmbedData helpEmbed = EmbedData.builder()
                .title("Available commands :")
                .addAllFields(helpTexts)
                .footer(footer)
                .build();

        return new EmbedMessage(helpEmbed);
    }

    @CommandAttributes.Command(
            helpText = "Retrieve a command full definition"
    )
    public static ResponseMessage definition(MessageContext context, String commandName) throws ReportableException {
        final Method method = CommandInvoker.getMethodFromName(commandName)
                                            .orElseThrow(
                                                    Suppliers.ofInstance(new MethodNotFoundException(commandName))
                                            );
        
        final Parameter[] parameters = method.getParameters();
        final String paramString = Arrays.stream(parameters)
                                         .skip(1)
                                         .map(p -> getTypeName(p) + ' ' + p.getName())
                                         .collect(Collectors.joining(", "));
    
        final String simplifiedDefinition = "void " + commandName + "(" + paramString + ");";
        
        return new CodeBlockMessage(simplifiedDefinition, "java");
    }
    
    private static String getTypeName(Parameter p) {
        final String[] packages = p.getType().getTypeName().split("\\.");
        return packages[packages.length - 1];
    }
    
}
