package ru.ryanreymorris.instagramsalesbot.telegram.handler.voice;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class VoiceMessageHandler implements UpdateHandler {

    private static final String CONFIRM_MESSAGE = "Подтвердите массовую отправку сообщения:";

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

    @Override
    public void handle(Update update) {
        BotUser botUser = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId());
        if (!botUser.isAdmin() || botUser.getActiveButton() == null) {
            return;
        }
        switch (botUser.getActiveButton()) {
            case MASS_SENDING -> {
                SendVoice sendVoice = new SendVoice("", new InputFile(update.getMessage().getVoice().getFileId()));
                massSendingStorage.setMethod(sendVoice);
                ButtonKeyboard buttonKeyboard = new ButtonKeyboard();
                buttonKeyboard.addMessageButton(0, ButtonEnum.CONFIRM.getCode(), ButtonEnum.CONFIRM.getName());
                buttonKeyboard.addMessageButton(0, ButtonEnum.DECLINE.getCode(), ButtonEnum.DECLINE.getName());
                SendMessage adminMessage = replyMessagesService.createMessageWithButtons(CONFIRM_MESSAGE, botUser.getTgUserId(), buttonKeyboard.getMessageButtons());
                botMessageService.updateLastMessage(adminMessage, update);
            }
            default -> {
                startCommand.processAdminUpdate(botUser, update, null);
            }
        }
    }

    @Override
    public UpdateType getUpdateHandlerType() {
        return UpdateType.VOICE;
    }
}
