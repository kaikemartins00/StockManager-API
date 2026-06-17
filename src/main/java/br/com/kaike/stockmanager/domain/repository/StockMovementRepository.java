package br.com.kaike.stockmanager.domain.repository;

import br.com.kaike.stockmanager.domain.entity.StockMovementEntity;
import br.com.kaike.stockmanager.dto.stock.StockMovementResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovementEntity, Integer> {

    Optional<List<StockMovementEntity>> findByProductSku(Integer sku);


}
