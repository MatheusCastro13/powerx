package br.ind.powerx.gestaoOperacional.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.User;

@Service
public class AuthenticationService {

	@Autowired
	private UserService userService;
	
	public User getUserAuthenticated() {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && authentication.isAuthenticated() &&
	            !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
	            
	            String email = authentication.getName();
	            User user = userService.findByEmail(email);
	            if (user != null) {
	                return user;
	            }
	        }
		return null;
	}
}
