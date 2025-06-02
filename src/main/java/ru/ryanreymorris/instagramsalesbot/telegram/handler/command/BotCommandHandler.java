package ru.ryanreymorris.instagramsalesbot.telegram.handler.command;

import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateHandler;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateType;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.BotCommandEnum;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Handler of bot commands action.
 */
@Component
public class BotCommandHandler implements UpdateHandler {

    @Lazy
    @Autowired
    private Map<BotCommandEnum, Command> commands;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Update update) {
        BotCommandEnum command = BotCommandEnum.findByCommandName(update.getMessage().getText());
        commands.get(command).handleCommand(update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateType getUpdateHandlerType() {
        return UpdateType.BOT_COMMAND;
    }
}
