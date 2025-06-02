package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.service.JobService;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DisableButton implements Button {

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
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        String errorMessage = null;
        try {
            jobService.disableAllJobs();
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
        return ButtonEnum.DISABLE;
    }
}
