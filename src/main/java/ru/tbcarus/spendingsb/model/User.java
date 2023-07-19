package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDate startPeriodDate; // Дата начала периода учёта трат
    private Role role;


    @OneToMany
    private List<Payment> payments;

    private String friends;
    @Transient
    private List<Integer> friendsIdList;

    public List<Integer> getFriendsIdList() {
        return Arrays.stream(friends.split(" "))
                .map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());
    }
}
