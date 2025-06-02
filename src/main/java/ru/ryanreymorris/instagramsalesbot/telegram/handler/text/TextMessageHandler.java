package ru.ryanreymorris.instagramsalesbot.telegram.handler.text;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateHandler;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateType;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.ButtonEnum;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.ButtonKeyboard;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import ru.ryanreymorris.instagramsalesbot.telegram.storage.MassSendingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

/**
 * Handler of text message action.
 */
@Component
public class TextMessageHandler implements UpdateHandler {

    private static final String CONFIRM_MESSAGE = "Подтвердите массовую отправку сообщения:";

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private StartCommand startCommand;
    @Autowired
    private ReplyMessagesService replyMessagesService;
    @Autowired
    private MassSendingStorage massSendingStorage;
    @Autowired
    private BotMessageService botMessageService;


    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void handle(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        if (!botUser.isAdmin() || botUser.getActiveButton() == null) {
            return;
        }
        String messageText = update.getMessage().getText();
        String errorMessage = null;
        switch (botUser.getActiveButton()) {
            case POST_URL -> {
                Optional<Property> property = propertyRepository.findById(Properties.POST_URL);
                if (property.isPresent()) {
                    Property newPostUrl = property.get();
                    newPostUrl.setValue(messageText);
                    propertyRepository.save(newPostUrl);
                    Optional<Property> postId = propertyRepository.findById(Properties.POST_ID);
                    if (postId.isPresent()) {
                        botUserRepository.deleteAllByPostId(postId.get().getValue());
                        propertyRepository.deleteById(Properties.POST_ID);
                    }
                } else {
                    Property newPostUrl = new Property(Properties.POST_URL, messageText);
                    propertyRepository.save(newPostUrl);
                }
            }
            case TEXT_PATTERN -> {
                Property property = new Property(Properties.TEXT_PATTERN, messageText);
                propertyRepository.save(property);
            }
            case LESSON_LINK -> {
                Property property = new Property(Properties.LESSON_LINK, messageText);
                propertyRepository.save(property);
            }
            case QUESTIONNAIRE_LINK -> {
                Property property = new Property(Properties.QUESTIONNAIRE_LINK, messageText);
                propertyRepository.save(property);
            }
            case MASS_SENDING -> {
                SendMessage sendMessage = replyMessagesService.createMessage(messageText, 1L);
                massSendingStorage.setMethod(sendMessage);
                ButtonKeyboard buttonKeyboard = new ButtonKeyboard();
                buttonKeyboard.addMessageButton(0, ButtonEnum.CONFIRM.getCode(), ButtonEnum.CONFIRM.getName());
                buttonKeyboard.addMessageButton(0, ButtonEnum.DECLINE.getCode(), ButtonEnum.DECLINE.getName());
                SendMessage adminMessage = replyMessagesService.createMessageWithButtons(CONFIRM_MESSAGE, botUser.getTgUserId(), buttonKeyboard.getMessageButtons());
                botMessageService.updateLastMessage(adminMessage, update);
                return;
            }
            default -> {
                return;
            }
        }
        startCommand.processAdminUpdate(botUser, update, errorMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateType getUpdateHandlerType() {
        return UpdateType.TEXT_MESSAGE;
    }
}
