package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails, HasId {
    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    protected Integer id;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 128, message = "Длина имени от 2 до 128")
    private String name;

    @Email(message = "Неверный формат почты")
    @NotBlank(message = "Не может быть пустым")
    @Size(max = 128, message = "Максимальный размер 128")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 4, max = 128, message = "Длина от 4 до 128")
    private String password;

    private boolean enabled;

    @NotNull(message = "Start date cannot be blank.")
    private LocalDate startPeriodDate; // Дата начала периода учёта трат

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    private String friends = ""; // строка с email-ами через пробел. 5 друзей МАКС

    private boolean newNotify;

    public User(String email, String password, Collection<Role> roles) {
        this("new_" + email, email, password, true, LocalDate.now().withDayOfMonth(1), new HashSet<>(roles));
    }

    public User(String name, String email, String password, LocalDate startPeriodDate) {
        this(name, email, password, true, startPeriodDate, new HashSet<Role>());
    }

    public User(String name, String email, String password, boolean enabled, LocalDate startPeriodDate) {
        this(name, email, password, enabled, startPeriodDate, new HashSet<Role>());
    }

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
        this(id, name, email, password, true, LocalDate.now(), Arrays.asList(roles));
    }

    public User(Integer id, String name, String email, String password, LocalDate startPeriodDate, Role... roles) {
        this(id, name, email, password, true, startPeriodDate, Arrays.asList(roles));
    }

    public User(Integer id, String name, String email, String password, boolean enabled, LocalDate startPeriodDate, Collection<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.startPeriodDate = startPeriodDate;
        this.roles = new HashSet<>(roles);
    }

    public User() {
        this.startPeriodDate = LocalDate.now().withDayOfMonth(1);
        this.roles = new HashSet<Role>(Arrays.asList(Role.USER, Role.SUPERUSER));
        this.enabled = true;
    }

    public List<String> getFriendsList() {
        return Arrays.asList(friends.split(" "));
    }

    public void addFriend(String email) {
        List<String> friendsList = getFriendsList();
        if (friendsList.contains(email)) {
            return;
        }
        friends = friends.isEmpty() ? email : friends + " " + email;
    }

    public void addFriends(String... friendsArray) {
        addFriendsList(Arrays.asList(friendsArray));
    }

    public void addFriendsList(List<String> friendsList) {
        for(String friend : friendsList) {
            addFriend(friend);
        }
    }

    public void setFriends(List<String> friendsList) {
        StringBuilder sb = new StringBuilder();
        for (String email : friendsList) {
            sb.append(email);
            sb.append(" ");
        }
        this.friends = sb.toString().trim();
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Дата окончания периода учёта трат. Период в программе принят равным 1 месяц с начала периода
    public LocalDate getEndPeriodDate() {
        return startPeriodDate.plusMonths(1).minusDays(1);
    }

    public boolean isUser() {
        return roles.contains(Role.USER);
    }

    public boolean isSuperUser() {
        return roles.contains(Role.SUPERUSER);
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public void setEnabled() {
        this.enabled = true;
    }

    public void setDisabled() {
        this.enabled = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
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
