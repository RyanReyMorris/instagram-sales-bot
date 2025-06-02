package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

/**
 * Enumeration of {@link Button}.
 */
public enum ButtonEnum {

    CONFIRM("-Подтверждаю-"),
    DECLINE("-Отклоняю-"),
    ENABLE("-Включить бота-"),
    DISABLE("-Отключить бота-"),
    POST_URL("-Пост в инстаграм-"),
    TEXT_PATTERN("-Ключевое слово-"),
    AUDIO("-Изменить аудио-"),
    LESSON_LINK("-Видео-урок-"),
    QUESTIONNAIRE_LINK("-Анкета-"),
    MASS_SENDING("-!Массовая рассылка!-"),
    JOB_CRON_EXPRESSION("-Периодичность работы бота-"),
    UPDATE("-Обновить статистику-");

    /**
     * Название кнопки
     */
    private final String buttonName;

    ButtonEnum(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getName() {
        return buttonName;
    }

    public String getCode() {
        return this.name();
    }
}
