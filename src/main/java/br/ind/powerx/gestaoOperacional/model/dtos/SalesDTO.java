package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

public record SalesDTO (Long customerId, Long employeeId, List<ProductSaleDTO> products){

}
