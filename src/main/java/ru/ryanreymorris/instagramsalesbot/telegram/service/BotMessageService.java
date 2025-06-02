package ru.ryanreymorris.instagramsalesbot.telegram.service;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Service for sending messages via telegram-bot.
 */
public interface BotMessageService {

    /**
     * Simple method for different sending
     *
     * @param method method to be sent
     * @param chatId receiver chatId
     */
    void sendPartialBotMethod(PartialBotApiMethod<?> method, Long chatId);

    /**
     * Send new message via telegram bot (do not update last message).
     *
     * @param sendMessage message to be sent.
     * @param update      user's update.
     */
    void sendNewMessageToUser(SendMessage sendMessage, Update update);

    /**
     * Send new message via telegram bot without user's message delete.
     *
     * @param sendMessage message to be sent.
     */
    void sendNewMessageToUser(SendMessage sendMessage);

    /**
     * Update last sent message via telegram bot.
     *
     * @param sendMessage message to be sent.
     * @param botUser     user.
     */
    void updateLastMessage(SendMessage sendMessage, BotUser botUser);

    void updateLastMessageWithPreview(SendMessage sendMessage, Update update);

    /**
     * Update last sent message via telegram bot.
     *
     * @param sendMessage message to be sent.
     * @param update      user's update.
     */
    void updateLastMessage(SendMessage sendMessage, Update update);

    /**
     * Update last sent message via telegram bot.
     *
     * @param sendContact contact to be sent.
     * @param update      user's update.
     */
    void updateLastMessage(SendContact sendContact, Update update);

    /**
     * Update last sent message via telegram bot.
     *
     * @param sendPhoto photo to be sent.
     * @param update    user's update.
     */
    void updateLastMessage(SendPhoto sendPhoto, Update update);

    /**
     * Delete user's message via telegram bot.
     *
     * @param chatId    chat id.
     * @param messageId message id.
     */
    void deleteUserMessage(Long chatId, Integer messageId);
}
