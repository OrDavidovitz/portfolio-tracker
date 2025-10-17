package com.or.portfolio_tracker.holding;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Holdings", description = "CRUD for portfolio holdings")
@RestController
@RequestMapping("/holdings")
public class HoldingController {

    private final HoldingService service;

    public HoldingController(HoldingService service) {
        this.service = service;
    }

    /** Create → return read DTO so the client immediately knows the assigned id and denormalized asset info. */
    @Operation(summary = "Create holding")
    @PostMapping
    public ResponseEntity<HoldingReadDTO> create(@Valid @RequestBody HoldingCreateDTO body) {
        Holding created = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(HoldingReadDTO.fromEntity(created));
    }

    /** Paginated list → always return DTOs (never entities). */
    @Operation(summary = "List holdings",
            description = "Paginated & sortable holdings with denormalized asset info")
    @GetMapping
    public Page<HoldingReadDTO> list(
            @ParameterObject
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return service.list(pageable);
    }
    /** Get single holding by id. */
    @Operation(summary = "Get holding by id")
    @GetMapping("/{id}")
    public ResponseEntity<HoldingReadDTO> getById(@PathVariable Long id) {
        Holding h = service.getById(id);
        return ResponseEntity.ok(HoldingReadDTO.fromEntity(h));
    }

    /** Delete holding. */
    @Operation(summary = "Delete holding by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}