package br.ind.powerx.gestaoOperacional.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.dtos.SalesRequest;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;
	
	public void saveSales(SalesRequest salesRequest) {
		
		System.out.println("Service");
		System.out.println(salesRequest.toString());
		System.out.println("vendas" + salesRequest.getSales().toString());
	}
}
