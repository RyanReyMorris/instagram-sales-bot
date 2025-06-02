package ru.ryanreymorris.instagramsalesbot.telegram.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Context provider is used to avoid cyclic dependencies in spring-application.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}