package com.or.portfolio_tracker.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Example: AAPL asset id should exist in your seed data
    private static final long AAPL_ID = 1L;
    private static final long CASH_ID = 4L;

    @Test
    void testBuyTransaction() throws Exception {
        var payload = """
            {
              "assetId": %d,
              "kind": "BUY",
              "quantity": 2.0,
              "price": 190.00,
              "fee": 0,
              "tradeTime": "2025-10-17T10:00:00Z",
              "note": "Integration test buy"
            }
            """.formatted(AAPL_ID);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("BUY"))
                .andExpect(jsonPath("$.assetSymbol").value("AAPL"))
                .andExpect(jsonPath("$.quantity").value(2.0));
    }

    @Test
    void testSellTransaction() throws Exception {
        var payload = """
            {
              "assetId": %d,
              "kind": "SELL",
              "quantity": 1.0,
              "price": 210.00,
              "fee": 0,
              "tradeTime": "2025-10-17T11:00:00Z",
              "note": "Integration test sell"
            }
            """.formatted(AAPL_ID);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("SELL"))
                .andExpect(jsonPath("$.assetSymbol").value("AAPL"))
                .andExpect(jsonPath("$.price").value(210.00));
    }

    @Test
    void testDepositTransaction() throws Exception {
        var payload = """
            {
              "assetId": %d,
              "kind": "DEPOSIT",
              "cashAmount": 500.00,
              "fee": 0,
              "tradeTime": "2025-10-17T12:00:00Z",
              "note": "Deposit test"
            }
            """.formatted(CASH_ID);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("DEPOSIT"))
                .andExpect(jsonPath("$.cashAmount").value(500.00));
    }

    @Test
    void testWithdrawalTransaction() throws Exception {
        var payload = """
            {
              "assetId": %d,
              "kind": "WITHDRAWAL",
              "cashAmount": 200.00,
              "fee": 0,
              "tradeTime": "2025-10-17T13:00:00Z",
              "note": "Withdrawal test"
            }
            """.formatted(CASH_ID);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kind").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.cashAmount").value(200.00));
    }

    @Test
    void testInvalidBuyWithoutQuantity() throws Exception {
        var payload = """
            {
              "assetId": %d,
              "kind": "BUY",
              "price": 190.00,
              "fee": 0,
              "tradeTime": "2025-10-17T14:00:00Z",
              "note": "Invalid buy"
            }
            """.formatted(AAPL_ID);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidNegativeFee() throws Exception {
        var payload = """
            {
              "assetId": %d,
              "kind": "SELL",
              "quantity": 1.0,
              "price": 210.00,
              "fee": -5,
              "tradeTime": "2025-10-17T15:00:00Z",
              "note": "Invalid fee"
            }
            """.formatted(AAPL_ID);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}