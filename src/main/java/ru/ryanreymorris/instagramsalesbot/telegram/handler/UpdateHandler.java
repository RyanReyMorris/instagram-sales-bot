package ru.ryanreymorris.instagramsalesbot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Common interface for update-handlers. it processes incoming update and reply to customer.
 */
public interface UpdateHandler {
    /**
     * Process/handle incoming update.
     *
     * @param update - incoming update.
     */
    void handle(Update update);

    /**
     * Get type of update handler.
     *
     * @return {@link UpdateType}.
     */
    UpdateType getUpdateHandlerType();
}
