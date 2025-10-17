package com.or.portfolio_tracker.asset;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springdoc.core.annotations.ParameterObject;

import java.net.URI;

/**
 * REST controller exposing CRUD endpoints for Assets.
 * Uses DTOs to separate API layer from persistence layer.
 */
@Tag(name = "Assets", description = "CRUD for investment assets")
@RestController
@Validated
@RequestMapping("/assets")
public class AssetController {

    private final AssetService service;

    public AssetController(AssetService service) {
        this.service = service;
    }

    @Operation(summary = "Create asset")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Symbol already exists")
    })
    @PostMapping
    public ResponseEntity<AssetReadDTO> create(@Valid @RequestBody AssetCreateDTO body) {
        Asset created = service.add(body);
        AssetReadDTO dto = AssetReadDTO.fromEntity(created);

        // 201 + Location header with URI of new resource
        return ResponseEntity
                .created(URI.create("/assets/" + created.getId()))
                .body(dto);
    }

    @Operation(summary = "List assets", description = "Paginated & sortable list of assets")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping
    public Page<AssetReadDTO> list(
            @ParameterObject
            @PageableDefault(size = 20, sort = "symbol", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return service.list(pageable).map(AssetReadDTO::fromEntity);
    }

    @Operation(summary = "Get asset by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssetReadDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(AssetReadDTO.fromEntity(service.getById(id)));
    }

    @Operation(summary = "Get asset by symbol")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    @GetMapping("/by-symbol/{symbol}")
    public ResponseEntity<AssetReadDTO> getBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(AssetReadDTO.fromEntity(service.getBySymbol(symbol)));
    }

    @Operation(summary = "Replace asset", description = "PUT = full replacement. Provide both symbol and assetType.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "409", description = "Symbol already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AssetReadDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AssetUpdateDTO body) {
        Asset updated = service.update(id, body);
        return ResponseEntity.ok(AssetReadDTO.fromEntity(updated));
    }

    @Operation(summary = "Delete asset")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}