package ru.tbcarus.spendingsb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@Table(name = "notes")
public class Note extends AbstractBaseEntity {

    private boolean read; // Прочитано?
    private LocalDateTime dateTime; // Дата создания
    private String title; // Заголовок
    private String text; // Текст уведомления
    private String email; // Кому уведомление

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Note() {

    }
}
