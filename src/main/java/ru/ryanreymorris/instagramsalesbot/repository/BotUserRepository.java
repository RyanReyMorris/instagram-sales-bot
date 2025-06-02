package ru.ryanreymorris.instagramsalesbot.repository;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    Collection<BotUser> findAllByInstUserIdIn(Collection<Long> userIds);

    @Query("select u from BotUser u where u.isMessageSent = false and u.instUsername is not null and u.sentRetryCounter < 3 order by u.created asc limit 50")
    Collection<BotUser> findReadyToSendInvite();

    @Query("select u from BotUser u where u.isLessonSent = true and u.isAdmin=false order by u.created asc")
    Collection<BotUser> findForMassSending();

    @Query("select u from BotUser u where u.isLessonSent = true and u.isQuestionnaireSent=false order by u.created asc")
    Collection<BotUser> findReadyToSendQuestionnaire();

    @Query("SELECT COUNT(u) FROM BotUser u WHERE u.isMessageSent = true")
    Long instagramInvitesCount();

    @Query("SELECT COUNT(u) FROM BotUser u WHERE u.isLessonSent = true")
    Long telegramInvitesCount();

    void deleteAllByPostId(String postId);

    BotUser findByTgUserId(Long tgUserId);
}
