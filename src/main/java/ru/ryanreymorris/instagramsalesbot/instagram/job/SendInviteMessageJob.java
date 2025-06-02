package ru.ryanreymorris.instagramsalesbot.instagram.job;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import ru.ryanreymorris.instagramsalesbot.instagram.InstagramBot;
import ru.ryanreymorris.instagramsalesbot.instagram.dto.SendMessageResult;
import ru.ryanreymorris.instagramsalesbot.instagram.exception.InstagramRuntimeException;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import org.apache.commons.io.IOUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;

@DisallowConcurrentExecution
@Component
public class SendInviteMessageJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SendInviteMessageJob.class);

    @Value("${bot.username}")
    private String tgBotName;

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private InstagramBot instagramBot;
    @Autowired
    private BotUserRepository botUserRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("Начало работы джоба {}", this.getClass().getSimpleName());
        String audioId = getAudioId();
        if (audioId == null) {
            logger.warn("Отсутствует аудио-приглашение");
            logger.info("Окончание работы джоба {}", this.getClass().getSimpleName());
            return;
        }
        Collection<BotUser> users = botUserRepository.findReadyToSendInvite();
        for (BotUser user : users) {
            try {
                if (!sendAudioMessage(user, audioId)) {
                    logger.error("Пользователь с id="+user.getInstUserId().toString()+" не был обработан");
                    continue;
                }
                String botInviteMessage = MessageFormat.format("Ссылка на урок - http://t.me/{0}?start={1}", tgBotName, user.getInstUserId().toString());
                if (!sendTextMessage(user, botInviteMessage)) {
                    logger.error("Пользователь с id="+user.getInstUserId().toString()+" не был обработан");
                    continue;
                }
                logger.info("Обработан пользователь {}", user.getInstUserId());
            } catch (Exception e) {
                logger.error("Ошибка при отправке инвайта", e);
            }
        }
        logger.info("Окончание работы джоба {}", this.getClass().getSimpleName());
    }

    private boolean sendTextMessage(BotUser user, String botInviteLink) {
        SendMessageResult sendBotInviteLinkResult = instagramBot.sendDirectText(user.getInstUserId(), botInviteLink);
        if (!sendBotInviteLinkResult.isMessageSent()) {
            user.incrementRetryCount();
            user.setErrorMessage(sendBotInviteLinkResult.getErrorMessage());
            botUserRepository.save(user);
            return false;
        } else {
            user.setMessageSent(true);
            botUserRepository.save(user);
            return true;
        }
    }

    private boolean sendAudioMessage(BotUser user, String audioId) {
        SendMessageResult sendAudioResult = instagramBot.sendDirectAudio(user.getInstUserId(), audioId);
        if (!sendAudioResult.isMessageSent()) {
            user.incrementRetryCount();
            user.setErrorMessage(sendAudioResult.getErrorMessage());
            botUserRepository.save(user);
            return false;
        } else {
            user.setMessageSent(true);
            botUserRepository.save(user);
            return true;
        }
    }

    private String getAudioId() {
        String id;
        File audioFile = new File("../audio-invite.m4a");
        if (!audioFile.exists()) {
            return null;
        }
        id = String.valueOf(System.currentTimeMillis());
        byte[] audioFileBytes;
        try {
            audioFileBytes = IOUtils.toByteArray(new FileInputStream(audioFile));
        } catch (IOException e) {
            logger.error("Произошла ошибка при считывании аудио-файла для инвайта");
            throw new InstagramRuntimeException("Произошла ошибка при считывании аудио-файла для инвайта", e);
        }
        instagramBot.uploadAudioToServer(audioFileBytes, id);
        propertyRepository.save(new Property(Properties.AUDIO_INVITE_ID, id));
        return id;
    }
}