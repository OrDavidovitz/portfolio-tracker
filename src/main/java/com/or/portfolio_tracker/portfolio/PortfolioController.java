package com.or.portfolio_tracker.portfolio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HTTP API for portfolio insights (summary and per-asset breakdown).
 */
@Tag(name = "Portfolio", description = "Portfolio summary & insights")
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService service;

    public PortfolioController(PortfolioService service) {
        this.service = service;
    }

    @Operation(
            summary = "Portfolio summary",
            description = "Returns distinct positions count, total quantity/cost, current value and unrealized PnL."
    )
    @GetMapping("/summary")
    public PortfolioSummaryDTO summary() {
        return service.summary();
    }

    @Operation(
            summary = "Per-asset breakdown",
            description = "Aggregated quantity/cost/average and optional live PnL per symbol."
    )
    @GetMapping("/summary/per-asset")
    public List<AssetPositionSummaryDTO> perAsset() {
        return service.perAsset();
    }
}