package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.DocumentService;
import br.ind.powerx.gestaoOperacional.services.IncentiveService;

@Controller
@RequestMapping("/incentives")
public class IncentiveController {

	@Autowired
	private IncentiveService incentiveService;
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
    private AuthenticationService authenticationService;
	
	
	
	@GetMapping
	public String findAll(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "25") int size,
	        Model model) {
	    User user = authenticationService.getUserAuthenticated();
	    
	    if (user.getRole().equalsIgnoreCase("role_admin")) {
	        Page<Incentive> incentivesPage = incentiveService.findAll(PageRequest.of(page, size));
	        List<Customer> customers = customerRepository.findAll();
	        List<Integer> documentNumbers = documentService.findAllDocumentNumbers();
	        
	        Map<Integer, String> documentCustomer = documentService.getCustomersByDocument(documentNumbers);
	        
	        model.addAttribute("user", user);
	        model.addAttribute("incentives", incentivesPage.getContent());
	        model.addAttribute("currentPage", page);
		    model.addAttribute("totalPages", incentivesPage.getTotalPages());
	        model.addAttribute("customers", customers);
	        model.addAttribute("documentNumbers", documentCustomer);
	        
	        return "incentiveLaunch";
	    } else {
	        Page<Incentive> incentivesPage = incentiveService.findByUser(user, PageRequest.of(page, size));
	        List<Customer> customers = user.getCustomers();
	        List<Integer> documentNumbers = documentService.findAllDocumentNumbersByUser(user);
	        
	        Map<Integer, String> documentCustomer = documentService.getCustomersByDocument(documentNumbers);
	        
	        model.addAttribute("user", user);
	        model.addAttribute("incentivesPage", incentivesPage.getContent());
	        model.addAttribute("customers", customers);
	        model.addAttribute("currentPage", page);
		    model.addAttribute("totalPages", incentivesPage.getTotalPages());
	        model.addAttribute("documentNumbers", documentCustomer);
	        
	        return "incentives";
	    }
	}

	
	@GetMapping("/{documentNumber}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDocumentDetails(@PathVariable Integer documentNumber) {
        Map<String, Object> details = documentService.getDocumentDetails(documentNumber);
        return ResponseEntity.ok(details);
    }
	
	
}



















































