package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateButton implements Button {

    @Autowired
    private StartCommand startCommand;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleClick(Update update) {
        startCommand.handleCommand(update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ButtonEnum getButton() {
        return ButtonEnum.UPDATE;
    }
}
