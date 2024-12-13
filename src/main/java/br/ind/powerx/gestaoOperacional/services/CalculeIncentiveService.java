package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.CompactIncentive;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveValueRepository;


@Service
public class CalculeIncentiveService {
	
	@Autowired
	private FunctionRepository funcRepo;
	
	@Autowired
	private IncentiveValueRepository incentiveValueRepository;
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;
	
	@Autowired
    private AuthenticationService authenticationService;
	

	public List<CompactIncentive> calculateIncentives(List<Sale> sales) {
	    validateSales(sales);

	    User user = authenticationService.getUserAuthenticated();
	    List<Incentive> incentives = new ArrayList<>();
	    Customer customer = sales.get(0).getCustomer();

	    Map<String, ApurationType> apurationTypes = preloadApurationTypes();

	    for (Sale sale : sales) {
	        incentives.addAll(calculateIncentivesForSale(sale, apurationTypes, customer, user));
	    }

	    incentives.addAll(calculateIncentivesForRoles(customer, sales, apurationTypes, user));
	    printIncentives(incentives);

	    return compactIncentives(incentives);
	}

	private void validateSales(List<Sale> sales) {
	    if (sales == null || sales.isEmpty()) {
	        throw new IllegalArgumentException("A lista de vendas não pode estar vazia.");
	    }

	    for (Sale sale : sales) {
	        if (sale == null) {
	            throw new IllegalArgumentException("A venda não pode ser nula.");
	        }
	        if (sale.getCustomer() == null || sale.getEmployee() == null || sale.getProduct() == null || sale.getQuantity() == null) {
	            throw new IllegalArgumentException("Venda contém informações obrigatórias nulas.");
	        }
	    }
	}

	private int calculateTotalQuantity(List<Sale> sales, Product product) {
	    return sales.stream()
	                .filter(sale -> sale.getProduct().equals(product)) // Filtra pelo produto
	                .mapToInt(Sale::getQuantity) // Mapeia a quantidade das vendas filtradas
	                .sum(); // Soma as quantidades
	}


	private Map<String, ApurationType> preloadApurationTypes() {
	    return apurationTypeRepository.findAll().stream()
	            .collect(Collectors.toMap(ApurationType::getName, java.util.function.Function.identity()));
	}

	private List<Incentive> calculateIncentivesForSale(Sale sale, Map<String, ApurationType> apurationTypes, Customer customer, User user) {
	    List<Incentive> incentives = new ArrayList<>();
	    Employee employee = sale.getEmployee();
	    Product product = sale.getProduct();
	    int quantity = sale.getQuantity();

	    for (Function function : employee.getFunctions()) {
	        if (isRelevantFunction(function)) {
	            IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);

	            
	            BigDecimal ccValue = BigDecimal.ZERO;
	            BigDecimal nfsValue = BigDecimal.ZERO;
	            
	            if(value != null) {
	            	ccValue = value.getCcValue().multiply(BigDecimal.valueOf(quantity));
		            nfsValue = value.getNfsValue().multiply(BigDecimal.valueOf(quantity));
		            incentives.addAll(createIncentives(ccValue, nfsValue, employee, function, sale.getCustomer(), sale, user, apurationTypes));
	            }
	            
	        }
	    }

	    return incentives;
	}

	private List<Incentive> calculateIncentivesForRoles(Customer customer, List<Sale> sales,
	                                                    Map<String, ApurationType> apurationTypes, User user) {
		
	    List<Incentive> incentives = new ArrayList<>();

	    List<String> roles = List.of("Chefe de Oficina", "Gerente de Pós Venda", "Diretor de Pós Venda", "Gerente de Peças");
	    List<Employee> relevantEmployees = customer.getEmployees().stream()
	            .filter(emp -> emp.getFunctions().stream().anyMatch(func -> roles.contains(func.getName())))
	            .collect(Collectors.toList());

	    for (Employee emp : relevantEmployees) {
	        for (Product product : customer.getGroup().getProducts()) {
	            for (Function function : emp.getFunctions()) {
	            	int totalQuantity = calculateTotalQuantity(sales, product);
	            	
	                IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);
	                
	                BigDecimal ccValue = BigDecimal.ZERO;
	                BigDecimal nfsValue = BigDecimal.ZERO;
	                
	                if(value != null) {
	                	ccValue = value.getCcValue().multiply(BigDecimal.valueOf(totalQuantity));
		                nfsValue = value.getNfsValue().multiply(BigDecimal.valueOf(totalQuantity));
		                incentives.addAll(createIncentives(ccValue, nfsValue, emp, function, customer, null, user, apurationTypes));
	                }

	            }
	        }
	    }

	    return incentives;
	}

	private boolean isRelevantFunction(Function function) {
	    return function.getName().equalsIgnoreCase("Mecânico") || function.getName().equalsIgnoreCase("Consultor Técnico");
	}

	private List<Incentive> createIncentives(BigDecimal ccValue, BigDecimal nfsValue, Employee employee, Function function,
	                                         Customer customer, Sale sale, User user, Map<String, ApurationType> apurationTypes) {
	    List<Incentive> incentives = new ArrayList<>();

	    if (ccValue.compareTo(BigDecimal.ZERO) > 0) {
	        incentives.add(new Incentive(null, null, user.getState(), employee.getPaymentMethod(), apurationTypes.get("Conta Corrente"),
	                employee, employee.getCpf(), ccValue, function, customer, sale, user));
	    }

	    if (nfsValue.compareTo(BigDecimal.ZERO) > 0) {
	        incentives.add(new Incentive(null, null, user.getState(), employee.getPaymentMethod(), apurationTypes.get("NF Serviço"),
	                employee, employee.getCpf(), nfsValue, function, customer, sale, user));
	    }

	    return incentives;
	}

	private void printIncentives(List<Incentive> incentives) {
	    incentives.forEach(i -> {
	        System.out.println("Vendedor : " + i.getEmployee().getName());
	        System.out.println("Valor : " + i.getIncentiveValue());
	        System.out.println("Metodo de pagamento: " + i.getPaymentMethod().getName());
	        System.out.println("Apuração : " + i.getApurationType().getName());
	        System.out.println();
	    });
	}

	public List<CompactIncentive> compactIncentives(List<Incentive> incentives) {
	    DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MM-yyyy");

	    Map<String, List<Incentive>> groupedIncentives = incentives.stream()
	        .collect(Collectors.groupingBy(Incentive::getCpf));

	    List<CompactIncentive> compactedList = new ArrayList<>();

	    for (Map.Entry<String, List<Incentive>> entry : groupedIncentives.entrySet()) {
	        String cpf = entry.getKey();
	        List<Incentive> employeeIncentives = entry.getValue();

	        Incentive firstIncentive = employeeIncentives.get(0);
	        String monthYear = firstIncentive.getReferenceDate().format(monthYearFormatter);
	        String employeeName = firstIncentive.getEmployee().getName();
	        String region = firstIncentive.getUser().getState().getState();
	        String type = firstIncentive.getApurationType().getName();
	        String modality = firstIncentive.getPaymentMethod().getName();
	        String function = firstIncentive.getEmployeeFunction().getName();
	        String store = firstIncentive.getCustomer().getFantasyName();
	        String cnpj = firstIncentive.getCustomer().getCnpj();

	        BigDecimal total = employeeIncentives.stream()
	            .map(Incentive::getIncentiveValue)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	        compactedList.add(new CompactIncentive(
	            monthYear, region, type, modality, cpf, employeeName, total, function, store, cnpj
	        ));
	    }

	    return compactedList;
	}
	
}





































