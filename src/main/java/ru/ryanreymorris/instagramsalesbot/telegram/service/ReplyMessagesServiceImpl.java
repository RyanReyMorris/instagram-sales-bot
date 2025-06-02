package ru.ryanreymorris.instagramsalesbot.telegram.service;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Implementation of {@link ReplyMessagesService} interface.
 */
@Service
public class ReplyMessagesServiceImpl implements ReplyMessagesService {

    /**
     * {@inheritDoc}
     */
    @Override
    public SendPhoto createMessageWithPhoto(byte[] photo, String caption, Long chatId) {
        InputStream inputStream = new ByteArrayInputStream(photo);
        InputFile file = new InputFile(inputStream, chatId.toString());
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(file);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(createCommonMessage(caption, chatId).getText());
        return sendPhoto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SendMessage createMessage(String messageText, Long chatId) {
        return createCommonMessage(messageText, chatId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SendMessage createMessageWithButtons(String messageText, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = createMessage(messageText, chatId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SendMessage createMessageWithMenuButtons(String messageText, Long chatId, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = createMessage(messageText, chatId);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    /**
     * Create {@link SendMessage} object from string for special customer.
     *
     * @param text-  text of message.
     * @param chatId - id of customer chat.
     * @return {@link SendMessage}.
     */
    private SendMessage createCommonMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(EmojiParser.parseToUnicode(text));
        return sendMessage;
    }
}
