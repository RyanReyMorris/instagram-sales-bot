package ru.ryanreymorris.instagramsalesbot.telegram.service;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.TelegramSystemBot;
import ru.ryanreymorris.instagramsalesbot.telegram.config.ApplicationContextProvider;
import ru.ryanreymorris.instagramsalesbot.telegram.exception.TelegramApiExceptionProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Implementation of {@link BotMessageService} interface.
 */
@Service
public class BotMessageServiceImpl implements BotMessageService {

    private static final Logger logger = LoggerFactory.getLogger(BotMessageServiceImpl.class);

    @Autowired
    private BotUserRepository botUserRepository;

    @Lazy
    @Autowired
    private TelegramApiExceptionProcessService exceptionProcessService;

    @Override
    public void sendPartialBotMethod(PartialBotApiMethod<?> method, Long chatId) {
        TelegramSystemBot telegramSystemBot = getBot();
        try {
            if (method.getClass().equals(SendMessage.class)) {
                ((SendMessage) method).setChatId(chatId);
                telegramSystemBot.execute((SendMessage) method);
            } else if (method.getClass().equals(SendPhoto.class)) {
                ((SendPhoto) method).setChatId(chatId);
                telegramSystemBot.execute((SendPhoto) method);
            } else if (method.getClass().equals(SendVideo.class)) {
                ((SendVideo) method).setChatId(chatId);
                telegramSystemBot.execute((SendVideo) method);
            } else if (method.getClass().equals(SendVideoNote.class)) {
                ((SendVideoNote) method).setChatId(chatId);
                telegramSystemBot.execute((SendVideoNote) method);
            } else if (method.getClass().equals(SendVoice.class)) {
                ((SendVoice) method).setChatId(chatId);
                telegramSystemBot.execute((SendVoice) method);
            } else {
                logger.error("Неизвестный тип отправляемого сообщения");
            }
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке метода пользователю", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastMessage(SendMessage sendMessage, BotUser botUser) {
        TelegramSystemBot telegramSystemBot = getBot();
        BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendMessage.getChatId()));
        if (user.getLastMessage() == null) {
            sendNewMessageToUser(sendMessage);
        } else {
            EditMessageText editMessageText = new EditMessageText();
            try {
                editMessageText.setChatId(botUser.getTgUserId().toString());
                editMessageText.setMessageId(user.getLastMessage());
                editMessageText.setText(sendMessage.getText());
                editMessageText.setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup());
                telegramSystemBot.execute(editMessageText);
            } catch (TelegramApiException exception) {
                exceptionProcessService.processTelegramApiException(editMessageText, exception);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastMessageWithPreview(SendMessage sendMessage, Update update) {
        TelegramSystemBot telegramSystemBot = getBot();
        BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendMessage.getChatId()));
        if (user == null || user.getLastMessage() == null) {
            sendNewMessageToUser(sendMessage, update);
        } else {
            EditMessageText editMessageText = new EditMessageText();
            Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
            try {
                editMessageText.setChatId(message.getChatId().toString());
                editMessageText.setMessageId(user.getLastMessage());
                editMessageText.setText(sendMessage.getText());
                editMessageText.setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup());
                telegramSystemBot.execute(editMessageText);
            } catch (TelegramApiException exception) {
                exceptionProcessService.processTelegramApiException(editMessageText, exception);
            } finally {
                if (!update.hasCallbackQuery()) {
                    deleteUserMessage(message.getChatId(), message.getMessageId());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastMessage(SendMessage sendMessage, Update update) {
        TelegramSystemBot telegramSystemBot = getBot();
        BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendMessage.getChatId()));
        if (user == null || user.getLastMessage() == null) {
            sendNewMessageToUser(sendMessage, update);
        } else {
            EditMessageText editMessageText = new EditMessageText();
            Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
            try {
                editMessageText.setChatId(message.getChatId().toString());
                editMessageText.setMessageId(user.getLastMessage());
                editMessageText.setText(sendMessage.getText());
                editMessageText.setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup());
                editMessageText.disableWebPagePreview();
                telegramSystemBot.execute(editMessageText);
            } catch (TelegramApiException exception) {
                exceptionProcessService.processTelegramApiException(editMessageText, exception);
            } finally {
                if (!update.hasCallbackQuery()) {
                    deleteUserMessage(message.getChatId(), message.getMessageId());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastMessage(SendContact sendContact, Update update) {
        TelegramSystemBot telegramSystemBot = getBot();
        Integer messageId;
        Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
        try {
            BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendContact.getChatId()));
            messageId = telegramSystemBot.execute(sendContact).getMessageId();
            user.setLastMessage(messageId);
            botUserRepository.save(user);
        } catch (TelegramApiException exception) {
            exceptionProcessService.processTelegramApiException(sendContact, exception);
        } finally {
            deleteUserMessage(message.getChatId(), message.getMessageId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLastMessage(SendPhoto sendPhoto, Update update) {
        TelegramSystemBot telegramSystemBot = getBot();
        Integer messageId;
        Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
        try {
            BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendPhoto.getChatId()));
            messageId = telegramSystemBot.execute(sendPhoto).getMessageId();
            deleteUserMessage(message.getChatId(), user.getLastMessage());
            user.setLastMessage(messageId);
            botUserRepository.save(user);
        } catch (TelegramApiException exception) {
            exceptionProcessService.processTelegramApiException(sendPhoto, exception);
        } finally {
            deleteUserMessage(message.getChatId(), message.getMessageId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNewMessageToUser(SendMessage sendMessage, Update update) {
        TelegramSystemBot telegramSystemBot = getBot();
        Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
        BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendMessage.getChatId()));
        try {
            Integer messageId = telegramSystemBot.execute(sendMessage).getMessageId();
            if (user != null) {
                user.setLastMessage(messageId);
                botUserRepository.save(user);
            }
        } catch (TelegramApiException exception) {
            exceptionProcessService.processTelegramApiException(sendMessage, exception);
        } finally {
            deleteUserMessage(message.getChatId(), message.getMessageId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNewMessageToUser(SendMessage sendMessage) {
        TelegramSystemBot telegramSystemBot = getBot();
        try {
            BotUser user = botUserRepository.findByTgUserId(Long.parseLong(sendMessage.getChatId()));
            Integer messageId = telegramSystemBot.execute(sendMessage).getMessageId();
            user.setLastMessage(messageId);
            botUserRepository.save(user);
        } catch (TelegramApiException exception) {
            exceptionProcessService.processTelegramApiException(sendMessage, exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUserMessage(Long chatId, Integer messageId) {
        TelegramSystemBot telegramSystemBot = getBot();
        DeleteMessage deleteMessage = new DeleteMessage();
        try {
            deleteMessage.setChatId(chatId.toString());
            deleteMessage.setMessageId(messageId);
            telegramSystemBot.execute(deleteMessage);
        } catch (TelegramApiException exception) {
            exceptionProcessService.processTelegramApiException(deleteMessage, exception);
        }
    }

    /**
     * Get Bot. Resolve cyclic dependencies of beans.
     *
     * @return OrderEscortBot bean.
     */
    private TelegramSystemBot getBot() {
        return ApplicationContextProvider.getApplicationContext().getBean(TelegramSystemBot.class);
    }
}