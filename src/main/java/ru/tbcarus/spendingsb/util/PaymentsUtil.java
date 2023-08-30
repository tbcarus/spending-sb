package ru.tbcarus.spendingsb.util;

import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentsUtil {

    // Преобразование листа трат в мапу
    public static Map<PaymentType, List<Payment>> getPaymentsMap(List<Payment> paymentList) {
        Map<PaymentType, List<Payment>> map = new HashMap<>();
        for (PaymentType pt : PaymentType.values()) {
            map.put(pt, new ArrayList<>());
        }
        for (Payment p : paymentList) {
            List<Payment> list = map.get(p.getType());
            list.add(p);
            map.put(p.getType(), list);
        }
        return map;
    }

    // Максимальный размер листа в мапе с листами
    public static int maxSize(Map<PaymentType, List<Payment>> map) {
        int max = 0;
        for (PaymentType pt : map.keySet()) {
            if (max < map.get(pt).size()) {
                max = map.get(pt).size();
            }
        }
        return max;
    }

    // Подсчёт суммы трат в листе
    public static int getSumByType(List<Payment> list) {
        int sum = 0;
        for (Payment p : list) {
            sum += p.getPrice();
        }
        return sum;
    }

    // Подсчёт сумм трат по типам в мапе
    public static Map<PaymentType, Integer> getSumMapByType(Map<PaymentType, List<Payment>> map) {
        Map<PaymentType, Integer> sumMap = new HashMap<>();
        for (PaymentType pt : map.keySet()) {
            sumMap.put(pt, getSumByType(map.get(pt)));
        }
        return sumMap;
    }

    // Подсчёт общей суммы трат в мапе
    public static int getSumAll(Map<PaymentType, Integer> map) {
        int sum = 0;
        for (PaymentType pt : map.keySet()) {
            sum += map.get(pt);
        }
        return sum;
    }
}
