package ru.tbcarus.spendingsb.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.DataJpaPaymentRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.SecurityUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    DataJpaPaymentRepository paymentRepository;
    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    UserService userService;

    Sort sort = Sort.by(Sort.Direction.DESC, "date", "userID", "price");

    public List<Payment> getPayments(User user, LocalDate after, LocalDate before) {
        Specification<Payment> sp = filterByGroup(user);
        sp = sp.and(filterByDate(after, before));

        return sortList(paymentRepository.getPayments(sp, sort));
    }

    public List<Payment> getPaymentsByType(User user, PaymentType type, LocalDate after, LocalDate before) {
        Specification<Payment> sp = filterByGroup(user);
        sp = sp.and(filterByType(type));
        sp = sp.and(filterByDate(after, before));

        return sortList(paymentRepository.getPayments(sp, sort));
    }

    public Payment get(User user, int payId) {
        Specification<Payment> sp = filterByGroup(user);
        sp = sp.and(filterById(payId));
        Payment payment = paymentRepository.get(sp);
        if (payment == null) {
            throw new NotFoundException("Запись не найдена.");
        }
        return payment;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Payment> getAll() {
        return sortList(paymentRepository.getAll());
    }

    public Payment create(Payment payment, int userId) {
        payment.setUserID(userId);
        payment.setUser(userService.getById(SecurityUtil.authUserId()));
        return paymentRepository.save(payment, userId);
    }

    public List<Payment> createAll(List<Payment> list, int userId) {
        User user = userService.getById(SecurityUtil.authUserId());
        list.forEach(p -> p.setUserID(userId));
        list.forEach(p -> p.setUser(user));
        return paymentRepository.saveAll(list, userId);
    }

    public Payment update(User user, Payment payment) {
        Payment p = get(user, payment.getId());
        if (p != null) {
            p.paymentUpdate(payment);
        } else {
            throw new NotFoundException("Payment not found");
        }
//        payment.setUserID(user.getId());
//        payment.setUser(user);
        return paymentRepository.save(p, user.getId());
    }

    public void delete(User user, int id) {
        Specification<Payment> sp = filterByGroup(user);
        sp = sp.and(filterById(id));
        if (!paymentRepository.delete(sp)) {
            throw new NotFoundException();
        }
    }

    public Specification<Payment> filterByType(PaymentType type) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (type == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("type"), type);
            }
        };
    }

    public Specification<Payment> filterByUserId(Integer userId) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (userId == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("userID"), userId);
            }
        };
    }

    public Specification<Payment> filterByGroup(User user) {
        Specification<Payment> sp = filterByUserId(user.getId());
        if (user.isInGroup()) {
            for (Friend friend : user.getFriendsList()) {
                sp = sp.or(filterByUserId(friend.getFriendId()));
            }
        }
        return sp;
    }

    public Specification<Payment> filterById(Integer id) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (id == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("id"), id);
            }
        };
    }

    public Specification<Payment> filterByDate(LocalDate after, LocalDate before) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                LocalDate tempAfter = after == null ? LocalDate.parse("1900-01-01") : after;
                LocalDate tempBefore = before == null ? LocalDate.parse("3000-01-01") : before;
                return criteriaBuilder.between(root.get("date"), tempAfter, tempBefore);
            }
        };
    }

    private List<Payment> sortList(List<Payment> list) {
        Collections.sort(list);
        return list;
    }

//
//    public int getSumType(PaymentType type, int userId) {
//
//    }
}
