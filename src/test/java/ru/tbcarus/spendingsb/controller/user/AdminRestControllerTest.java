package ru.tbcarus.spendingsb.controller.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.tbcarus.spendingsb.controller.AbstractControllerTest;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;
import java.util.List;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.tbcarus.spendingsb.UserTestData.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
//@WebMvcTest
class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {
    };

    @Autowired
    private UserService userService;

    @Test
    void getAll() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(AdminRestController.REST_URL))
                .andExpect(status().isOk())
                .andDo(print());
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        List<User> actual = objectMapper.readValue(contentAsString, typeReference);
        assertEquals("Возвращается не правильный результат при запросе GET " + REST_URL, List.of(admin, superUser, user), actual);
    }

    @Test
    void getById() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Admin"));
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        User actual = objectMapper.readValue(contentAsString, User.class);
        assertEquals("Вернулся неправильный объект при запросе GET " + REST_URL + ADMIN_ID, admin, actual);
    }

    @Test
    void getByIdNotFound() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getByEmail() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by-email?email=" + user.getEmail()))
                .andExpect(status().isOk())
                .andDo(print());
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        User actual = objectMapper.readValue(contentAsString, User.class);
        assertEquals("Вернулся неправильный объект при запросе GET " + REST_URL + "by-email?email=" + user.getEmail(), user, actual);

    }

    @Test
    void getEmailIdNotFound() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "by-email?email=" + "NotFound@notexist.ggg"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void create() throws Exception {
        User newUser = getNew();
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post(AdminRestController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        User actual = objectMapper.readValue(contentAsString, User.class);
        assertEquals("Вернулся неправильный результат при запросе POST " + AdminRestController.REST_URL, getNew(), actual);
    }

    @Test
    void update() throws Exception {
        User updated = getUpdated();
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNoContent());
        assertEquals("Вернулся неправильный результат при запросе PUT " + REST_URL + USER_ID, getUpdated(), userService.getById(USER_ID));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + SUPER_USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        mockMvc.perform((MockMvcRequestBuilders.get(REST_URL + SUPER_USER)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}