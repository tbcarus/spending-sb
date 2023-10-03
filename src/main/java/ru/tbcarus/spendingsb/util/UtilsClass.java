package ru.tbcarus.spendingsb.util;

import lombok.extern.slf4j.Slf4j;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Утильный класс
@Slf4j
public class UtilsClass {

    public static int toInt (String s) {
        int result = Integer.MIN_VALUE;
        try {
            result = (int) Double.parseDouble(s.replace(',', '.'));
        } catch (Exception exc) {
            log.warn("Ошибка парсинга цены {}", s);
            exc.printStackTrace();
        }
        return result;
    }

    // Проверка строки на недопустимые символы
    public static String checkDescription(String s) {

        return s;
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
