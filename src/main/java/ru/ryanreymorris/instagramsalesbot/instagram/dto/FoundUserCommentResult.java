package ru.ryanreymorris.instagramsalesbot.instagram.dto;

import ru.ryanreymorris.instagramsalesbot.entity.BotUser;

import java.util.Collection;

public class FoundUserCommentResult {

    private Collection<BotUser> foundUserComments;

    private String newestCommentId;

    private Long sameCallsCount;

    public Collection<BotUser> getFoundUserComments() {
        return foundUserComments;
    }

    public void setFoundUserComments(Collection<BotUser> foundUserComments) {
        this.foundUserComments = foundUserComments;
    }

    public String getNewestCommentId() {
        return newestCommentId;
    }

    public void setNewestCommentId(String newestCommentId) {
        this.newestCommentId = newestCommentId;
    }

    public Long getSameCallsCount() {
        return sameCallsCount;
    }

    public void setSameCallsCount(Long sameCallsCount) {
        this.sameCallsCount = sameCallsCount;
    }
}
