package br.ind.powerx.gestaoOperacional.controllers;

import java.awt.Taskbar.State;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import br.ind.powerx.gestaoOperacional.services.IncentiveService;

@Controller
@RequestMapping("/incentives")
public class IncentiveController {

	@Autowired
	private IncentiveService incentiveService;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;
	
	@Autowired
	private FunctionRepository functionRepository;
	
	@Autowired
	private SaleRepository saleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	
	@GetMapping
	public String findAll(Model model) {
		List<Incentive> incentives = incentiveService.findAll();
		List<Customer> customers = customerRepository.findAll();
		List<Employee> employees = employeeRepository.findAll();
		List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
		List<ApurationType> apurationTypes = apurationTypeRepository.findAll();
		List<Function> functions = functionRepository.findAll();
		List<Sale> sales = saleRepository.findAll();
		List<User> users = userRepository.findAll();
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
		
		model.addAttribute("incentives", incentives);
		model.addAttribute("customers", customers);
		model.addAttribute("employees", employees);
		model.addAttribute("paymentMethods", paymentMethods);
		model.addAttribute("apurationTypes", apurationTypes);
		model.addAttribute("functions", functions);
		model.addAttribute("sales", sales);
		model.addAttribute("users", users);
		model.addAttribute("positions", positionList);
		model.addAttribute("states", stateList);
		
		return "incentiveLaunch";
		
	}
}



















































