package com.szymon.javaexcercise.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class SorterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIsMasked() throws Exception {
        HttpSession session = this.mockMvc.perform(post("/submitOne").param("bank", "test").param("number", "1234123412341234").param("date","Nov-2019")).andExpect(status().is3xxRedirection()).andReturn().getRequest().getSession();
        assert session != null;
        String body = this.mockMvc.perform(get("/view").session((MockHttpSession) session)).andReturn().getResponse().getContentAsString();
        System.out.println(body);
        assert body.contains("1234-xxxx-xxxx-xxxx");
        assert !body.contains("1234123412341234");
    }


    @Test
    public void testIsSorted() throws Exception {
        HttpSession session = this.mockMvc.perform(post("/submitOne").param("bank", "thisBankShouldBeFirst").param("number", "1234123412341234").param("date","Nov-2019")).andExpect(status().is3xxRedirection()).andReturn().getRequest().getSession();
        assert session != null;
        session = this.mockMvc.perform(post("/submitOne").session((MockHttpSession) session).param("bank", "thisBankShouldBeSecond").param("number", "1234123412341234").param("date","Nov-2018")).andExpect(status().is3xxRedirection()).andReturn().getRequest().getSession();
        assert session != null;
        String body = this.mockMvc.perform(get("/view").session((MockHttpSession) session)).andReturn().getResponse().getContentAsString();
        System.out.println(body);
        assert body.indexOf("thisBankShouldBeFirst")<body.indexOf("thisBankShouldBeSecond");
    }
}