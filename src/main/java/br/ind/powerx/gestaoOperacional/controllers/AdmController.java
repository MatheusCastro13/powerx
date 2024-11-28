package br.ind.powerx.gestaoOperacional.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;


@Controller
@RequestMapping("/adm")
public class AdmController {

	@Autowired
    private AuthenticationService authenticationService;
	
	@GetMapping
	public String admDashboardPage(Model model) {
		User user = authenticationService.getUserAuthenticated();
	       if(user != null) {
	    	   model.addAttribute("user", user);
	       }
		
		return "adm-dashboard";
	}
	
	
}
