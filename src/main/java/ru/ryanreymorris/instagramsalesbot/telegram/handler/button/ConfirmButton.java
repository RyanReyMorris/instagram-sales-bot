package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.storage.MassSendingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;

@Component
public class ConfirmButton implements Button {

    @Autowired
    private BotMessageService botMessageService;
    @Autowired
    private MassSendingStorage massSendingStorage;
    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private StartCommand startCommand;

    @Override
    public void handleClick(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        startCommand.processAdminUpdate(botUser, update, null);
        PartialBotApiMethod<?> method = massSendingStorage.getMethod();
        Collection<BotUser> botUsers = botUserRepository.findForMassSending();
        for (BotUser botUserForMassSending : botUsers) {
            botMessageService.sendPartialBotMethod(method, botUserForMassSending.getTgUserId());
        }
        massSendingStorage.setMethod(null);
    }

    @Override
    public ButtonEnum getButton() {
        return ButtonEnum.CONFIRM;
    }
}
