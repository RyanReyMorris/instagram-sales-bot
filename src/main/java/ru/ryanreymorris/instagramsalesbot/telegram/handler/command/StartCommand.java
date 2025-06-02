package ru.ryanreymorris.instagramsalesbot.telegram.handler.command;


import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import ru.ryanreymorris.instagramsalesbot.repository.SchedulerRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.ButtonEnum;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.ButtonKeyboard;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * Start {@link Command}.
 */
@Component
public class StartCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(StartCommand.class);

    private final static String START_MESSAGE = """
            Держи урок - {0}
            """;

    private final static String NO_LESSON_LINK = """
            Привет, {0}! :grin:
            Рад видеть тебя здесь!
            К сожалению, ссылка на урок пока что недоступна.
            Попробуй написать чуть позже.
            """;

    private final static String STATISTICS = """
            :gear: Статус работы бота: {0}
            :email: Количество приглашенных в инстаграм: {1}
            :runner: Количество пришедших в телеграм: {2}
            :link: Текущая ссылка на пост: {3}
            :link: Текущая ссылка на урок: {4}
            :link: Текущая ссылка на анкету: {5}
            :eye: Ключевое слово поиска: {6}
            :date: Дата проверки: {7}
            """;

    @Autowired
    private ReplyMessagesService replyMessagesService;
    @Autowired
    private SchedulerRepository schedulerRepository;
    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private BotMessageService botMessageService;
    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCommand(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        if (botUser.isAdmin()) {
            processAdminUpdate(botUser, update, null);
        } else {
            processUserUpdate(botUser, update);
        }
    }

    private void processUserUpdate(BotUser botUser, Update update) {
        Optional<Property> lessonLink = propertyRepository.findById(Properties.LESSON_LINK);
        if (lessonLink.isEmpty()) {
            String messageText = MessageFormat.format(NO_LESSON_LINK, botUser.getTgUsername());
            SendMessage sendMessage = replyMessagesService.createMessage(messageText, botUser.getTgUserId());
            botMessageService.updateLastMessage(sendMessage, update);
        } else {
            String lessonLinkUrl = lessonLink.get().getValue();
            String messageText = MessageFormat.format(START_MESSAGE, lessonLinkUrl);
            SendMessage sendMessage = replyMessagesService.createMessage(messageText, botUser.getTgUserId());
            botMessageService.updateLastMessage(sendMessage, update);
            botUser.setLessonSent(true);
            botUserRepository.save(botUser);
        }
    }

    public void processAdminUpdate(BotUser botUser, Update update, String errorMessage) {
        botUser.setActiveButton(null);
        botUserRepository.save(botUser);
        Long instagramInvitesCount = botUserRepository.instagramInvitesCount();
        Long telegramInvitesCount = botUserRepository.telegramInvitesCount();
        Collection<Property> properties = propertyRepository.findAll();
        String postUrl = properties.stream().filter(p -> Properties.POST_URL.equals(p.getKey())).findFirst().map(Property::getValue).orElse("-");
        String lessonLink = properties.stream().filter(p -> Properties.LESSON_LINK.equals(p.getKey())).findFirst().map(Property::getValue).orElse("-");
        String questionnaireLink = properties.stream().filter(p -> Properties.QUESTIONNAIRE_LINK.equals(p.getKey())).findFirst().map(Property::getValue).orElse("-");
        String textPattern = properties.stream().filter(p -> Properties.TEXT_PATTERN.equals(p.getKey())).findFirst().map(Property::getValue).orElse("-");
        String status;
        switch (schedulerRepository.findAll().size()) {
            case 3 -> status = "Включен :white_check_mark:";
            case 0 -> status = "Отключен :x:";
            default -> status = errorMessage == null ? "ОШИБКА :exclamation:" : ":exclamation: ОШИБКА: errorMessage";
        }
        ButtonKeyboard buttonKeyboard = new ButtonKeyboard();
        buttonKeyboard.addMessageButton(0, ButtonEnum.ENABLE.getCode(), ButtonEnum.ENABLE.getName());
        buttonKeyboard.addMessageButton(0, ButtonEnum.DISABLE.getCode(), ButtonEnum.DISABLE.getName());
        buttonKeyboard.addMessageButton(1, ButtonEnum.POST_URL.getCode(), ButtonEnum.POST_URL.getName());
        buttonKeyboard.addMessageButton(2, ButtonEnum.TEXT_PATTERN.getCode(), ButtonEnum.TEXT_PATTERN.getName());
        buttonKeyboard.addMessageButton(3, ButtonEnum.AUDIO.getCode(), ButtonEnum.AUDIO.getName());
        buttonKeyboard.addMessageButton(4, ButtonEnum.LESSON_LINK.getCode(), ButtonEnum.LESSON_LINK.getName());
        buttonKeyboard.addMessageButton(4, ButtonEnum.QUESTIONNAIRE_LINK.getCode(), ButtonEnum.QUESTIONNAIRE_LINK.getName());
        buttonKeyboard.addMessageButton(5, ButtonEnum.MASS_SENDING.getCode(), ButtonEnum.MASS_SENDING.getName());
        buttonKeyboard.addMessageButton(6, ButtonEnum.UPDATE.getCode(), ButtonEnum.UPDATE.getName());
        String message = MessageFormat.format(STATISTICS, status, instagramInvitesCount, telegramInvitesCount, postUrl, lessonLink, questionnaireLink, textPattern, new Date());
        SendMessage sendMessage = replyMessagesService.createMessageWithButtons(message, botUser.getTgUserId(), buttonKeyboard.getMessageButtons());
        sendMessage.disableWebPagePreview();
        botMessageService.updateLastMessage(sendMessage, update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BotCommandEnum getBotcommand() {
        return BotCommandEnum.START;
    }
}