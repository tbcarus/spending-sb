package ru.tbcarus.spendingsb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "costs")
public class Payment extends AbstractBaseEntity {
    private PaymentType type; // Тип траты
    private int price; // Сумма траты
    private String description; // Описание траты
    private LocalDate date; // Дата совершения траты. Устанавливается автоматически или выбирается вручную
    private Integer userID; // Владелец траты

    @ManyToOne
    private User user;

    public Payment() {

    }

    public Payment(PaymentType type, int price, LocalDate date, Integer userID) {
        this(type, price, null, date, userID);
    }

    public Payment(PaymentType type, int price, String description, LocalDate date, Integer userID) {
        this.type = type;
        this.price = price;
        this.description = description;
        this.date = date;
        this.userID = userID;
    }

    public Payment(Integer id, PaymentType type, int price, String description, LocalDate date, Integer userID) {
        super(id);
        this.type = type;
        this.price = price;
        this.description = description;
        this.date = date;
        this.userID = userID;
    }


}
