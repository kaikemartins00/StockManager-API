package br.com.kaike.stockmanager.domain.entity;

import br.com.kaike.stockmanager.domain.enums.MovementTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_stock_movement")
public class StockMovementEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementTypeEnum movementTypeEnum;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity = 0 ;

    private LocalDateTime movementDate;

    @ManyToOne
    @JoinColumn(name = "product_sku", nullable = false)
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


}