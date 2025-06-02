package ru.ryanreymorris.instagramsalesbot.entity;

import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.ButtonEnum;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.command.BotCommandEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.Objects;

@Entity(name = "BotUser")
@Table(name = "bot_user")
public class BotUser {

    @Id
    @Column(name = "inst_user_id")
    private Long instUserId;

    @Column(name = "tg_user_id")
    private Long tgUserId;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "inst_username")
    private String instUsername;

    @Column(name = "tg_username")
    private String tgUsername;

    @Column(name = "comment_id")
    private String commentId;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "is_message_sent")
    private Boolean isMessageSent;

    @Column(name = "is_lesson_sent")
    private Boolean isLessonSent;

    @Column(name = "is_questionnaire_sent")
    private Boolean isQuestionnaireSent;

    @Column(name = "sent_retry_counter")
    private Integer sentRetryCounter;

    @Column(name = "created")
    private Date created;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "last_message")
    private Integer lastMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_button")
    private ButtonEnum activeButton;

    public BotUser() {
        this.isMessageSent = false;
        this.isAdmin = false;
        this.isLessonSent = false;
        this.isQuestionnaireSent = false;
    }

    public BotUser(Long instUserId, String instUsername, String commentId, String postId) {
        this.instUserId = instUserId;
        this.instUsername = instUsername;
        this.commentId = commentId;
        this.postId = postId;
        this.isMessageSent = false;
        this.isAdmin = false;
        this.isLessonSent = false;
        this.isQuestionnaireSent = false;
        this.created = new Date();
        this.sentRetryCounter = 0;
    }

    public Long getInstUserId() {
        return instUserId;
    }

    public void setInstUserId(Long userId) {
        this.instUserId = userId;
    }

    public String getInstUsername() {
        return instUsername;
    }

    public void setInstUsername(String instUsername) {
        this.instUsername = instUsername;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isMessageSent() {
        return isMessageSent;
    }

    public void setMessageSent(boolean messageSent) {
        isMessageSent = messageSent;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getSentRetryCounter() {
        return sentRetryCounter;
    }

    public void setSentRetryCounter(int sentRetryCounter) {
        this.sentRetryCounter = sentRetryCounter;
    }

    public void incrementRetryCount() {
        ++this.sentRetryCounter;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getTgUserId() {
        return tgUserId;
    }

    public void setTgUserId(Long tgUserId) {
        this.tgUserId = tgUserId;
    }

    public boolean isLessonSent() {
        return isLessonSent;
    }

    public void setLessonSent(boolean lessonSent) {
        isLessonSent = lessonSent;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getTgUsername() {
        return tgUsername;
    }

    public void setTgUsername(String tgUsername) {
        this.tgUsername = tgUsername;
    }

    public Integer getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Integer lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ButtonEnum getActiveButton() {
        return activeButton;
    }

    public void setActiveButton(ButtonEnum activeButton) {
        this.activeButton = activeButton;
    }

    public boolean isQuestionnaireSent() {
        return isQuestionnaireSent;
    }

    public void setQuestionnaireSent(boolean questionnaireSent) {
        isQuestionnaireSent = questionnaireSent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotUser botUser = (BotUser) o;
        return isAdmin == botUser.isAdmin && isMessageSent == botUser.isMessageSent && isLessonSent == botUser.isLessonSent && isQuestionnaireSent == botUser.isQuestionnaireSent && sentRetryCounter == botUser.sentRetryCounter && Objects.equals(instUserId, botUser.instUserId) && Objects.equals(tgUserId, botUser.tgUserId) && Objects.equals(instUsername, botUser.instUsername) && Objects.equals(tgUsername, botUser.tgUsername) && Objects.equals(commentId, botUser.commentId) && Objects.equals(postId, botUser.postId) && Objects.equals(created, botUser.created) && Objects.equals(errorMessage, botUser.errorMessage) && Objects.equals(lastMessage, botUser.lastMessage) && activeButton == botUser.activeButton;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instUserId, tgUserId, isAdmin, instUsername, tgUsername, commentId, postId, isMessageSent, isLessonSent, isQuestionnaireSent, sentRetryCounter, created, errorMessage, lastMessage, activeButton);
    }

    @Override
    public String toString() {
        return "BotUser{" +
                "instUserId=" + instUserId +
                ", tgUserId=" + tgUserId +
                ", isAdmin=" + isAdmin +
                ", instUsername='" + instUsername + '\'' +
                ", tgUsername='" + tgUsername + '\'' +
                ", commentId='" + commentId + '\'' +
                ", postId='" + postId + '\'' +
                ", isMessageSent=" + isMessageSent +
                ", isLessonSent=" + isLessonSent +
                ", isQuestionnaireSent=" + isQuestionnaireSent +
                ", sentRetryCounter=" + sentRetryCounter +
                ", created=" + created +
                ", errorMessage='" + errorMessage + '\'' +
                ", lastMessage=" + lastMessage +
                ", activeButton=" + activeButton +
                '}';
    }
}
