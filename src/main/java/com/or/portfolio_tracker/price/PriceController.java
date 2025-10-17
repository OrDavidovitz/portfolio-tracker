package com.or.portfolio_tracker.price;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * DEV-only endpoint to set current prices in the in-memory provider.
 */
@Profile("dev")
@RestController
@RequestMapping("/prices")
public class PriceController {

    private final InMemoryPriceProvider provider;

    public PriceController(InMemoryPriceProvider provider) {
        this.provider = provider;
    }

    @Operation(summary = "Set current price (DEV)")
    @PostMapping
    public void set(@RequestParam String symbol, @RequestParam BigDecimal price) {
        provider.put(symbol, price);
    }
}