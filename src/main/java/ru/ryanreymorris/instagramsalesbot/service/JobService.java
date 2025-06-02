package ru.ryanreymorris.instagramsalesbot.service;

import ru.ryanreymorris.instagramsalesbot.config.Properties;
import ru.ryanreymorris.instagramsalesbot.entity.Property;
import ru.ryanreymorris.instagramsalesbot.entity.SchedulerJobInfo;
import ru.ryanreymorris.instagramsalesbot.instagram.exception.InstagramRuntimeException;
import ru.ryanreymorris.instagramsalesbot.instagram.job.FindPostCommentUsersJob;
import ru.ryanreymorris.instagramsalesbot.instagram.job.SendInviteMessageJob;
import ru.ryanreymorris.instagramsalesbot.repository.PropertyRepository;
import ru.ryanreymorris.instagramsalesbot.repository.SchedulerRepository;
import ru.ryanreymorris.instagramsalesbot.telegram.job.SendQuestionnaireJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    private static final String NO_REPEAT_EXPRESSION_PROVIDED = "Не указан период выполнения джобы";

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private SchedulerRepository schedulerRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public void updateInstCronExpression(String cronExpression) {
//        Property cronProperty = propertyRepository.findById(Properties.INST_JOB_REPEAT_EXPRESSION).orElseThrow(() -> new InstagramRuntimeException(NO_REPEAT_EXPRESSION_PROVIDED));
//        cronProperty.setValue(cronExpression);
//        propertyRepository.save(cronProperty);
//        Collection<SchedulerJobInfo> enableJobs = schedulerRepository.findAllByJobGroup("INSTAGRAM_JOBS");
//        if (!enableJobs.isEmpty()) {
//            SchedulerJobInfo postFindingJob = new SchedulerJobInfo(FindPostCommentUsersJob.class.getName(), "INSTAGRAM_JOBS", cronExpression);
//            SchedulerJobInfo sendInviteJob = new SchedulerJobInfo(SendInviteMessageJob.class.getName(), "INSTAGRAM_JOBS", cronExpression);
//            try {
//                deleteJob(postFindingJob);
//                deleteJob(sendInviteJob);
//            } catch (Exception e) {
//                throw new InstagramRuntimeException("Ошибка при включении инстаграм-бота: {0}", e);
//            }
//        }
    }

    public void enableAllJobs() {
        String instFindCronExpression = propertyRepository.findById(Properties.INST_FIND_JOB_REPEAT_EXPRESSION).orElseThrow(() -> new InstagramRuntimeException(NO_REPEAT_EXPRESSION_PROVIDED)).getValue();
        String instSendCronExpression = propertyRepository.findById(Properties.INST_SEND_JOB_REPEAT_EXPRESSION).orElseThrow(() -> new InstagramRuntimeException(NO_REPEAT_EXPRESSION_PROVIDED)).getValue();
        String tgCronExpression = propertyRepository.findById(Properties.TELEGRAM_JOB_REPEAT_EXPRESSION).orElseThrow(() -> new InstagramRuntimeException(NO_REPEAT_EXPRESSION_PROVIDED)).getValue();
        SchedulerJobInfo postFindingJob = new SchedulerJobInfo(FindPostCommentUsersJob.class.getName(), "INSTAGRAM_JOBS", instFindCronExpression);
        SchedulerJobInfo sendInviteJob = new SchedulerJobInfo(SendInviteMessageJob.class.getName(), "INSTAGRAM_JOBS", instSendCronExpression);
        SchedulerJobInfo sendQuestionnaireJob = new SchedulerJobInfo(SendQuestionnaireJob.class.getName(), "TELEGRAM_JOBS", tgCronExpression);
        try {
            createJob(postFindingJob);
            createJob(sendInviteJob);
            createJob(sendQuestionnaireJob);
        } catch (Exception e) {
            throw new InstagramRuntimeException("Ошибка при включении инстаграм-бота: {0}", e);
        }
    }

    public void disableAllJobs() {
        SchedulerJobInfo postFindingJob = new SchedulerJobInfo(FindPostCommentUsersJob.class.getName(), "INSTAGRAM_JOBS");
        SchedulerJobInfo sendInviteJob = new SchedulerJobInfo(SendInviteMessageJob.class.getName(), "INSTAGRAM_JOBS");
        SchedulerJobInfo sendQuestionnaireJob = new SchedulerJobInfo(SendQuestionnaireJob.class.getName(), "TELEGRAM_JOBS", "0 0 10 * * ?");
        try {
            deleteJob(postFindingJob);
            deleteJob(sendInviteJob);
            deleteJob(sendQuestionnaireJob);
        } catch (Exception e) {
            throw new InstagramRuntimeException("Ошибка при включении инстаграм-бота: {0}", e);
        }
    }

    public void createJob(SchedulerJobInfo jobInfo) throws SchedulerException {
        Optional<SchedulerJobInfo> existingJob = schedulerRepository.findById(jobInfo.getJobClass());
        if (existingJob.isPresent()) {
            logger.error("Джоба с названием {} уже существует", jobInfo.getJobClass());
            throw new InstagramRuntimeException("Ошибка при создании джобы: данная джоба уже существует");
        } else {
            try {
                JobDetail jobDetail = JobBuilder
                        .newJob((Class<? extends Job>) Class.forName(jobInfo.getJobClass()))
                        .withIdentity(jobInfo.getJobClass(), jobInfo.getJobGroup())
                        .build();
                CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobInfo.getJobClass())
                        .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()))
                        .build();
                scheduler.scheduleJob(jobDetail, cronTrigger);
                schedulerRepository.save(jobInfo);
                logger.info("Джоб класса {} успешно запущен", jobInfo.getJobClass());
            } catch (ClassNotFoundException e) {
                logger.error("Class Not Found - {}", jobInfo.getJobClass(), e);
                throw new InstagramRuntimeException(MessageFormat.format("Неизвестная джоба:{0}", jobInfo.getJobClass()));
            } catch (SchedulerException e) {
                logger.error("Произошла ошибка при регистрации джоба {} ", jobInfo.getJobClass());
                throw e;
            }
        }
    }

    public void deleteJob(SchedulerJobInfo jobInfo) throws SchedulerException {
        Optional<SchedulerJobInfo> existingJob = schedulerRepository.findById(jobInfo.getJobClass());
        if (existingJob.isEmpty()) {
            logger.error("Джобы с названием {} не существует", jobInfo.getJobClass());
            throw new InstagramRuntimeException("Ошибка при удалении джобы: данной джобы не существует");
        } else {
            try {
                boolean isJobDeleted = scheduler.deleteJob(new JobKey(jobInfo.getJobClass(), jobInfo.getJobGroup()));
                if (isJobDeleted) {
                    logger.info("Джоб класса {} успешно удален", jobInfo.getJobClass());
                    schedulerRepository.delete(existingJob.get());
                } else {
                    logger.error("Неизвестная ошибка при удалении джоба {} ", jobInfo.getJobClass());
                    throw new InstagramRuntimeException(MessageFormat.format("Неизвестная ошибка при удалении джоба {0}", jobInfo.getJobClass()));
                }
            } catch (SchedulerException e) {
                logger.error("Ошибка удаления джоба {} ", jobInfo.getJobClass());
                throw e;
            }
        }
    }
}