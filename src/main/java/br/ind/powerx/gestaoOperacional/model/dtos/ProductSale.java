package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductSale {

	private Long productId;
    private Integer quantity;
}
