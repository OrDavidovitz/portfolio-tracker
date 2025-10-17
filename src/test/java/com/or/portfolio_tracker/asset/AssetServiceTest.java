package com.or.portfolio_tracker.asset;

import com.or.portfolio_tracker.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock AssetRepository repo;
    @InjectMocks AssetService service;

    private AssetCreateDTO createDto(String symbol, AssetType type) {
        AssetCreateDTO dto = new AssetCreateDTO();
        dto.setSymbol(symbol);
        dto.setAssetType(type);
        return dto;
    }

    private AssetUpdateDTO updateDto(String symbol, AssetType type) {
        AssetUpdateDTO dto = new AssetUpdateDTO();
        dto.setSymbol(symbol);
        dto.setAssetType(type);
        return dto;
    }

    @Test
    void add_shouldCreate_whenSymbolIsUnique() {
        var dto = createDto("AAPL", AssetType.EQUITY);
        when(repo.existsBySymbol("AAPL")).thenReturn(false);
        when(repo.save(ArgumentMatchers.any(Asset.class)))
                .thenAnswer(inv -> {
                    Asset a = inv.getArgument(0);
                    a.setId(123L);
                    return a;
                });

        Asset created = service.add(dto);

        assertThat(created.getId()).isEqualTo(123L);
        assertThat(created.getSymbol()).isEqualTo("AAPL");
        verify(repo).save(any(Asset.class));
    }

    @Test
    void add_shouldFail_whenSymbolExists() {
        var dto = createDto("AAPL", AssetType.EQUITY);
        when(repo.existsBySymbol("AAPL")).thenReturn(true);

        assertThatThrownBy(() -> service.add(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getById_shouldReturn_whenExists() {
        var a = new Asset("BTC", AssetType.CRYPTO);
        a.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(a));

        Asset found = service.getById(7L);

        assertThat(found.getSymbol()).isEqualTo("BTC");
    }

    @Test
    void getById_shouldThrow_whenMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldChangeSymbolAndType_whenOk() {
        var existing = new Asset("OLD", AssetType.CASH);
        existing.setId(10L);
        when(repo.findById(10L)).thenReturn(Optional.of(existing));
        when(repo.existsBySymbol("NEW")).thenReturn(false);
        when(repo.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = updateDto("NEW", AssetType.EQUITY);
        Asset updated = service.update(10L, dto);

        assertThat(updated.getSymbol()).isEqualTo("NEW");
        assertThat(updated.getAssetType()).isEqualTo(AssetType.EQUITY);
    }

    @Test
    void update_shouldFail_whenSymbolConflict() {
        var existing = new Asset("AAPL", AssetType.EQUITY);
        existing.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsBySymbol("AAPL2")).thenReturn(true); // another row has it

        var dto = updateDto("AAPL2", AssetType.EQUITY);

        assertThatThrownBy(() -> service.update(1L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void delete_shouldThrow_whenMissing() {
        when(repo.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}