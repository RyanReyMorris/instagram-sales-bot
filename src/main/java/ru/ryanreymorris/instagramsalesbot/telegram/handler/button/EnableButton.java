package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.entity.SchedulerJobInfo;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.SchedulerRepository;
import ru.ryanreymorris.instagramsalesbot.service.JobService;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;


@Component
public class EnableButton implements Button {

    @Autowired
    private JobService jobService;
    @Autowired
    private StartCommand startCommand;
    @Autowired
    private BotUserRepository botUserRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleClick(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId());
        String errorMessage = null;
        try {
            jobService.enableAllJobs();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        startCommand.processAdminUpdate(botUser, update, errorMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ButtonEnum getButton() {
        return ButtonEnum.ENABLE;
    }
}
