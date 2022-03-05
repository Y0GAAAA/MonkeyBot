import commands.invocation.CommandInvoker;

import panic.ExitCodes;
import panic.Panicker;
import parsing.Constants;
import cosmetics.Clantag;
import cosmetics.Static;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.discordjson.json.EmbedData;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.entity.RestChannel;
import parsing.exceptions.internal.AggregateException;
import reactor.core.publisher.Flux;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Program {

    private static final List<RestChannel> boopReminderChannels = new ArrayList<>();
    private static final Executor callbackTaskExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        
        final String discordBotToken;
        try {
            discordBotToken = args[0];
            System.out.println("Found token : " + discordBotToken);
        } catch (Exception ex) {
            new Panicker("No token provided as argument", ExitCodes.MISSING_TOKEN).panic();
            throw ex; // unreachable, silence the compiler about discordBotToken potentially being null
        }
        
        final DiscordClient client = DiscordClient.create(discordBotToken);

        final GatewayDiscordClient gateway = client.gateway()
                .setEnabledIntents(IntentSet.of(
                        Intent.GUILD_MEMBERS,
                        Intent.GUILD_MESSAGES,
                        Intent.DIRECT_MESSAGES
                ))
                .login()
                .block();
        
        System.out.println("Successfully logged-in.");
        
        Objects.requireNonNull(gateway);

        SetupHelpStatus(gateway);
        SetupBoopReminderChannels(gateway);
        
        // boop reminder thread
        new Thread(() -> {
            while (true) {
                LocalTime boopTime = WaitUntilNextBoopTime();
                EmbedData embedMessage = GetBoopNotificationEmbedMessageData(boopTime);

                for (RestChannel channel : boopReminderChannels) {
                    channel.createMessage(embedMessage).subscribe();
                }

                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException ignored) {}
            }
        }).start();

        // animated status thread
        new Thread(() -> {
            String[] frames = Clantag.getCycle("Booping");

            while (true) {
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException ignored) {}

                for (String frame : frames) {
                    UpdateStatusText(gateway, frame);
                    try {
                        Thread.sleep(4_000);
                    } catch (InterruptedException ignored) {}
                }
                SetupHelpStatus(gateway);
            }
        }).start();
        
        try {
            CommandInvoker.ValidateCallbackSignatures();
            System.out.println("All callback methods signatures were verified and valid.");
        } catch (AggregateException ex) {
            ex.printExceptionMessages();
        }
        
        CommandInvoker.InitializeStaticCommandContainers();
    
        System.out.println("Initialized static command containers fields.");
        
        gateway.on(MessageCreateEvent.class)
               .subscribe(event -> {

                    if (event.getMember().get().isBot()) //TODO improve anti injection mechanism
                        return;
                   
                    Message message = event.getMessage();
                    String messageContent = message.getContent();

                    if (messageContent.startsWith(Constants.PREFIX)) {

                        int commandEnd = messageContent.indexOf(' ');

                        if (commandEnd == -1) {
                            commandEnd = messageContent.length();
                        }

                        final String commandName = messageContent.substring(Constants.PREFIX_LENGTH, commandEnd);
                        final String arguments = messageContent.substring(commandEnd).trim();

                        System.out.println("Received command : " + commandName + "\n↳ Argument string : " + arguments + "\n");

                        callbackTaskExecutor.execute(
                                () -> CommandInvoker.invoke(
                                        event,
                                        commandName,
                                        arguments
                                )
                        );

                    }

                });
        
        gateway.onDisconnect().block();

    }

    private static void UpdateStatusText(GatewayDiscordClient client, String text) {
        client.updatePresence(Presence.online(Activity.playing(text))).block();
    }

    private static void SetupHelpStatus(GatewayDiscordClient client) {
        UpdateStatusText(client, "Use " + Constants.PREFIX + "help");
    }

    private static void SetupBoopReminderChannels(GatewayDiscordClient client) {
        client.getGuilds()
                .doOnNext(guild -> {
                    GuildChannel boopChannel = guild
                            .getChannels()
                            .filter(channel -> channel.getName().equals(Constants.BOOP_REMINDER_CHANNEL_NAME))
                            .blockFirst();

                    if (boopChannel == null) {
                        System.out.println("Boop reminder channel doesn't exist yet on " + guild.getName() + ". Create a new text channel named " + Constants.BOOP_REMINDER_CHANNEL_NAME + ".");
                    } else {
                        boopReminderChannels.add(boopChannel.getRestChannel());
                    }

                }).subscribe();

    }

    private static LocalTime WaitUntilNextBoopTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDate currentDate = currentTime.toLocalDate();

        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();

        LocalDateTime sleepUntil;

        if (minute > hour) { // if it already happened this hour

            System.out.println("Already happened this hour.");

            if (hour == 23) {
                sleepUntil = LocalDateTime.of(currentDate.plusDays(1), LocalTime.of(0, 0));
            } else {
                sleepUntil = LocalDateTime.of(currentDate, LocalTime.of(hour + 1, hour + 1));
            }

        } else {
            System.out.println("Will happen this hour.");
            sleepUntil = LocalDateTime.of(currentDate, LocalTime.of(hour, hour));
        }

        sleepUntil = sleepUntil.plusSeconds(2); // add few seconds because it tends to sleep for a too short period of time

        System.out.println("Will sleep until : " + sleepUntil.toString());

        long sleepTime = currentTime.until(sleepUntil, ChronoUnit.MILLIS);

        System.out.println("Will sleep for " + sleepTime + "ms.");

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignored) {
            System.err.println("Thread got interrupted while sleeping until next boop time.");
        }

        return sleepUntil.toLocalTime();
    }

    private static EmbedData GetBoopNotificationEmbedMessageData(LocalTime boopTime) {
        return EmbedData.builder()
                .title(boopTime.format(DateTimeFormatter.ofPattern("mm:mm")))
                .description("BOOP TIME \uD83D\uDC43\n@here")
                .build();
    }

}