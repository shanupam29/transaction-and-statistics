package com.n26.test.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.controller.StatisticsController;
import com.n26.model.Statistics;
import com.n26.service.StatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper;

    @MockBean
    private StatisticsService statisticsService;

    @Autowired

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    /**
     * - When :  Retrieve all transaction in last 60 seconds.
     * - Given : NA
     * - Expected :  Return Success response with status success.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_invalid_json_request_ReturnSuccess() throws Exception {
        Statistics expectedStatistics =new Statistics("167.93","83.97","123.22","44.71",2L);
        given(statisticsService.retrieveStatistics()).willReturn(expectedStatistics);
        this.mvc.perform(get("/statistics")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString
                        (expectedStatistics)));
    }

}
