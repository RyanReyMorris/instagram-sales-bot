package ru.ryanreymorris.instagramsalesbot.telegram.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Creates {@link SendMessage} object in response to Customer actions.
 */
public interface ReplyMessagesService {

    /**
     * Create an outgoing message.
     *
     * @param photo   - photo as byte array.
     * @param caption - caption of photo.
     * @param chatId  - id of customer chat.
     * @return {@link SendPhoto}.
     */
    SendPhoto createMessageWithPhoto(byte[] photo, String caption, Long chatId);

    /**
     * Create an outgoing message.
     *
     * @param messageText - text of message.
     * @param chatId      - id of customer chat.
     * @return {@link SendMessage}.
     */
    SendMessage createMessage(String messageText, Long chatId);

    /**
     * Create message with inline buttons.
     *
     * @param messageText          - test of message.
     * @param chatId               - id of customer chat.
     * @param inlineKeyboardMarkup - buttons.
     * @return {@link SendMessage}
     */
    SendMessage createMessageWithButtons(String messageText, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup);

    /**
     * Метод получения сообщения с кнопками главного меню.
     *
     * @param messageText         - test of message.
     * @param chatId              - id of customer chat.
     * @param replyKeyboardMarkup - menu buttons.
     * @return {@link SendMessage}
     */
    SendMessage createMessageWithMenuButtons(String messageText, Long chatId, ReplyKeyboardMarkup replyKeyboardMarkup);

}
