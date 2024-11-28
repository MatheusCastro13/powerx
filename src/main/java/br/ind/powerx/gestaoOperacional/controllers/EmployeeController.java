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
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.services.EmployeeService;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private FunctionRepository functionRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	@GetMapping
	public String findAll(Model model) {
		List<Employee> employees = employeeService.findAllByActiveTrue();
		List<Function> functions = functionRepository.findAll();
		List<Customer> customers = customerRepository.findAll();
		List<ApurationType> apurationTypes = apurationTypeRepository.findAll();
		List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();

		
		model.addAttribute("employees", employees);
		model.addAttribute("functions", functions);
		model.addAttribute("customers", customers);
		model.addAttribute("apurationTypes", apurationTypes);
		model.addAttribute("paymentMethods", paymentMethods);
		
		return "employees";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute Employee employee, 
						@RequestParam List<Long> functions, 
						@RequestParam List<Long> customers, 
						@RequestParam Long paymentMethod, 
						@RequestParam List<Long> apurationTypes) {
		employee.setActive(true);
		employeeService.save(employee, functions, customers, paymentMethod, apurationTypes);
		
		return "redirect:/adm?saved=true";
	}
	
	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @ModelAttribute Employee employee, 
			@RequestParam List<Long> functions, 
			@RequestParam List<Long> customers,
			@RequestParam Long paymentMethod, 
			@RequestParam List<Long> apurationTypes,
			Model model) {
		employee.setActive(true);

		employeeService.update(id, employee, functions, customers, paymentMethod, apurationTypes);
		
		return "redirect:/adm?updated=true";
	}
	
}


































