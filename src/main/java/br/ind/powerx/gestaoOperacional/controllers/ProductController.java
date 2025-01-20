package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.ApurationTypeService;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.FunctionService;
import br.ind.powerx.gestaoOperacional.services.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ApurationTypeService apurationTypeService;
	
	@Autowired
	private FunctionService functionService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
            Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Product> products = productService.findAll(PageRequest.of(page, size));
		List<Customer> customers = customerService.findAllByActiveTrue();
		List<ApurationType> apurationTypes = apurationTypeService.findAll();
		List<Function> functions = functionService.findAll();
		
		model.addAttribute("products", products.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", products.getTotalPages());
	    model.addAttribute("user", user);
		model.addAttribute("customers", customers);
		model.addAttribute("apurationTypes", apurationTypes);
		model.addAttribute("functions", functions);
		
		return "products";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute Product product, Model model) {
		productService.save(product);
		
		return "redirect:/adm?saved=true";
	}
	
	@PostMapping("/update/{id}")
	public String save(@PathVariable Long id,@ModelAttribute Product product) {
		productService.save(product);
		
		return "products";
	}
	
	@PostMapping("/incentiveValue/delete/{productId}/{incentiveValueId}")
	public ResponseEntity<Void> removeIncentiveValue(@PathVariable(name = "productId") Long productId, 
			@PathVariable(name = "incentiveValueId") Long incentiveValueId) {
		
		productService.removeIncentiveValue(productId, incentiveValueId);
		
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/incentive/{id}")
	public String addIncentiveValues(@PathVariable Long id,
	        @RequestParam List<Long> customer,
	        @RequestParam List<Long> function,
	        @RequestParam List<Double> ccValue, 
	        @RequestParam List<Double> nfsValue){
		
		productService.addIncentiveValue(id, customer, function, ccValue, nfsValue);
		return "products";
	}
	
}























