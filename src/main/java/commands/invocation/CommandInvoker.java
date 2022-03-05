package commands.invocation;

import commands.implementations.*;
import commands.invocation.CommandAttributes.Command;
import commands.invocation.CommandAttributes.SingleInstance;
import commands.invocation.CommandAttributes.AdministratorCommand;

import commands.invocation.pre.CommandContainerStaticInitializer;
import commands.responses.ResponseMessage;
import commands.responses.ErrorMessage;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import parsing.DiscordCommandParser;
import parsing.exceptions.*;
import parsing.exceptions.internal.AggregateException;
import parsing.exceptions.reportable.InsufficientPermissionException;
import parsing.exceptions.reportable.ParallelExecutionAttemptException;
import reflection.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class CommandInvoker {

    private static final Class<?>[] COMMAND_CONTAINERS = new Class[]{
            TextManipulationCommands.class,
            PuzzleCommands.class,
            CosmeticCommands.class,
            MemberMutationCommands.class,
            HelpCommands.class,
            WordInformationCommands.class,
            GameCommands.class,
            TiktokCommands.class,
            CreditsCommands.class,
            AdministrationCommands.class,
    };
    
    public static final Method[] CALLBACK_METHODS = Util.getMethodsFromClasses(COMMAND_CONTAINERS)
                                                        .filter(m -> m.isAnnotationPresent(Command.class))
                                                        .toArray(Method[]::new);
    
    public static Optional<Method> getMethodFromName(String methodName) {
        for (Method callbackMethod : CALLBACK_METHODS) {
            if (callbackMethod.getName().equals(methodName)) {
                return Optional.of(callbackMethod);
            }
        }
        return Optional.empty();
    }
    
    private static final DiscordCommandParser COMMAND_PARSER = new DiscordCommandParser();
    private static final MultipleValuesHashMap<Method, Snowflake> METHOD_SIMULTANEOUS_CALLER_MAP = new MultipleValuesHashMap<Method, Snowflake>(CALLBACK_METHODS);

    public static void InitializeStaticCommandContainers() {
        for (Class<?> commandContainer : COMMAND_CONTAINERS) {
            CommandContainerStaticInitializer.InitializeStatic(commandContainer);
        }
    }
    public static void ValidateCallbackSignatures() throws AggregateException {
        AggregateException signatureExceptions = new AggregateException();
        
        for (Method m : CALLBACK_METHODS)
            signatureExceptions.addIfThrowing(() -> DiscordCommandParser.ArgumentParser.validateMethod(m));
    
        if (!signatureExceptions.isEmpty())
            throw signatureExceptions;
    }
    
    public static void invoke(MessageCreateEvent event, String commandName, String arguments) {
        final MessageContext context = new MessageContext(event);
        
        getMethodFromName(commandName)
              .ifPresent(method -> {
                    boolean alreadyRunning = false;
                    try {
                        if (
                            method.isAnnotationPresent(AdministratorCommand.class)
                            &&
                            !context.getDbUser().getIsAdmin()
                        ){
                            throw InsufficientPermissionException.notAdmin();
                        }
                        
                        if (method.isAnnotationPresent(SingleInstance.class)) {
                            switch (method.getAnnotation(SingleInstance.class).mode()) {

                                case Guild:
                                    alreadyRunning = METHOD_SIMULTANEOUS_CALLER_MAP.contains(method, context.getGuildId());
                                    break;
                                    
                                case Global:
                                    alreadyRunning = METHOD_SIMULTANEOUS_CALLER_MAP.anyFor(method);
                                    break;
                            }

                            if (alreadyRunning) {
                                throw new ParallelExecutionAttemptException(method);
                            }
                        }

                        Parameter[] parameters = Arrays.copyOfRange(method.getParameters(), 1, method.getParameters().length);

                        Object[] objectifiedArguments = COMMAND_PARSER.parseFunctionArgumentsFromCommandString(
                                arguments,
                                parameters
                        );

                        Object[] finalArgumentsWithContext = new Object[objectifiedArguments.length + 1];

                        finalArgumentsWithContext[0] = context;

                        System.arraycopy(
                                objectifiedArguments,
                                0,
                                finalArgumentsWithContext,
                                1,
                                objectifiedArguments.length
                        );

                        METHOD_SIMULTANEOUS_CALLER_MAP.add(method, context.getGuildId());

                        Object response = method.invoke(null, finalArgumentsWithContext);

                        if (method.getReturnType().equals(ResponseMessage.class) && Objects.nonNull(response)) {
                            context.send((ResponseMessage) response);
                        }

                    } catch (InvocationTargetException | ReportableException ex) {
                        
                        final Throwable cause = ex.getCause();
                        final Class<?> causeClass = cause.getClass().getSuperclass();
    
                        // TODO: 25/07/2021 CHECK IF CAUSE HAS SUPERCLASS, NULLEXCEPTION GETS RAISED HERE
                        
                        if (Objects.nonNull(causeClass) && causeClass.equals(ReportableException.class)) {
                            context.send(new ErrorMessage((Exception) cause));
                        } else {
                            ex.printStackTrace();
                            context.send(new ErrorMessage("internal exception"));
                        }

                    } catch (IllegalAccessException | IllegalArgumentException ignored) {
                        throw new IllegalStateException("unreachable");
                    } finally {
                        if (!alreadyRunning) {
                            METHOD_SIMULTANEOUS_CALLER_MAP.remove(method, context.getGuildId());
                        }
                    }
                });
    }

}