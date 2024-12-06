  package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ind.powerx.gestaoOperacional.model.dtos.SalesDTO;
import br.ind.powerx.gestaoOperacional.services.SaleService;

@Controller
@RequestMapping("/sale")
public class SaleController {
	
	@Autowired
	public SaleService saleService;
	
	@PostMapping("/calcule")
	@ResponseBody
	public ResponseEntity<?> saveSales(@RequestBody List<SalesDTO> sales){
		System.out.println("ENTROU NO CONTROLADOR");
		saleService.saveSales(sales);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public ResponseEntity<?> teste(){
		System.out.println("Entrou");
		return ResponseEntity.noContent().build();
	}


}
