package org.refit.spring.ceo.controller;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.ceo.service.CeoService;
import org.refit.spring.config.RootConfig;
import org.refit.spring.config.ServletConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        RootConfig.class,
        ServletConfig.class
})
@Log4j
class CeoControllerTest {

    @Autowired
    CeoService ceoService;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    void getListUndone() throws Exception {
        String response = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/ceo/undone")
                                .accept("application/json")
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("응답 결과 : " + response);
    }
}