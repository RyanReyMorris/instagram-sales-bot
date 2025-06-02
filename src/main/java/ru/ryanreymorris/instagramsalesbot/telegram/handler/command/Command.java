package ru.ryanreymorris.instagramsalesbot.telegram.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Command interface for handling telegram-bot commands.
 */
public interface Command {

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided object with all the needed data for command.
     */
    void handleCommand(Update update);

    /**
     * Get command type.
     *
     * @return bot command.
     */
    BotCommandEnum getBotcommand();
}
