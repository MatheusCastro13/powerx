package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.dtos.SalesDTO;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;
	
	public void saveSales(List<SalesDTO> salesDTO) {
		System.out.println("ENTROU NO Service");
		salesDTO.forEach(sale -> {
			System.out.println(sale.toString());
		});
	}
}
