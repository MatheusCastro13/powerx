package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductReportDTO {
	private String productCode;
    private String productName;
    private Integer quantity;

}
