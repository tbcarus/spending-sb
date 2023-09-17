package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "users")
public class User extends AbstractBaseEntity {
    public static final int START_SEQ = 100000;

    private String name;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDate startPeriodDate; // Дата начала периода учёта трат

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    private Set<Role> roles;

    public User(String name, String email, String password, boolean enabled, LocalDate startPeriodDate, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.startPeriodDate = startPeriodDate;
        this.roles = roles;
    }

    public User(User u) {
        this(u.id, u.name, u.email, u.password, u.enabled, u.startPeriodDate, u.roles);
    }

    public User(Integer id, String name, String email, String password, Role... roles) {
        this(id, name, email,password,true, LocalDate.now(), Arrays.asList(roles));
    }

    public User(Integer id, String name, String email, String password, LocalDate startPeriodDate, Role... roles) {
        this(id, name, email,password,true, startPeriodDate, Arrays.asList(roles));
    }

    public User(Integer id, String name, String email, String password, boolean enabled, LocalDate startPeriodDate, Collection<Role> roles) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.startPeriodDate = startPeriodDate;
        this.roles = new HashSet<>(roles);
    }

    public User() {
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

    // Дата окончания периода учёта трат. Период в программе принят равным 1 месяц с начала периода
    public LocalDate getEndPeriodDate() {
        return startPeriodDate.plusMonths(1).minusDays(1);
    }

//    @OneToMany
//    private List<Payment> payments;
//
//    private String family;
//    @Transient
//    private List<Integer> familyIdList;
//
//    public List<Integer> getFamiyIdList() {
//        return Arrays.stream(family.split(" "))
//                .map(s -> Integer.parseInt(s))
//                .collect(Collectors.toList());
//    }
}
