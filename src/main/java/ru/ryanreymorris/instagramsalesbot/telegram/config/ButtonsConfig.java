package ru.ryanreymorris.instagramsalesbot.telegram.config;

import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.Button;
import ru.ryanreymorris.instagramsalesbot.telegram.handler.button.ButtonEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bot buttons configuration.
 */
@Configuration
public class ButtonsConfig {

    @Autowired
    private List<Button> buttons;

    @Bean
    public Map<ButtonEnum, Button> buttons() {
        Map<ButtonEnum, Button> botButtons = new HashMap<>();
        for (Button button : buttons) {
            botButtons.put(button.getButton(), button);
        }
        return botButtons;
    }

}
