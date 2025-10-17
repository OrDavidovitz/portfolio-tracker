package com.or.portfolio_tracker.asset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerIT {

    @Autowired MockMvc mvc;

    @Test
    void createAndGet_shouldWork() throws Exception {
        String symbol = "T" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        var payload = """
          { "symbol": "%s", "assetType": "EQUITY" }
        """.formatted(symbol);

        // Create
        String location = mvc.perform(post("/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/assets/")))
                .andExpect(jsonPath("$.symbol").value(symbol))
                .andReturn().getResponse().getHeader("Location");

        // Get by Location
        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value(symbol))
                .andExpect(jsonPath("$.assetType").value("EQUITY"));

        // List page=0
        mvc.perform(get("/assets?size=5&page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}