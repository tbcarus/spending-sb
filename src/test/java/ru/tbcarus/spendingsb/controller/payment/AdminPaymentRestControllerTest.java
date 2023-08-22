package ru.tbcarus.spendingsb.controller.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.tbcarus.spendingsb.PaymentTestData;
import ru.tbcarus.spendingsb.controller.AbstractControllerTest;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.service.PaymentService;
import org.springframework.test.util.AssertionErrors;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
class AdminPaymentRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminPaymentRestController.REST_URL + '/';
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final TypeReference<List<Payment>> typeReference = new TypeReference<List<Payment>>() {
    };

    @Autowired
    PaymentService paymentService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void get() {
    }

    @Test
    void getAll() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(AdminPaymentRestController.REST_URL))
                .andExpect(status().isOk())
                .andDo(print());
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentAsString, typeReference);
        AssertionErrors.assertEquals("Возвращается не правильный результат при запросе GET " + REST_URL, PaymentTestData.getAllPayments(), actual);
    }

    @Test
    void getPayments() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}