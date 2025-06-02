package ru.ryanreymorris.instagramsalesbot.instagram;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.instagram.dto.FoundUserCommentResult;
import ru.ryanreymorris.instagramsalesbot.instagram.dto.SendMessageResult;
import ru.ryanreymorris.instagramsalesbot.instagram.exception.InstagramRuntimeException;
import ru.ryanreymorris.instagramsalesbot.instagram.inst4j.MediaGetCommentsMinIdRequest;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.media.Media;
import com.github.instagram4j.instagram4j.models.media.UploadParameters;
import com.github.instagram4j.instagram4j.models.media.timeline.Comment;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.direct.DirectThreadsBroadcastRequest;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.requests.upload.MediaUploadFinishRequest;
import com.github.instagram4j.instagram4j.requests.upload.RuploadVideoRequest;
import com.github.instagram4j.instagram4j.responses.IGResponse;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaGetCommentsResponse;
import com.xcoder.easyinsta.Instagram;
import com.xcoder.easyinsta.exceptions.IGLoginException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Service
public class InstagramBot {

    private static final Logger logger = LoggerFactory.getLogger(InstagramBot.class);
    private static final String LOGIN_EXCEPTION = "Произошла ошибка при попытке авторизации:{0}";
    private static final String BOT_IS_BLOCKED = "Превышено максимальное количество попыток неудачной авторизации. Инстаграм-бот заблокирован";
    private static final AtomicLong mediaCommentCallCount = new AtomicLong(0);
    private static final Queue<Integer> delayQueue = new ConcurrentLinkedQueue<>();
    private int authRetryCount = 0;

    @Value("${inst.login}")
    private String login;
    @Value("${inst.password}")
    private String password;

    @Autowired
    private PropertyRepository propertyRepository;

    @PostConstruct
    private void postConstruct() throws Exception {
        logger.info("Инициализация инстаграм-бота");
        new File("../insta-cache").mkdirs();
        logger.info("Директория insta-cache успешно создана");
        this.authRetryCount = Integer.parseInt(propertyRepository.findById(Properties.AUTH_RETRY_COUNT)
                .orElseThrow(() -> new Exception("Произошла ошибка при инициализации приложения. Не заданы настройка:" + Properties.AUTH_RETRY_COUNT))
                .getValue());
        delayQueue.add(7000);
        delayQueue.add(5000);
        delayQueue.add(10000);
        delayQueue.add(4000);
        delayQueue.add(3000);
        delayQueue.add(8000);
        delayQueue.add(6000);
        logger.info("Успешно установлены настройки");
    }

    public SendMessageResult sendDirectText(Long userId, String message) {
        SendMessageResult sendMessageResult = new SendMessageResult();
        IGResponse response = null;
        try {
            response = invoke(client -> new DirectThreadsBroadcastRequest(new DirectThreadsBroadcastRequest.BroadcastTextPayload(message, userId)).execute(client).join());
            sendMessageResult.setMessageSent(true);
        } catch (InstagramRuntimeException ie) {
            logger.error(MessageFormat.format("Произошла ошибка при отправке текстового сообщения:{0}", ie.getMessage()), ie.getSuppressed()[0]);
            sendMessageResult.setMessageSent(false);
            sendMessageResult.setErrorMessage(ie.getMessage());
        } catch (Exception e) {
            logger.error(MessageFormat.format("Неизвестная ошибка при отправке текстового сообщения:{0}", e.getMessage()), e.getCause());
            sendMessageResult.setMessageSent(false);
            sendMessageResult.setErrorMessage(e.getMessage());
        }
        sendMessageResult.setResponse(response);
        return sendMessageResult;
    }

    public SendMessageResult sendDirectAudio(Long userId, String audioId) {
        SendMessageResult sendMessageResult = new SendMessageResult();
        IGResponse response = null;
        try {
            response = invoke(client -> new DirectThreadsBroadcastRequest(new DirectThreadsBroadcastRequest.BroadcastShareVoicePayload(audioId, userId)).execute(client).join());
            sendMessageResult.setMessageSent(true);
        } catch (InstagramRuntimeException ie) {
            logger.error(MessageFormat.format("Произошла ошибка при отправке аудио-сообщения{0}", ie.getMessage()), ie.getSuppressed()[0]);
            sendMessageResult.setMessageSent(false);
            sendMessageResult.setErrorMessage(ie.getMessage());
        } catch (Exception e) {
            logger.error(MessageFormat.format("Неизвестная ошибка при отправке аудио-сообщения:{0}", e.getMessage()), e.getCause());
            sendMessageResult.setMessageSent(false);
            sendMessageResult.setErrorMessage(e.getMessage());
        }
        sendMessageResult.setResponse(response);
        return sendMessageResult;
    }

    public void uploadAudioToServer(byte[] audioFileBytes, String id) {
        invoke(client -> {
            new RuploadVideoRequest(audioFileBytes, UploadParameters.forDirectVoice(id)).execute(client).join();
            IGResponse response = new MediaUploadFinishRequest(id).execute(client).join();
            logger.info("Аудио-инвайт успешно загружен на сервер");
            return 0;
        });
    }

    public FoundUserCommentResult findUserPostComments(String postId, String textPattern, String newestCommentId) {
        FoundUserCommentResult foundUserCommentResult = new FoundUserCommentResult();
        MediaGetCommentsMinIdRequest request = new MediaGetCommentsMinIdRequest(postId);

        return invoke(client -> {
            String minId;
            List<Comment> foundComments = new ArrayList<>();
            int commentsReelCount = 0;
            boolean containsOldComments;
            do {
                MediaGetCommentsResponse response = request.execute(client).join();
                minId = (String) response.getExtraProperties().get("next_min_id");
                request.setMinId(minId);
                if (commentsReelCount == 0) {
                    foundUserCommentResult.setNewestCommentId(response.getComments().get(0).getPk());
                }
                Collection<Comment> sortedComments = response.getComments()
                        .stream()
                        .filter(c -> c.getText().toLowerCase().contains(textPattern.toLowerCase()))
                        .toList();
                foundComments.addAll(sortedComments);
                commentsReelCount++;
                containsOldComments = response.getComments().stream().map(Comment::getPk).toList().contains(newestCommentId);
            } while (minId != null && !containsOldComments);
            List<BotUser> foundUsers = foundComments.stream()
                    .map(c -> new BotUser(c.getUser_id(), c.getUser().getUsername(), c.getPk(), postId))
                    .toList();
            foundUserCommentResult.setFoundUserComments(foundUsers);
            if (commentsReelCount == 1 && containsOldComments && foundUserCommentResult.getNewestCommentId().equals(newestCommentId)) {
                mediaCommentCallCount.incrementAndGet();
                foundUserCommentResult.setSameCallsCount(mediaCommentCallCount.get());
            } else {
                resetSameCallsCount();
            }
            return foundUserCommentResult;
        });
    }

    public void resetSameCallsCount() {
        mediaCommentCallCount.set(0);
    }

    public String findPostId(String postUrl) {
        return invoke(client -> {
            String postCode;
            if (postUrl.contains("reel")) {
                postCode = StringUtils.substringBetween(postUrl, "https://www.instagram.com/reel/", "/?");
            } else {
                postCode = StringUtils.substringBetween(postUrl, "https://www.instagram.com/p/", "/?");
            }
            String maxId;
            Optional<TimelineMedia> postToBeFind;
            FeedUserRequest request = new FeedUserRequest(client.getSelfProfile().getPk());
            do {
                FeedUserResponse response = client.sendRequest(request).join();
                maxId = response.getNext_max_id();
                request.setMax_id(response.getNext_max_id());
                postToBeFind = response.getItems()
                        .stream()
                        .filter(item -> postCode.equals(item.getCode()))
                        .findAny();
            } while (maxId != null && postToBeFind.isEmpty());
            return postToBeFind.map(Media::getId).orElse(null);
        });
    }

    private Instagram login(boolean shouldUpdate) {
        try {
            if (shouldUpdate) {
                File dir = new File("../insta-cache");
                File client = new File(dir, "ClientObject.ser");
                File cookie = new File(dir, "LoginSession.ser");
                Instagram instagram = Instagram.login(login, password);
                instagram.client.serialize(client, cookie);
                return instagram;
            }
            return Instagram.loginOrCache(new File("../insta-cache"), login, password);
        } catch (IGLoginException e) {
            logger.error("Произошла ошибка при попытке авторизации");
            --authRetryCount;
            throw new InstagramRuntimeException(MessageFormat.format(LOGIN_EXCEPTION, e.getMessage()), e.getReason(), e.getCause());
        } catch (IOException e) {
            throw new InstagramRuntimeException("Ошибка при работе с файлами кэша инстаграм", e);
        }
    }

    private synchronized <T> T invoke(@NotNull Function<IGClient, T> function) {
        return invoke(function, 3);
    }

    private synchronized <T> T invoke(@NotNull Function<IGClient, T> function, int tries) {
        if (authRetryCount == 0) throw new InstagramRuntimeException(BOT_IS_BLOCKED);
        RuntimeException exception = new RuntimeException();
        while (tries > 0) {
            try {
                int delayMillis = getNextDelay();
                Thread.sleep(delayMillis);
                IGClient client = login(false).client;
                return function.apply(client);
            } catch (InstagramRuntimeException e) {
                if (e.getMessage().contains("Произошла ошибка при попытке авторизации")) {
                    logger.error("Произошла ошибка при выполнении действия. Осталось попыток:{}. Исходная ошибка:{}", tries--, e.getMessage());
                    exception = e;
                    login(true);
                } else {
                    logger.error("Произошла ошибка при выполнении действия. Осталось попыток:{}. Исходная ошибка:{}", tries--, e.getMessage());
                    exception = e;
                }
            } catch (InterruptedException ie) {
                logger.error("Произошло прерывание потока. Осталось попыток:{}. Исходная ошибка:{}", tries--, ie.getMessage());
                exception = new RuntimeException("Произошло прерывание потока", ie);
            }
        }
        throw exception;
    }

    private Integer getNextDelay() {
        Integer delayMillis = delayQueue.poll();
        delayQueue.add(delayMillis);
        return delayMillis;
    }
}