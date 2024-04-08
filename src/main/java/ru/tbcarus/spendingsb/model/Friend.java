package ru.tbcarus.spendingsb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private User user; // чей друг id
    private String userEmail; // чей друг e-mail
    private int friendId; // кто друг id
    private String friendEmail; // кто друг e-mail

    @Column(name = "color", columnDefinition = "varchar(255) default '#ffc107'")
    private String color = "#ffc107"; // цвет

    public Friend(User user, int friendId, String friendEmail) {
        this.user = user;
        this.userEmail = user.getEmail();
        this.friendId = friendId;
        this.friendEmail = friendEmail;
    }

    public Friend(User user, User friend) {
        this(user, friend.getId(), friend.getEmail());
    }

    public String toString() {
        return "Friend(userEmail=" + this.getUserEmail() + ", friendId=" + this.getFriendId() + ", friendEmail=" + this.getFriendEmail() + ", color=" + this.getColor() + ")";
    }
}
