package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findAllByUserId(int userId);
    List<Payment> findAllByUserIdAndType(PaymentType type, int userId);

    @Query("SELECT p FROM Payment p WHERE p.userID = :userId AND p.date >= :after AND p.date <= :brfore")
    List<Payment> getBetween(LocalDate after, LocalDate before, int userId);
}
