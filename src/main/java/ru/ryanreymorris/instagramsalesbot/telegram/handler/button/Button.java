package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Button interface for handling telegram-bot button clicks.
 */
public interface Button {
    /**
     * Main method, which is executing button click logic.
     *
     * @param update provided object with all the needed data for button.
     */
    void handleClick(Update update);

    /**
     * Get button type.
     *
     * @return button type.
     */
    ButtonEnum getButton();
}
