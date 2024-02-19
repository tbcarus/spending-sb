package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.tbcarus.spendingsb.util.ConfigUtil;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_requests")
public class EmailAction extends AbstractBaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user; // владелец запроса

    private String code; // код

    @Enumerated(EnumType.STRING)
    private EmailRequestType type; // тип запроса

    private LocalDateTime dateTime; // дата создания

    private boolean used; // была ли использована ссылка

    public EmailAction(EmailRequestType type) {
        this.type = type;
        this.dateTime = LocalDateTime.now();
        this.used = false;
    }

    public boolean isActive() {
        return dateTime.plusDays(ConfigUtil.DEFAULT_EXPIRED_DAYS).isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return !isActive();
    }

}
