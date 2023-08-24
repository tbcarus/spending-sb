package ru.tbcarus.spendingsb;

import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.tbcarus.spendingsb.model.AbstractBaseEntity.START_SEQ;

public class PaymentTestData {
    public static final MatcherFactory.Matcher<Payment> PAYMENT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Payment.class, "user");

    public static final int NOT_FOUND = 1;

    public static final int PID1 = START_SEQ + 3;
    public static final int PID2 = START_SEQ + 4;
    public static final int PID3 = START_SEQ + 5;
    public static final int PID4 = START_SEQ + 6;
    public static final int PID5 = START_SEQ + 7;
    public static final int PID6 = START_SEQ + 8;
    public static final int PID7 = START_SEQ + 9;
    public static final int PID8 = START_SEQ + 10;
    public static final int PID9 = START_SEQ + 11;
    public static final int PID10 = START_SEQ + 12;
    public static final int PID11 = START_SEQ + 13;
    public static final int PID12 = START_SEQ + 14;

    public static final Payment P1 = new Payment(PID1, PaymentType.DINNER, 250, "", LocalDate.parse("2023-02-01"), UserTestData.USER_ID);
    public static final Payment P2 = new Payment(PID2, PaymentType.CAR, 1800, "Антикор", LocalDate.parse("2023-01-25"), UserTestData.USER_ID);
    public static final Payment P3 = new Payment(PID3, PaymentType.GAS, 3000, "", LocalDate.parse("2023-01-30"), UserTestData.USER_ID);
    public static final Payment P4 = new Payment(PID4, PaymentType.OTHER, 4200, "Озон", LocalDate.parse("2023-02-03"), UserTestData.USER_ID);
    public static final Payment P5 = new Payment(PID5, PaymentType.FOOD, 4000, "", LocalDate.parse("2023-02-12"), UserTestData.USER_ID);
    public static final Payment P6 = new Payment(PID6, PaymentType.DINNER, 280, "", LocalDate.parse("2023-02-01"), UserTestData.ADMIN_ID);
    public static final Payment P7 = new Payment(PID7, PaymentType.TRANSIT, 500, "", LocalDate.parse("2023-02-11"), UserTestData.ADMIN_ID);
    public static final Payment P8 = new Payment(PID8, PaymentType.ENTERTAINMENT, 3600, "", LocalDate.parse("2023-02-18"), UserTestData.ADMIN_ID);
    public static final Payment P9 = new Payment(PID9, PaymentType.OTHER, 4100, "Озон", LocalDate.parse("2023-02-04"), UserTestData.SUPER_USER_ID);
    public static final Payment P10 = new Payment(PID10, PaymentType.FOOD, 1564, "", LocalDate.parse("2023-02-11"), UserTestData.SUPER_USER_ID);
    public static final Payment P11 = new Payment(PID11, PaymentType.DINNER, 320, "", LocalDate.parse("2023-02-01"), UserTestData.SUPER_USER_ID);
    public static final Payment P12 = new Payment(PID12, PaymentType.TRANSIT, 1500, "Метро", LocalDate.parse("2023-02-11"), UserTestData.SUPER_USER_ID);

    public static List<Payment> getAllPaymentsSorted() {
        // Sorted by date after by userID after price ASC
        List<Payment> allPayments = new ArrayList<>(List.of(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12));
        Collections.sort(allPayments);
        return allPayments;
    }

    public static List<Payment> getByType(PaymentType type) {
        List<Payment> allPayments = new ArrayList<>(List.of(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12));
        List<Payment> filteredList = allPayments.stream().filter(p -> p.getType() == type).collect(Collectors.toList());
        Collections.sort(filteredList);
        return filteredList;
    }

    public static List<Payment> getByUserId(int userId) {
        List<Payment> allPayments = new ArrayList<>(List.of(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12));
        List<Payment> filteredList = allPayments.stream().filter(p -> p.getUserID() == userId).collect(Collectors.toList());
        Collections.sort(filteredList);
        return filteredList;
    }

    public static List<Payment> getByDateBetween(LocalDate after, LocalDate before) {
        List<Payment> allPayments = new ArrayList<>(List.of(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12));
        List<Payment> filteredList = allPayments
                .stream()
                .filter(p -> (p.getDate().isAfter(after) || p.getDate().isEqual(after))
                        && (p.getDate().isBefore(before) || p.getDate().isEqual(before)))
                .collect(Collectors.toList());
        Collections.sort(filteredList);
        return filteredList;
    }

    public static List<Payment> getFiltered(PaymentType type, int userId, LocalDate after, LocalDate before) {
        List<Payment> allPayments = new ArrayList<>(List.of(P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12));
        List<Payment> filteredList = allPayments
                .stream()
                .filter(p -> p.getType() == type
                        && p.getUserID() == userId
                        && (p.getDate().isAfter(after) || p.getDate().isEqual(after))
                        && (p.getDate().isBefore(before) || p.getDate().isEqual(before)))
                .collect(Collectors.toList());
        Collections.sort(filteredList);
        return filteredList;
    }

    public static Payment getNew() {
        return new Payment(null, PaymentType.CLOTH, 5500, "Брюки", LocalDate.parse("2023-04-08"), 100001);
    }

    public static Payment getUpdated() {
        Payment updated = new Payment(P9);
        updated.setType(PaymentType.ENTERTAINMENT);
        updated.setDescription("Хоккей");
        updated.setPrice(1500);
        updated.setDate(LocalDate.parse("2023-02-05"));
        return updated;
    }
}
