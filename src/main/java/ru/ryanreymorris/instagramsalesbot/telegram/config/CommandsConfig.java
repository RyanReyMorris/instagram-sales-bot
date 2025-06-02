package ru.ryanreymorris.instagramsalesbot.telegram.config;


import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.BotCommandEnum;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bot commands configuration.
 */
@Configuration
public class CommandsConfig {

    @Autowired
    private List<Command> commands;

    @Bean
    public Map<BotCommandEnum, Command> commands() {
        Map<BotCommandEnum, Command> botCommands = new HashMap<>();
        for (Command command : commands) {
            botCommands.put(command.getBotcommand(), command);
        }
        return botCommands;
    }
}
