package ru.ryanreymorris.instagramsalesbot.telegram.handler.command;


import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Unknown {@link Command}.
 */
@Component
public class UnknownCommand  implements Command {

    private final static String UNKNOWN_MESSAGE = """
                К сожалению, я не знаю такой команды :no_mouth:
                """;

    @Autowired
    private BotUserRepository botUserRepository;

    @Autowired
    private ReplyMessagesService replyMessagesService;

    @Autowired
    private BotMessageService botMessageService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCommand(Update update) {
        BotUser user = botUserRepository.findByTgUserId(update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId());
        SendMessage sendMessage = replyMessagesService.createMessage(UNKNOWN_MESSAGE, user.getTgUserId());
        botMessageService.updateLastMessage(sendMessage, update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BotCommandEnum getBotcommand() {
        return BotCommandEnum.UNKNOWN;
    }
}
