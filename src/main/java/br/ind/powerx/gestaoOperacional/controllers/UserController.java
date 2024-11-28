package br.ind.powerx.gestaoOperacional.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.UserFilterDTO;
import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping
	public String getUsers(Model model) {
		List<User> userList = userService.findAllByActiveTrue();
		List<Customer> availableCustomers = customerService.findAllByUserIdNull();
		List<Position> positionList = new ArrayList<>();
		List<State> stateList = new ArrayList<>();
		
		
		Position[] positions = Position.values();
		State[] states = State.values();
		
		for(Position p : positions) {
			positionList.add(p);
		}
		
		for(State s : states) {
			stateList.add(s);
		}
	
		model.addAttribute("users" , userList);
	    model.addAttribute("positions", positionList);
	    model.addAttribute("states", stateList);
	    model.addAttribute("availableCustomers", availableCustomers);
		return "users";
	}
	
	 @PostMapping("/update/{id}")
	    public String updateUser(@PathVariable Long id,
	                             @ModelAttribute User user,
	                             @RequestParam List<Long> customers, 
	                             Model model) {
	        userService.update(user, customers);

	        return "redirect:/adm?updated=true";
	    }


	
	
	@PostMapping("/save")
	public String saveUser(@ModelAttribute User user,
							Model model) {
		userService.save(user);
		
		return "redirect:/adm?saved=true";
	}

	@PostMapping("/filter")
	public String filterUsers(@RequestBody UserFilterDTO filters, Model model) {

	    List<User> filteredUsers = userService.filterUsers(filters.positions(), filters.states());
	    model.addAttribute("usersFiltered", filteredUsers);

	    return "fragments/user-table :: userTable(usersFiltered=${usersFiltered})";
	}
	
	@GetMapping("/clearFilters")
    public String clearFilters(Model model) {
        List<User> allUsers = userService.findAllByActiveTrue();

        model.addAttribute("users", allUsers);

        return "fragments/all-users-table :: userTable(allUsers=${users})";
    }

}





















