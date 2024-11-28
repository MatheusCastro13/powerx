package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.PaymentMethodDTO;
import br.ind.powerx.gestaoOperacional.services.PaymentMethodService;

@Controller
@RequestMapping("/payments")
public class PaymentMethodController {
	
	@Autowired
	private PaymentMethodService methodService;

	@GetMapping
	public String getPayments(Model model) {
		
		List<PaymentMethod> payments = methodService.findAll();
		model.addAttribute("payments", payments);
		
		return "payments";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute PaymentMethodDTO method) {
		
		methodService.save(method);
		
		return "redirect:/adm?saved=true";
	}
	
	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @ModelAttribute PaymentMethod method) {
		methodService.update(id, method);
		
		return "redirect:/adm?updated=true";
	}
	
	
}











