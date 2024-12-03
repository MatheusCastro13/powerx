package br.ind.powerx.gestaoOperacional.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.dtos.SalesDTO;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;
	
	public void saveSales(SalesDTO salesDTO) {
		
	}
}
