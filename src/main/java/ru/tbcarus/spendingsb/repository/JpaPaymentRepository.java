package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;

import java.util.List;

@Transactional
public interface JpaPaymentRepository extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Payment> {
    List<Payment> findAllByUserId(int userId);
    List<Payment> findAllByUserIdAndType(PaymentType type, int userId);

    @Query("SELECT p from Payment p ORDER BY p.date DESC, p.userID, p.price")
    List<Payment> getAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM Payment p WHERE p.id=:id AND p.userID=:userId")
    int delete(@Param("id") int id, @Param("userId") int userId);

//    @Query("SELECT p from Payment p ORDER BY p.date, p.userID, p.price")
//    List<Payment> getAll(Specification<Payment> specification);

//    @Query("SELECT p FROM Payment p WHERE p.userID = :userId AND p.date >= :after AND p.date <= :brfore")
//    List<Payment> getBetween(LocalDate after, LocalDate before, int userId);

//    @Query("SELECT p FROM Payment p WHERE p.userID = :userId AND p.type = :type AND p.date >= :after AND p.date <= :brfore")
//    List<Payment> getBetweenByType(PaymentType type, LocalDate after, LocalDate before, int userId);
}
