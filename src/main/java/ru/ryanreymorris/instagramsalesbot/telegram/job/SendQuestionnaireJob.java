package ru.ryanreymorris.instagramsalesbot.telegram.job;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.BotUser;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import ru.ryanreymorris.instagramsalesbot.repository.BotUserRepository;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.service.BotMessageService;
import ru.ryanreymorris.instagramsalesbot.telegram.service.ReplyMessagesService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;

@DisallowConcurrentExecution
@Component
public class SendQuestionnaireJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SendQuestionnaireJob.class);

    private final static String SEND_QUESTIONNAIRE = """
            Анкета предварительной записи на мое наставничество по 3д моушн дизайну и заработку на этом в середине декабря.
                        
            В прошлый раз было 120 анкет и солд-аут.
            В этот раз запросов в разы больше.
                        
            Заполняйте и я созвонюсь с вами :point_down:
            {0}
                        
            Бонусы:
            -доступ в закрытый канал программы (там уже более чем 300 человек)
            -личный созвон со мной
            """;

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private BotUserRepository botUserRepository;
    @Autowired
    private BotMessageService botMessageService;
    @Autowired
    private ReplyMessagesService replyMessagesService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("Начало работы джоба {}", this.getClass().getSimpleName());
        Optional<Property> questionnaireLink = propertyRepository.findById(Properties.QUESTIONNAIRE_LINK);
        if(questionnaireLink.isPresent()) {
            Collection<BotUser> readyToQuestionnaireSend = botUserRepository.findReadyToSendQuestionnaire();
            for(BotUser botUser: readyToQuestionnaireSend) {
                try {
                    String questionnaireLinkText = questionnaireLink.get().getValue();
                    String messageText = MessageFormat.format(SEND_QUESTIONNAIRE, questionnaireLinkText);
                    SendMessage sendMessage = replyMessagesService.createMessage(messageText, botUser.getTgUserId());
                    botMessageService.updateLastMessage(sendMessage, botUser);
                    botUser.setQuestionnaireSent(true);
                    botUserRepository.save(botUser);
                } catch (Exception e) {
                    logger.error("Ошибка при отправке анкеты", e);
                }
            }
        } else {
            logger.warn("Не задана ссылка на анкету");
        }
        logger.info("Окончание работы джоба {}", this.getClass().getSimpleName());
    }
}
