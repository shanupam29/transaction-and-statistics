package com.n26.test.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.controller.TransactionController;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    private static final long OFFSET_TIME_SUCCESS = 10000;
    private static final long OFFSET_TIME_FUTURE = -60000;
    private static final long OFFSET_TIME_SIXTY_SECONDS = 60000;


    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    /**
     * - When :  Valid Transaction Object with amount and transaction date in UTC within 60s.
     * - Given : Valid Transaction
     * - Expected :  Return Success response with status created.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_ReturnSuccess() throws Exception {
        Instant timestamp = Instant.now().minusMillis(OFFSET_TIME_SUCCESS);
        Transaction transaction = buildTransaction("123.3361", DateTimeFormatter.ISO_INSTANT.format(timestamp));
        this.mvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(transaction))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    /**
     * - When :  InValid Transaction Object with amount and transaction date in future.
     * - Given : InValid Transaction with date in future
     * - Expected :  Return status response 422.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_future_transaction_date_Return422() throws Exception {
        Instant timestamp = Instant.now().minusMillis(OFFSET_TIME_FUTURE);
        Transaction transaction = buildTransaction("123.3361", DateTimeFormatter.ISO_INSTANT.format(timestamp));
        this.mvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(transaction))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
    /**
     * - When :  InValid Transaction Object with amount as not a number.
     * - Given : InValid Transaction with amount as not a number.
     * - Expected :  Return status response 422.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_amount_not_a_number_Return422() throws Exception {
        Instant timestamp = Instant.now().minusMillis(OFFSET_TIME_SUCCESS);
        Transaction transaction = buildTransaction("hello world", DateTimeFormatter.ISO_INSTANT.format(timestamp));
        this.mvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(transaction))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }
    /**
     * - When :  InValid Transaction Object with null amount.
     * - Given : InValid Transaction with null amount.
     * - Expected :  Return status response 422.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_null_amount_ReturnBadRequest() throws Exception {
        Instant timestamp = Instant.now().minusMillis(OFFSET_TIME_SUCCESS);
        Transaction transaction = buildTransaction(null, DateTimeFormatter.ISO_INSTANT.format(timestamp));
        this.mvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(transaction))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    /**
     * - When :  Valid Transaction Object with transaction date older than sixty seconds.
     * - Given : Valid Transaction with older than sixty seconds
     * - Expected :  Return status response 204- No Content.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_older_than_sixty_seconds_ReturnNoContent() throws Exception {
        Instant timestamp = Instant.now().minusMillis(OFFSET_TIME_SIXTY_SECONDS);
        Transaction transaction = buildTransaction("879.1122", DateTimeFormatter.ISO_INSTANT.format(timestamp));
        this.mvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(transaction))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    /**
     * - When :  Invalid json request.
     * - Given : Invalid json request with a plain text message in the body.
     * - Expected :  Return status response 400- Bad Request.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_invalid_json_request_ReturnBadRequest() throws Exception {
        this.mvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("Plain Text Message")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    /**
     * - When :  Delete/Reset all the transactions.
     * - Given : No body
     * - Expected :  Return Success status response 204-No Content.
     *
     * @throws Exception
     */
    @Test
    public void test_Transaction_delete_transaction() throws Exception {
        this.mvc.perform(delete("/transaction")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



    private Transaction buildTransaction(String amount, String timestamp) {
        return new Transaction(amount,timestamp);
    }
}
