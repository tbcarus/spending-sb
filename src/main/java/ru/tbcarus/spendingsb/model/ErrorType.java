package ru.tbcarus.spendingsb.model;

public enum ErrorType {
    ALREADY_IN_GROUP("Уже есть в группе"),
    HAS_GROUP("Пользователь состоит в другой группе"),
    TOO_MUCH("В группе уже более 5 пользователей");

    private final String title;

    ErrorType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
