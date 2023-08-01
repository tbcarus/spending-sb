package ru.tbcarus.spendingsb.model;

public enum PaymentType {
    DINNER("Обед", false),
    CAR("Машина", true),
    GAS("Бензин", false),
    FOOD("Продукты", false),
    TRANSIT("Проезд", false),
    ENTERTAINMENT("Развлечения", true),
    CLOTH("Одежда", false),
    PHARMACY("Аптека", false),
    CHILDREN("Дети", true),
    OTHER("Прочее", true);

    private final String title;
    private final boolean descriptionOutput; // Признак вывода описания траты на странице или при экспорте данных в существующую структуру файла эксель

    PaymentType(String title, boolean b) {
        this.title = title;
        this.descriptionOutput = b;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDescriptionOutput() {
        return descriptionOutput;
    }
}
