package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "costs")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;
}
