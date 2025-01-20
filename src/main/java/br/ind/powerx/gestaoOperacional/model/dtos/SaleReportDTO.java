package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleReportDTO {

	private String employeeName;
    private List<ProductReportDTO> products;

}
