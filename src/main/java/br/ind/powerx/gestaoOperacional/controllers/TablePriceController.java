package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceSaveDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.ProductService;
import br.ind.powerx.gestaoOperacional.services.TablePriceService;

@Controller
@RequestMapping("/table-prices")
public class TablePriceController {

	private final TablePriceService tableService;
	
	private final ProductService productService;
	
	private final CustomerService customerService;
	
	private final AuthenticationService authenticationService;
	
	@Autowired
	public TablePriceController(TablePriceService tableService, ProductService productService, 
			CustomerService customerService, AuthenticationService authenticationService) {
		this.tableService = tableService;
		this.productService = productService;
		this.customerService = customerService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<TablePrice> tables = tableService.findAll(PageRequest.of(page, size));
		List<Customer> customers = customerService.findAll();
		List<Product> products = productService.findAll();
		
		model.addAttribute("tablePrices", tables.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", tables.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("products", products);
		model.addAttribute("customers", customers);
		
		return "table-price";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute TablePriceSaveDTO table) {
		tableService.save(table);
		
		return "redirect:/table-prices";
	}
	
	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @ModelAttribute TablePriceSaveDTO table) {
		tableService.update(id, table);
		return "redirect:/tabel-prices";
	}
}






















