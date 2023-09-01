package ru.tbcarus.spendingsb.controller.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.util.PaymentsUtil;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/payments")
public class PaymentController extends AbstractPaymentController {

    @GetMapping()
    public String getAll(Model model) {
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(super.getAll());
        Map<PaymentType, Integer> sumMapByType = PaymentsUtil.getSumMapByType(paymentsMap);
        model.addAttribute("PaymentsMap", paymentsMap);
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("maxSize", PaymentsUtil.maxSize(paymentsMap));
        model.addAttribute("sumMapByType", sumMapByType);
        model.addAttribute("sumAll", PaymentsUtil.getSumAll(sumMapByType));
        return "list";
    }

    @GetMapping("/{id}")
    public String get(Model model, @PathVariable int id) {

        return "edit";
    }
}
