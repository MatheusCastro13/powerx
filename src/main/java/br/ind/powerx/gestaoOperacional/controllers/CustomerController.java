package br.ind.powerx.gestaoOperacional.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import br.ind.powerx.gestaoOperacional.model.Flag;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Industry;
import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerFilterDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerUpdateDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.EmployeeService;
import br.ind.powerx.gestaoOperacional.services.FlagService;
import br.ind.powerx.gestaoOperacional.services.IndustryService;
import br.ind.powerx.gestaoOperacional.services.MechanicApurationService;
import br.ind.powerx.gestaoOperacional.services.PartnerGroupService;
import br.ind.powerx.gestaoOperacional.services.TablePriceService;
import br.ind.powerx.gestaoOperacional.services.UserService;
import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService customerService;

	private final EmployeeService employeeService;

	private final PartnerGroupService groupService;

	private final UserService userService;

	private final MechanicApurationService mechanicApurationService;
	
	private final IndustryService industryService;

	private final FlagService flagService;
	
	private final TablePriceService tableService;
	
	@Autowired
	public CustomerController(CustomerService customerService, 
			EmployeeService employeeService, PartnerGroupService groupService, 
			UserService userService, MechanicApurationService mechanicApurationService, 
			IndustryService industryService, FlagService flagService,
			TablePriceService tableService) {
		this.customerService = customerService;
		this.employeeService = employeeService;
		this.groupService = groupService;
		this.userService = userService;
		this.mechanicApurationService = mechanicApurationService;
		this.industryService = industryService;
		this.flagService = flagService;
		this.tableService = tableService;
	}
	

	@Autowired
	private AuthenticationService authenticationService;
	
	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
            Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Customer> customers = customerService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("fantasyName"))));
		List<Employee> employees = employeeService.findAllByActiveTrue();
		List<Group> groups = groupService.findAll();
		List<MechanicApuration> mechanicApurations = mechanicApurationService.findAll();
		List<Industry> industries = industryService.findAll();
		List<Flag> flags = flagService.findAll();
		List<User> users = userService.findAllByActiveTrue();
		List<TablePrice> tablePrices = tableService.findAll();

		model.addAttribute("customers", customers.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", customers.getTotalPages());
	    model.addAttribute("user", user);
		model.addAttribute("employees", employees);
		model.addAttribute("groups", groups);
		model.addAttribute("industries", industries);
		model.addAttribute("flags", flags);
		model.addAttribute("mechanicApurations", mechanicApurations);
		model.addAttribute("users", users);
		model.addAttribute("tablePrices", tablePrices);

		return "customers";

	}

	@PostMapping("/save")
	public String save(@ModelAttribute Customer customer, 
            @RequestParam Long userId, 
            @RequestParam Long groupId,
            @RequestParam(required = false) Long industryId,
            @RequestParam(required = false) Long flagId,
            @RequestParam Long mechanicApurationId, 
            @RequestParam(required = false) List<Long> employees, 
            Model model) {
		customer.setActive(true);
		customerService.save(customer, userId, groupId, industryId, flagId, mechanicApurationId, employees);

		return "redirect:/customers";
	}

	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, 
			@ModelAttribute CustomerUpdateDTO customer,
			@RequestParam Long userId,
			@RequestParam Long groupId, 
			@RequestParam(required = false) Long industryId,
            @RequestParam(required = false) Long flagId,
			@RequestParam Long mechanicApurationId, 
			@RequestParam(value = "employees", required = false) List<Long> employees,
			Model model) {
		customerService.update(customer, userId, groupId, industryId, flagId, mechanicApurationId, employees);

		return "redirect:/customers";
	}

	@PostMapping("/filter")
	public String filterCustomer(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
            @RequestBody CustomerFilterDTO filters, 
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Customer> customers = customerService.filterCustomers(filters.users(), 
				filters.groups(), 
				filters.industries(), 
				filters.flags(),
				PageRequest.of(page, size));
		List<Employee> employees = employeeService.findAllByActiveTrue();
		List<Group> groups = groupService.findAll();
		List<MechanicApuration> mechanicApurations = mechanicApurationService.findAll();
		List<Industry> industries = industryService.findAll();
		List<Flag> flags = flagService.findAll();
		List<User> users = userService.findAllByActiveTrue();
		List<TablePrice> tablePrices = tableService.findAll();

		model.addAttribute("customers", customers.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", customers.getTotalPages());
	    model.addAttribute("user", user);
		model.addAttribute("employees", employees);
		model.addAttribute("groups", groups);
		model.addAttribute("industries", industries);
		model.addAttribute("flags", flags);
		model.addAttribute("mechanicApurations", mechanicApurations);
		model.addAttribute("users", users);
		model.addAttribute("tablePrices", tablePrices);


		return "fragments/filteredCustomers :: filtered-customers";
	}

	@GetMapping("/clearFilters")
	public String clearFilters(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
			Model model) {
		Page<Customer> customers = customerService.findAll(PageRequest.of(page, size));

		model.addAttribute("customers", customers.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", customers.getTotalPages());

		return "fragments/filteredCustomers :: filtered-customers";
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
