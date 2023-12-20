package ru.tbcarus.spendingsb.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
public class Note extends AbstractBaseEntity {

    private boolean read; // Прочитано?
    private LocalDateTime dateTime; // Дата создания
    private String title; // Заголовок
    private String text; // Текст уведомления
    private String email; // Кому уведомление

    public Note() {

    }
}
