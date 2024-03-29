package ru.tbcarus.spendingsb.model;

import ru.tbcarus.spendingsb.util.ConfigUtil;
import ru.tbcarus.spendingsb.util.UserUtil;

public enum ErrorType {
    ALREADY_IN_GROUP("Уже есть в группе"),
    HAS_GROUP("Пользователь состоит в другой группе"),
    TOO_MUCH_FRIENDS("В группе уже " + UserUtil.DEFAULT_MAX_FRIENDS + " пользователей"),
    TOO_MUCH_INVITES("Общее количество друзей и отправленных приглашений не может быть более " + UserUtil.DEFAULT_MAX_FRIENDS),
    PERIOD_EXPIRED("Вышел срок (" + ConfigUtil.getStringDefaultDays() + " ) действия запроса. Пожалуйста, сделайте новый запрос."),
    NOT_FOUND("Запись не найдена"),
    DO_NOT_MATCH("Не совпадают"),
    WRONG_LENGTH("Неверная длина"),
    TOO_MUCH_REPEAT_REQUESTS("Повторный запрос возможен через " + ConfigUtil.getStringDefaultDays());

    private final String title;

    ErrorType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
