package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class JobCronExpressionButton implements Button {

    private final static String CRON_EXPRESSION = "Введите cron-значение периодичности работы джоба. Пример: CRON:0 */5 * * * ?";

    @Autowired
    private BotMessageService botMessageService;
    @Autowired
    private ReplyMessagesService replyMessagesService;
    @Autowired
    private BotUserRepository botUserRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleClick(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId());
        botUser.setActiveButton(getButton());
        botUserRepository.save(botUser);
        SendMessage sendMessage = replyMessagesService.createMessage(CRON_EXPRESSION, botUser.getTgUserId());
        botMessageService.updateLastMessage(sendMessage, update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ButtonEnum getButton() {
        return ButtonEnum.JOB_CRON_EXPRESSION;
    }
}