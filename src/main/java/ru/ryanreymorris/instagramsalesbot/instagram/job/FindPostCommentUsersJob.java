package ru.ryanreymorris.instagramsalesbot.instagram.job;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import ru.ryanreymorris.instagramsalesbot.instagram.InstagramBot;
import ru.ryanreymorris.instagramsalesbot.instagram.dto.FoundUserCommentResult;
import ru.ryanreymorris.instagramsalesbot.instagram.exception.InstagramRuntimeException;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
@Component
public class FindPostCommentUsersJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(FindPostCommentUsersJob.class);
    private static final String NO_POST_CODE_PROVIDED = "Не указан код поста в инстаграме, по которому необходимо мониторить комментарии";
    private static final String NO_TEXT_PATTERN_PROVIDED = "Не указан паттерн комментария для поиска пользователей";
    private static Long waitingTimestamp = 0L;

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private InstagramBot instagramBot;
    @Autowired
    private BotUserRepository botUserRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("Начало работы джоба {}", this.getClass().getSimpleName());
        if (System.currentTimeMillis() < waitingTimestamp) {
            logger.info("Преждевременное окончание работы джоба {}. Защита от спама, время ожидания:{} м", this.getClass().getSimpleName(), ((waitingTimestamp - System.currentTimeMillis()) / 60000));
            return;
        }
        String postId = getCurrentPostId();
        String textPattern = propertyRepository.findById(Properties.TEXT_PATTERN).orElseThrow(() -> new InstagramRuntimeException(NO_TEXT_PATTERN_PROVIDED)).getValue();
        String lastCommentMark = propertyRepository.findById(Properties.NEWEST_COMMENT_ID).map(Property::getValue).orElse(null);
        FoundUserCommentResult foundUserCommentResult = instagramBot.findUserPostComments(postId, textPattern, lastCommentMark);
        propertyRepository.save(new Property(Properties.NEWEST_COMMENT_ID, foundUserCommentResult.getNewestCommentId()));
        Collection<BotUser> foundNewUsers = getNewUniqueUsers(foundUserCommentResult);
        botUserRepository.saveAll(foundNewUsers);
        if (foundUserCommentResult.getSameCallsCount() != null && foundUserCommentResult.getSameCallsCount() <= 10) {
            waitingTimestamp = System.currentTimeMillis() + 1000 * 60 * 10 * foundUserCommentResult.getSameCallsCount();
        } else {
            instagramBot.resetSameCallsCount();
        }
        logger.info("Окончание работы джоба {}", this.getClass().getSimpleName());
    }

    private Collection<BotUser> getNewUniqueUsers(FoundUserCommentResult foundUserCommentResult) {
        Collection<BotUser> foundUsers = foundUserCommentResult.getFoundUserComments();
        Collection<Long> foundUserIds = foundUsers.stream().map(BotUser::getInstUserId).toList();
        Collection<Long> existUsersIds = botUserRepository.findAllByInstUserIdIn(foundUserIds).stream().map(BotUser::getInstUserId).toList();
        Collection<BotUser> newUsers = foundUsers.stream().filter(user -> !existUsersIds.contains(user.getInstUserId())).collect(Collectors.toList());
        Set<Long> uniqueNames = new HashSet<>();
        newUsers.removeIf(user -> !uniqueNames.add(user.getInstUserId()));
        return newUsers;
    }

    private String getCurrentPostId() {
        String postId;
        Optional<Property> postIdOpt = propertyRepository.findById(Properties.POST_ID);
        if (postIdOpt.isPresent()) {
            postId = postIdOpt.get().getValue();
        } else {
            String postUrl = propertyRepository.findById(Properties.POST_URL).orElseThrow(() -> new InstagramRuntimeException(NO_POST_CODE_PROVIDED)).getValue();
            postId = instagramBot.findPostId(postUrl);
            propertyRepository.save(new Property(Properties.POST_ID, postId));
        }
        return postId;
    }
}