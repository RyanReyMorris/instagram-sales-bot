package ru.ryanreymorris.instagramsalesbot.telegram.facade;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.instagram.InstagramBot;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateHandler;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

/**
 * Facade service of bot. It processes incoming update from user and sends it to proper handler.
 */
@Service
public class BotFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(BotFacadeService.class);

    @Autowired
    private Map<UpdateType, UpdateHandler> updateHandlers;
    @Autowired
    private BotUserRepository botUserRepository;

    /**
     * Handle incoming update from customer. Dedicate update to proper handler
     *
     * @param update - incoming update
     */
    public void handleUpdate(Update update) {
        Long tgUserId = update.getMessage() == null ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        logger.info("Пришел пользователь с tgUserId="+tgUserId.toString());
        BotUser existingUser = botUserRepository.findByTgUserId(tgUserId);
        if (existingUser != null) {
            if (!existingUser.isAdmin() && existingUser.isQuestionnaireSent()) {
                return;
            }
            updateHandlers.get(getHandlerType(update)).handle(update);
            return;
        }
        String instUserId = StringUtils.substringAfterLast(update.getMessage().getText(), " ");
        if (StringUtils.isEmpty(instUserId)) {
            logger.warn("Пустая ссылка!");
            return;
        }
        if (instUserId.equals("privateGuest")) {
            logger.info("privateGuest c id="+tgUserId.toString());
            existingUser = new BotUser();
            existingUser.setInstUserId(tgUserId);
            existingUser.setTgUserId(tgUserId);
            existingUser.setTgUsername("Приватный:" + getUsername(update.getMessage() == null ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom()));
            botUserRepository.save(existingUser);
            updateHandlers.get(getHandlerType(update)).handle(update);
        } else {
            Long instUserIdLong;
            try {
                instUserIdLong = Long.parseLong(instUserId);
            } catch (Exception e) {
                logger.error("Ошибка при прасинге instId="+instUserId+" для пользователя c id="+tgUserId.toString());
                return;
            }
            logger.info("Найден новый пользователь c id="+tgUserId.toString()+" и instId="+instUserIdLong.toString());
            Optional<BotUser> newUser = botUserRepository.findById(instUserIdLong);
            if (newUser.isPresent()) {
                existingUser = newUser.get();
                existingUser.setTgUserId(tgUserId);
                existingUser.setTgUsername(getUsername(update.getMessage().getFrom()));
                botUserRepository.save(existingUser);
                updateHandlers.get(getHandlerType(update)).handle(update);
            }
        }
    }

    /**
     * Get available customer name.
     *
     * @param user - telegram user.
     * @return username.
     */
    private String getUsername(User user) {
        String customerName;
        String customerFirstName = user.getFirstName();
        String customerLastName = user.getLastName();
        if (StringUtils.isNoneEmpty(customerFirstName, customerLastName)) {
            customerName = MessageFormat.format("{0} {1}", customerFirstName, customerLastName);
        } else if (StringUtils.isNotEmpty(customerFirstName)) {
            customerName = customerFirstName;
        } else if (StringUtils.isNotEmpty(customerLastName)) {
            customerName = customerLastName;
        } else {
            customerName = user.getUserName();
        }
        return customerName;
    }

    /**
     * Get type of user's update actions
     *
     * @param update - sent update
     * @return type of update
     */
    private UpdateType getHandlerType(Update update) {
        if (update.hasCallbackQuery()) {
            return UpdateType.BUTTON_CLICK;
        } else if (update.getMessage().hasPhoto()) {
            return UpdateType.PHOTO;
        } else if (update.getMessage().hasVideo()) {
            return UpdateType.VIDEO;
        } else if (update.getMessage().hasVideoNote()) {
            return UpdateType.VIDEO_NOTE;
        } else if (update.getMessage().hasVoice()) {
            return UpdateType.VOICE;
        } else if (update.getMessage().hasEntities() && update.getMessage().getEntities().get(0).getType().equals("bot_command")) {
            if (update.getMessage().getText().contains("CRON:")) {
                return UpdateType.TEXT_MESSAGE;
            }
            return UpdateType.BOT_COMMAND;
        } else if (update.getMessage().hasAudio()) {
            return UpdateType.AUDIO;
        } else {
            return UpdateType.TEXT_MESSAGE;
        }
    }
}