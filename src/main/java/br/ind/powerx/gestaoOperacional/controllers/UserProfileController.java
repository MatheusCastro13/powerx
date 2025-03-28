package br.ind.powerx.gestaoOperacional.controllers;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/user")
public class UserProfileController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/{id}")
	public String getUserById(@PathVariable Long id, Model model) {
		User user = userService.findById(id);
		user.getCustomers().stream()
	    .sorted(Comparator.comparing(Customer::getFantasyName))
	    .toList();
		
		
		model.addAttribute("user", user);
		
		return "user-detail";
	}

}
