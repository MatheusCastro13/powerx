package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaleDTO {

	private LocalDate referenceDate;
	private String customerName;
	private String employeeName;
	private String productName;
	private String productCode;
	private Integer quantity;
	private Integer documentNumber;

}
