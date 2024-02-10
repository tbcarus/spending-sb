package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@Table(name = "notes")
public class Note extends AbstractBaseEntity {

    @Enumerated(EnumType.STRING)
    private NoteType type; // тип уведомления
    private boolean read; // Прочитано?
    private LocalDateTime dateTime; // Дата создания
    private String title; // Заголовок
    private String text; // Текст уведомления
    private String email; // Кому уведомление

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // отправитель

    public Note() {

    }
}
