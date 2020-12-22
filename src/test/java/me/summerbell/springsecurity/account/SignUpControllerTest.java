package me.summerbell.springsecurity.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;


    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("_csrf")));
    }

    @Test
    void processSignUp() throws Exception{
        mockMvc.perform(post("/signup")
        .param("username", "jaen")
        .param("password", "1234")
        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void pw() throws Exception{
        Account account = new Account();
        account.setUsername("jaden");
        account.setPassword("123");
        account.setRole("USER");

        accountService.createNew(account);


        //todo : 데이터를 넣어놔야함
        mockMvc.perform(get("/signup/pw"))
                .andDo(print())
                .andExpect(status().isOk());

    }
}