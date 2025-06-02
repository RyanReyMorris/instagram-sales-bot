package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import ru.ryanreymorris.instagramsalesbot.repository.SchedulerRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import ru.ryanreymorris.instagramsalesbot.telegram.storage.MassSendingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DeclineButton implements Button {

    @Autowired
    private MassSendingStorage massSendingStorage;
    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private StartCommand startCommand;

    @Override
    public void handleClick(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        massSendingStorage.setMethod(null);
        startCommand.processAdminUpdate(botUser, update, null);
    }

    @Override
    public ButtonEnum getButton() {
        return ButtonEnum.DECLINE;
    }
}
