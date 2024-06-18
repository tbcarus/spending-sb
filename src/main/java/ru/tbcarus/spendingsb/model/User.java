package ru.tbcarus.spendingsb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private boolean banned;

    @NotNull(message = "Start date cannot be blank.")
    private LocalDate startPeriodDate; // Дата начала периода учёта трат

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    private boolean newNotify;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Friend> friendsList;

    public User() {
        this.startPeriodDate = LocalDate.now().withDayOfMonth(1);
        this.roles = new HashSet<Role>(Arrays.asList(Role.USER, Role.SUPERUSER));
        this.enabled = false;
        this.banned = false;
    }

    public User(String email, String password, Collection<Role> roles) {
        this("new_" + email, email, password, false, LocalDate.now().withDayOfMonth(1), new HashSet<>(roles));
    }

    public User(String name, String email, String password, LocalDate startPeriodDate) {
        this(name, email, password, false, startPeriodDate, new HashSet<Role>());
    }

    public User(String name, String email, String password, boolean enabled, LocalDate startPeriodDate) {
        this(name, email, password, enabled, startPeriodDate, new HashSet<Role>());
    }

    public User(String name, String email, String password, boolean enabled, LocalDate startPeriodDate, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.banned = false;
        this.startPeriodDate = startPeriodDate;
        this.roles = roles;
    }

    public User(User u) {
        this(u.id, u.name, u.email, u.password, u.enabled, u.startPeriodDate, u.roles);
    }

    public User(Integer id, String name, String email, String password, Role... roles) {
        this(id, name, email, password, false, LocalDate.now(), Arrays.asList(roles));
    }

    public User(Integer id, String name, String email, String password, LocalDate startPeriodDate, Role... roles) {
        this(id, name, email, password, false, startPeriodDate, Arrays.asList(roles));
    }

    public User(Integer id, String name, String email, String password, boolean enabled, LocalDate startPeriodDate, Collection<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.banned = false;
        this.startPeriodDate = startPeriodDate;
        this.roles = new HashSet<>(roles);
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

    public void addRole(Role role) {
        if (!roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public void removeRole(Role role) {
        if (roles.contains(role)) {
            roles.remove(role);
        }
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

    public boolean isInGroup() {
        return !friendsList.isEmpty();
    }

    public void setEnabled() {
        this.enabled = true;
    }

    public void setDisabled() {
        this.enabled = false;
    }

    public void ban() {
        this.banned = true;
    }

    public void unban() {
        this.banned = false;
    }

    public void setFriendsList(List<Friend> list) {
        this.friendsList.clear();
        if (list != null) {
            this.friendsList.addAll(list);
        }
    }

    public void addFriend(Friend friend) {
        friendsList.add(friend);
        friend.setUser(this);
    }

    public void deleteFriend(Friend friend) {
        friendsList.remove(friend);
        friend.setUser(null);
    }

    public void deleteFriend(String email) {
        for (Friend f : friendsList) {
            if (f.getFriendEmail().equals(email)) {
                friendsList.remove(f);
                f.setUser(null);
                break;
            }
        }
    }

    public void removeAllFriends() {
        friendsList.clear();
    }

    public boolean hasFriend(int id) {
        return friendsList.stream().anyMatch(f -> f.getFriendId() == id);
    }

    public boolean hasFriend(String email) {
        return friendsList.stream().anyMatch(f -> f.getFriendEmail().equals(email));
    }

    public boolean isCurrentPeriod() {
        return startPeriodDate.minusDays(1).isBefore(LocalDate.now()) &&
                startPeriodDate.plusMonths(1).isAfter(LocalDate.now());
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

    public String toString() {
        return "User(id=" + id + ", name=" + name + ", email=" + email + ", enabled=" + enabled + ", banned=" + banned + ", startPeriodDate=" + startPeriodDate + ", roles=" + roles + ", newNotify=" + newNotify + ")";
    }
}
