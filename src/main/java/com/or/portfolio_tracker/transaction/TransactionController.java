package com.or.portfolio_tracker.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transactions", description = "Portfolio event history")
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;
    private final TransactionRepository repo;

    public TransactionController(TransactionService service, TransactionRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @Operation(summary = "Create transaction")
    @PostMapping
    public TransactionReadDTO create(@Valid @RequestBody TransactionCreateDTO dto) {
        return TransactionReadDTO.fromEntity(service.create(dto));
    }

    @Operation(summary = "List transactions (newest first)")
    @GetMapping
    public Page<TransactionReadDTO> list(@ParameterObject Pageable pageable) {
        return repo.findAllByOrderByTradeTimeDesc(pageable)
                .map(TransactionReadDTO::fromEntity);
    }
}