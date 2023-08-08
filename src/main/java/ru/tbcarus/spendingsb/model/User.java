package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
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

    public User(Integer id, String name, String email, String password, Role... roles) {
        this(id, name, email,password,true, LocalDate.now(), roles);
    }
    public User(Integer id, String name, String email, String password, boolean enabled, LocalDate startPeriodDate, Role... roles) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.startPeriodDate = startPeriodDate;
        this.roles = Stream.of(roles).collect(Collectors.toSet());
    }

    public User() {
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
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
