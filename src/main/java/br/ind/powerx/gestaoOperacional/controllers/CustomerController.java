package br.ind.powerx.gestaoOperacional.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerFilterDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductDTO;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.EmployeeService;
import br.ind.powerx.gestaoOperacional.services.MechanicApurationService;
import br.ind.powerx.gestaoOperacional.services.PartnerGroupService;
import br.ind.powerx.gestaoOperacional.services.UserService;
import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private PartnerGroupService groupService;

	@Autowired
	private UserService userService;

	@Autowired
	private MechanicApurationService mechanicApurationService;

	@GetMapping
	public String findAll(Model model) {
		List<Customer> customers = customerService.findAllByActiveTrue();
		List<Employee> employees = employeeService.findAllByActiveTrue();
		List<Group> groups = groupService.findAll();
		List<MechanicApuration> mechanicApurations = mechanicApurationService.findAll();
		List<User> users = userService.findAllByActiveTrue();

		model.addAttribute("customers", customers);
		model.addAttribute("employees", employees);
		model.addAttribute("groups", groups);
		model.addAttribute("mechanicApurations", mechanicApurations);
		model.addAttribute("users", users);

		return "customers";

	}

	@PostMapping("/save")
	public String save(@ModelAttribute Customer customer, 
            @RequestParam Long userId, 
            @RequestParam Long groupId,
            @RequestParam Long mechanicApurationId, 
            @RequestParam(required = false) List<Long> employees, 
            Model model) {
		customer.setActive(true);
		customerService.save(customer, userId, groupId, mechanicApurationId, employees);

		return "redirect:/adm?saved=true";
	}

	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @ModelAttribute Customer customer, @RequestParam Long userId,
			@RequestParam Long groupId, @RequestParam Long mechanicApurationId, @RequestParam(value = "employees", required = false) List<Long> employees,
			Model model) {
		if (!customer.isActive()) {
			customer.setActive(true);
		}
		customerService.update(customer, userId, groupId, mechanicApurationId, employees);

		return "redirect:/adm?updated=true";
	}

	@PostMapping("/filter")
	public String filterCustomer(@RequestBody CustomerFilterDTO filters, Model model) {
		List<Customer> customers = customerService.filterCustomers(filters.users(), filters.groups());

		model.addAttribute("customers", customers);

		return "/fragments/customers/customers-table :: customers-table(customers=${customers})";
	}

	@GetMapping("/clearFilters")
	public String clearFilters(Model model) {
		List<Customer> customers = customerService.findAllByActiveTrue();

		model.addAttribute("customers", customers);

		return "/fragments/customers/customers-table :: customers-table(customers=${customers})";
	}

	@GetMapping("/{customerId}/employees")
	public ResponseEntity<?> getEmployeesAndProductsByClient(@PathVariable Long customerId) {
		try {

			Customer customer = customerService.findById(customerId)
					.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

			List<Employee> employees = customer.getEmployees();

			Group group = customer.getGroup();

			List<ProductDTO> products = group.getProducts().stream()
					.map(p -> new ProductDTO(p.getId(), p.getProductCode(), p.getProductName())).collect(Collectors.toList());

			List<EmployeeDTO> consultores = employees.stream()
					.filter(emp -> emp.getFunctions().stream()
							.anyMatch(function -> function.getName().equalsIgnoreCase("Consultor Técnico")))
					.map(emp -> new EmployeeDTO(emp.getId(), emp.getName(), "Consultor Técnico")).collect(Collectors.toList());

			List<EmployeeDTO> mecanicos = employees.stream()
					.filter(emp -> emp.getFunctions().stream()
							.anyMatch(function -> function.getName().equalsIgnoreCase("Mecânico")))
					.map(emp -> new EmployeeDTO(emp.getId(), emp.getName(), "Mecânico")).collect(Collectors.toList());

			Map<String, Object> response = new HashMap<>();
			response.put("consultores", consultores);
			response.put("produtos", products);
			
			if(customer.getMechanicApuration() != null) {
				if(!customer.getMechanicApuration().getName().equalsIgnoreCase("Linear")) {
					response.put("mecanicos", mecanicos);
				}
				else {
					mecanicos = new ArrayList<>();
					response.put("mecanicos", mecanicos);
				}
			}
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Erro ao carregar dados do cliente" + e);
		}
	}

}
