package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;


import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateHandler;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Handler of button click action.
 */
@Component
public class ButtonClickHandler implements UpdateHandler {

    @Autowired
    private Map<ButtonEnum, Button> buttons;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Update update) {
        String buttonData = update.getCallbackQuery().getData();
        buttons.get(ButtonEnum.valueOf(buttonData)).handleClick(update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateType getUpdateHandlerType() {
        return UpdateType.BUTTON_CLICK;
    }
}
