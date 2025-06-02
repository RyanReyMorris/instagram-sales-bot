package ru.ryanreymorris.instagramsalesbot.telegram.exception;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Service for telegram api exceptions processing.
 */
public interface TelegramApiExceptionProcessService {

    /**
     * Process telegram-api exception.
     */
    void processTelegramApiException(PartialBotApiMethod<?> methodObject, TelegramApiException exception);
}
