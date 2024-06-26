package ru.tbcarus.spendingsb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.tbcarus.spendingsb.annotation.NotZero;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "costs")
public class Payment extends AbstractBaseEntity implements Comparable<Payment> {
    @Enumerated(EnumType.STRING)
    private PaymentType type; // Тип траты

    @NotZero(message = "Не должно быть равно 0")
    private int price; // Сумма траты
    @Size(max = 256, message = "Максимальная длина 256 символов")
    private String description; // Описание траты
    private LocalDate date; // Дата совершения траты. Устанавливается автоматически или выбирается вручную
    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer userID; // Владелец траты

    @ManyToOne
    @JsonBackReference
    private User user;

    public Payment() {

    }

    public Payment(LocalDate date) {
        this.date = date;
    }

    public Payment(Payment p) {
        this.id = p.id;
        this.type = p.type;
        this.price = p.price;
        this.description = p.description;
        this.date = p.date;
        this.userID = p.userID;
    }

    public Payment(PaymentType type, int price, String description, LocalDate date) {
        this(type, price, description, date, null);
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

    public void paymentUpdate(Payment p) {
        this.type = p.getType();
        this.price = p.getPrice();
        this.description = p.getDescription();
        this.date = p.getDate();
    }

    @Override
    public int compareTo(Payment o) {
        int result = -1 * this.date.compareTo(o.getDate());
        if (result != 0) {
            return result;
        } else {
            result = this.userID - o.userID;
            if (result != 0) {
                return result;
            } else {
                return this.price - o.getPrice();
            }
        }
    }
}
