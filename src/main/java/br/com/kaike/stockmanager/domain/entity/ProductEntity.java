package br.com.kaike.stockmanager.domain.entity;

import br.com.kaike.stockmanager.domain.enums.CategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_product")
public class ProductEntity {

    @Id
    @Column(unique = true, nullable = false)
    private Integer sku;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryEnum category;

}
