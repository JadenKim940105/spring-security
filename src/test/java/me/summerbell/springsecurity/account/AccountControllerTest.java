package me.summerbell.springsecurity.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    /*@Test
    void index_anonymous() throws Exception {
        mockMvc.perform(get("/").with(anonymous())) // 익명접근
                .andDo(print())
                .andExpect(status().isOk());
    }*/

    @Test
    @WithAnonymousUser  // 어노테이션을 사용할 수 도 있다.
    void index_anonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void index_user() throws Exception{
        mockMvc.perform(get("/").with(user("jaden").roles("USER")))  // 목킹유저 사용
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void admin_user() throws Exception{
        mockMvc.perform(get("/admin").with(user("jaden").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void index_admin() throws Exception{
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void admin_admin() throws Exception{
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))  // 목킹유저 사용
                .andDo(print())
                .andExpect(status().isOk());
    }

}