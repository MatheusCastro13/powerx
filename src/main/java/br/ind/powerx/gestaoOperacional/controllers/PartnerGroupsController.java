package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.PartnerGroupService;
import br.ind.powerx.gestaoOperacional.services.ProductService;

@Controller
@RequestMapping("/partner-groups")
public class PartnerGroupsController {
	
	@Autowired
	private PartnerGroupService groupService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;

	@GetMapping
	public String getPartnerGroups(Model model) {
		List<Group> groups = groupService.findAll();
		List<Product> products = productService.findAll();
		List<Customer> availableCustomers = customerService.findAllByGroupIdNull();
		
		model.addAttribute("groups", groups);
		model.addAttribute("products", products);
		model.addAttribute("availableCustomers", availableCustomers);
		
		return "partner-groups";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute Group group, Model model) {
		groupService.save(group);
		
		return "redirect:/adm?saved=true";
	}
	
	@PostMapping("/update/{id}")
	public String update(@ModelAttribute Group group, Model model) {
		groupService.update(group);
		
		return "redirect:/adm?updated=true";
	}
}
