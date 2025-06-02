package ru.ryanreymorris.instagramsalesbot.telegram.handler.audio;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.TelegramSystemBot;
import ru.ryanreymorris.instagramsalesbot.telegram.config.ApplicationContextProvider;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateHandler;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateType;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.StartCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

/**
 * Handler of audio message.
 */
@Component
public class AudioMessageHandler implements UpdateHandler {

    @Autowired
    private StartCommand command;
    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void handle(Update update) {
        TelegramSystemBot telegramSystemBot = getBot();
        GetFile getFile = new GetFile();
        getFile.setFileId(update.getMessage().getAudio().getFileId());
        try {
            String filePath = telegramSystemBot.execute(getFile).getFilePath();
            telegramSystemBot.downloadFile(filePath, new File("../audio-invite.m4a"));
            propertyRepository.deleteById(Properties.AUDIO_INVITE_ID);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        command.handleCommand(update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateType getUpdateHandlerType() {
        return UpdateType.AUDIO;
    }

    /**
     * Get Bot. Resolve cyclic dependencies of beans.
     *
     * @return OrderEscortBot bean.
     */
    private TelegramSystemBot getBot() {
        return ApplicationContextProvider.getApplicationContext().getBean(TelegramSystemBot.class);
    }
}