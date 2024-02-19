package ru.tbcarus.spendingsb.model;

import ru.tbcarus.spendingsb.util.ConfigUtil;
import ru.tbcarus.spendingsb.util.UserUtil;

public enum ErrorType {
    ALREADY_IN_GROUP("Уже есть в группе"),
    HAS_GROUP("Пользователь состоит в другой группе"),
    TOO_MUCH_FRIENDS("В группе уже " + UserUtil.DEFAULT_MAX_FRIENDS + " пользователей"),
    TOO_MUCH_INVITES("Общее количество друзей и отправленных приглашений не может быть более " + UserUtil.DEFAULT_MAX_FRIENDS),
    PERIOD_EXPIRED("Вышел срок (" + ConfigUtil.DEFAULT_EXPIRED_DAYS + " дня) действия запроса. Пожалуйста, сделайте новый запрос."),
    NOT_FOUND("Запись не найдена");

    private final String title;

    ErrorType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
