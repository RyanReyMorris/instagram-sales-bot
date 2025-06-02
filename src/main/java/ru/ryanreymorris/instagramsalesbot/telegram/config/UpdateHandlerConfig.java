package ru.ryanreymorris.instagramsalesbot.telegram.config;


import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateHandler;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Update handlers configuration.
 */
@Configuration
public class UpdateHandlerConfig {

    @Autowired
    private List<UpdateHandler> handlers;

    @Bean
    public Map<UpdateType, UpdateHandler> updateHandlers() {
        Map<UpdateType, UpdateHandler> updateHandlers = new HashMap<>();
        for (UpdateHandler handler : handlers) {
            updateHandlers.put(handler.getUpdateHandlerType(), handler);
        }
        return updateHandlers;
    }
}
