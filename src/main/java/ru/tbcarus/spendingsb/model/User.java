package ru.tbcarus.spendingsb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    private String friends = ""; // строка с email-ами через пробел. 5 друзей МАКС

    private String friendsId = ""; // строка с id через пробел. Костыль, так как в тратах id пользователя. Может, потом стоит убрать список email-ов

    private boolean newNotify;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Friend> friendList;

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

    public List<String> getFriendsList() {
        return friends.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(friends.split(" ")));
    }

    public List<Integer> getFriendsIdList() {
        return friendsId.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.stream(friendsId.split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList()));
    }

    public void addFriend(String email) {
        List<String> friendsList = getFriendsList();
        if (friendsList.contains(email)) {
            return;
        }
        friends = friends.isEmpty() ? email : friends + " " + email;
    }

    public void addFriendId(int id) {
        List<Integer> friendsList = getFriendsIdList();
        if (friendsList.contains(id)) {
            return;
        }
        friendsId = friendsId.isEmpty() ? String.valueOf(id) : friendsId + " " + id;
    }

    public void addFriendsList(List<String> friendsList) {
        for (String friend : friendsList) {
            addFriend(friend);
        }
    }

    public void addFriendsIdList(List<Integer> friendsIdList) {
        for (int friendId : friendsIdList) {
            addFriendId(friendId);
        }
    }

    public void addFriends(String... friendsArray) {
        addFriendsList(Arrays.asList(friendsArray));
    }

    public void addFriendsId(Integer... friendsIdArray) {
        addFriendsIdList(Arrays.asList(friendsIdArray));
    }

    public void setFriendsList(List<String> friendsList) {
        StringBuilder sb = new StringBuilder();
        for (String email : friendsList) {
            sb.append(email);
            sb.append(" ");
        }
        this.friends = sb.toString().trim();
    }

    public void setFriendsIdList(List<Integer> friendsIdList) {
        StringBuilder sb = new StringBuilder();
        for (int id : friendsIdList) {
            sb.append(id);
            sb.append(" ");
        }
        this.friendsId = sb.toString().trim();
    }

    public void removeAllFriends() {
        friends = "";
    }

    public void removeAllFriendsId() {
        friendsId = "";
    }

    public void removeFriend(String email) {
        friends = friends.replace(email, "").replaceAll("\\s+", " ").trim();
    }

    public void removeFriendId(int id) {
        String idStr = String.valueOf(id);
        friendsId = friendsId.replace(idStr, "").replaceAll("\\s+", " ").trim();
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
        return !friendsId.isEmpty();
    }

    public boolean hasFriend(String email) {
        return getFriendsList().contains(email);
    }

    public boolean hasFriendId(int id) {
        return getFriendsIdList().contains(id);
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

//    public void addToFriendList(Friend friend) {
//        friendList.add(friend);
//    }
//
//    public void deleteFromFriendList(Friend friend) {
//        friendList.remove(friend);
//    }

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
