package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
@Table(name = "friends")
public class Friend extends AbstractBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user; // чей друг id
    private String userEmail; // чей друг e-mail
    private int friendId; // кто друг id
    private String friendEmail; // кто друг e-mail

    @Column(name = "color", columnDefinition = "varchar(255) default '#ffc107'")
    private String color; // цвет

    public Friend(User user, String userEmail, int friendId, String friendEmail) {
        this.user = user;
        this.userEmail = userEmail;
        this.friendId = friendId;
        this.friendEmail = friendEmail;
    }
}
