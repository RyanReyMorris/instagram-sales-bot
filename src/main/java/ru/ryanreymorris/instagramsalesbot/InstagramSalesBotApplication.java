package ru.ryanreymorris.instagramsalesbot;


import ru.ryanreymorris.instagramsalesbot.telegram.TelegramSystemBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class InstagramSalesBotApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ru.ryanreymorris.instagramsalesbot.InstagramSalesBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(context.getBean(TelegramSystemBot.class));
    }
}
