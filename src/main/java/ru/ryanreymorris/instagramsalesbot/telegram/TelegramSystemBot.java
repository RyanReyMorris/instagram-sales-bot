package ru.ryanreymorris.instagramsalesbot.telegram;

import ru.ryanreymorris.instagramsalesbot.telegram.facade.BotFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramSystemBot extends TelegramLongPollingBot {

    private final String username;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    TelegramSystemBot(@Value("${bot.token}") String token, @Value("${bot.username}") String username) {
        super(token);
        this.username = username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        botFacadeService.handleUpdate(update);
    }


    @Override
    public String getBotUsername() {
        return username;
    }

}
