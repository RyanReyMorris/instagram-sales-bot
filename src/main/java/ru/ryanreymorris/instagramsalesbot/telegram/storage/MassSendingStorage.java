package ru.ryanreymorris.instagramsalesbot.telegram.storage;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

@Component
public class MassSendingStorage {

    private PartialBotApiMethod<?> method;

    public void setMethod(PartialBotApiMethod<?> method) {
        this.method = method;
    }

    public PartialBotApiMethod<?> getMethod() {
        return this.method;
    }
}
