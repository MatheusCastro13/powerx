package br.ind.powerx.gestaoOperacional.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.dtos.SalesRequest;
import br.ind.powerx.gestaoOperacional.services.SaleService;

@Controller
@RequestMapping("/sales")
public class SaleController {
	
	@Autowired
	public SaleService saleService;
	
	@PostMapping("/saveAll")
	public ResponseEntity<?> saveSales(@RequestBody SalesRequest salesRequest){
		
		saleService.saveSales(salesRequest);
		return ResponseEntity.noContent().build();
	}

}
