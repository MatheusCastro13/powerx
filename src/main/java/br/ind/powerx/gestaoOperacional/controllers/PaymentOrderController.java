package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;

@Controller
@RequestMapping("/payment-order")
public class PaymentOrderController {

	private final CustomerRepository customerRepository;
	
	private  final ApurationTypeRepository apurationTypeRepository;
	
	private final AuthenticationService authenticationService;
	
	@Autowired
	public PaymentOrderController(CustomerRepository customerRepository, ApurationTypeRepository apurationTypeRepository,
			AuthenticationService authenticationService) {
		this.customerRepository = customerRepository;
		this.apurationTypeRepository = apurationTypeRepository;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping	
	public String getPaymentOrderPage(Model model) {
		User user = authenticationService.getUserAuthenticated();
		List<Customer> customers = customerRepository.findAll();
		List<ApurationType> apurationTypes = apurationTypeRepository.findAll();
		
		model.addAttribute("user", user);
		model.addAttribute("customers", customers);
		model.addAttribute("apurationTypes", apurationTypes);
		
		return "payment-order";
	}
	
	@GetMapping("/state")
	public String getPaymentOrderForStatePage(Model model) {
		State[] states = {State.SP2, State.SP3, State.RJ, State.PB, State.PE, State.PA, State.GO};
		User user = authenticationService.getUserAuthenticated();
		
		List<ApurationType> apurationTypes = apurationTypeRepository.findAll();
		
		model.addAttribute("user", user);
		model.addAttribute("states", states);
		model.addAttribute("apurationTypes", apurationTypes);
		
		return "payment-order-state";
	}
}
