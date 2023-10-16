package ru.tbcarus.spendingsb.controller.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.tbcarus.spendingsb.PaymentTestData;
import ru.tbcarus.spendingsb.UserTestData;
import ru.tbcarus.spendingsb.controller.AbstractControllerTest;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.service.PaymentService;

import java.time.LocalDate;
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

    @Test
    void get() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + PaymentTestData.PID5))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String stringContent = result.getResponse().getContentAsString();
        Payment actual = objectMapper.readValue(stringContent, Payment.class);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.P5);
    }

    @Test
    void getAll() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(AdminPaymentRestController.REST_URL))
                .andExpect(status().isOk())
                .andDo(print());
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentAsString, typeReference);
//        AssertionErrors.assertEquals("Возвращается не правильный результат при запросе GET " + REST_URL, PaymentTestData.getAllPayments(), actual);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.getAllPaymentsSorted());
    }

    @Test
    void getAllFilteredByNone() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/by"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String contentContent = result.getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentContent, typeReference);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.getAllPaymentsSorted());
    }

    @Test
    void getAllFilteredByType() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/by?type=GAS"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String contentContent = result.getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentContent, typeReference);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.getByType(PaymentType.GAS));
    }

    @Test
    void getAllFilteredByUser() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/by?userId=100002"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String contentContent = result.getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentContent, typeReference);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.getByUserId(UserTestData.SUPER_USER_ID));
    }

    @Test
    void getAllFilteredByDate() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/by?after=2023-01-30&before=2023-02-11"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String contentContent = result.getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentContent, typeReference);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.getByDateBetween(LocalDate.parse("2023-01-30"), LocalDate.parse("2023-02-11")));
    }

    @Test
    void getAllFiltered() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "/by?type=DINNER&userId=100002&after=2023-01-30&before=2023-02-11"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String contentContent = result.getResponse().getContentAsString();
        List<Payment> actual = objectMapper.readValue(contentContent, typeReference);
        Assertions.assertThat(actual).isEqualTo(PaymentTestData.getFiltered(PaymentType.DINNER, UserTestData.SUPER_USER_ID, LocalDate.parse("2023-01-30"), LocalDate.parse("2023-02-11")));
    }

    @Test
    void getNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + PaymentTestData.NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    void create() throws Exception {
        Payment newP = PaymentTestData.getNew();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(AdminPaymentRestController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newP)))
                .andExpect(status().isCreated())
                .andReturn();
        String stringContent = result.getResponse().getContentAsString();
        Payment resultPayment = objectMapper.readValue(stringContent, Payment.class);
        Payment actual = paymentService.get(resultPayment.getId(), resultPayment.getUserID());
        newP.setId(actual.getId());
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("user")
                .isEqualTo(newP);
    }

    @Test
    void update() throws Exception {
        Payment updated = PaymentTestData.getUpdated();
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + updated.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNoContent())
                .andReturn();
        Payment actual = paymentService.get(updated.getId(), updated.getUserID());
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("user")
                .isEqualTo(updated);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + PaymentTestData.PID4))
                .andExpect(status().isNoContent())
                .andDo(print());
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + PaymentTestData.PID4))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + PaymentTestData.NOT_FOUND))
                .andExpect(status().isNotFound());
    }
}