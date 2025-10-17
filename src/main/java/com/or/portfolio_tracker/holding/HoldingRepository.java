package com.or.portfolio_tracker.holding;

import com.or.portfolio_tracker.portfolio.PositionPerAssetRow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    @EntityGraph(attributePaths = "asset")   // <-- make sure Asset is loaded
    Page<Holding> findAll(Pageable pageable);

    @Query("""
           select new com.or.portfolio_tracker.portfolio.PositionPerAssetRow(
               a.symbol,
               a.assetType,
               sum(h.quantity),
               sum(h.quantity * h.purchasePrice)
           )
           from Holding h
           join h.asset a
           group by a.symbol, a.assetType
           order by a.symbol asc
           """)
    List<PositionPerAssetRow> sumPerAssetRaw();

    @Query("select sum(h.quantity) from Holding h")
    BigDecimal sumQuantity();

    @Query("select sum(h.quantity * h.purchasePrice) from Holding h")
    BigDecimal sumInvested();
}