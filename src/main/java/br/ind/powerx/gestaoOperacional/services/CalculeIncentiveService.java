package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveValueRepository;


@Service
public class CalculeIncentiveService {
	
	@Autowired
	private FunctionRepository funcRepo;
	
	@Autowired
	private IncentiveRepository incentiveRepository;
	
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
	    int totalQuantity = 0;
	    for(Sale sale : sales) {
	    	if(sale.getProduct().equals(product) && !sale.getEmployee().getFunctions().stream().anyMatch(f -> f.getName().equalsIgnoreCase("Mecânico"))) {
	    		totalQuantity += sale.getQuantity();
	    	}
	    }
	    System.out.println("\nAquantidade somada para o produto " + product.getProductName() + " = " + totalQuantity + "\n");
	    return totalQuantity;
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
		            incentives.addAll(createIncentives(ccValue, nfsValue, employee, function, sale.getCustomer(), sale.getOrdem(), user, apurationTypes));
	            }
	            
	        }
	    }

	    return incentives;
	}

	private List<Incentive> calculateIncentivesForRoles(Customer customer, List<Sale> sales,
	                                                    Map<String, ApurationType> apurationTypes, User user) {
		System.out.println("\nCalculando incentivos por funções fora ct e m\n");
		
	    List<Incentive> incentives = new ArrayList<>();

	    List<String> roles = List.of("Chefe de Oficina", "Gerente de Pós Venda", "Diretor de Pós venda", "Gerente de Peças");
	    List<Employee> relevantEmployees = customer.getEmployees().stream()
	            .filter(emp -> emp.getFunctions().stream().anyMatch(func -> roles.contains(func.getName())))
	            .collect(Collectors.toList());

	    for (Employee emp : relevantEmployees) {
	        for (Product product : customer.getGroup().getProducts()) {
	            for (Function function : emp.getFunctions()) {
	            	if(!function.getName().equalsIgnoreCase("Mecânico") && !function.getName().equalsIgnoreCase("Consultor Técnico")) {
	            		int totalQuantity = calculateTotalQuantity(sales, product);
		            	System.out.println("Premiado - " + emp.getName());
		            	System.out.println("Função - " + function.getName());
		            	System.out.println("produto vendido - " + product.getProductCode() + " " + product.getProductName());
		            	System.out.println("Quantidade de produtos - " + totalQuantity);
		            	
		                IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);
		                
		                BigDecimal ccValue = BigDecimal.ZERO;
		                BigDecimal nfsValue = BigDecimal.ZERO;
		                
		                if(value != null) {
		                	System.out.println(value);
		                	ccValue = value.getCcValue().multiply(BigDecimal.valueOf(totalQuantity));
			                nfsValue = value.getNfsValue().multiply(BigDecimal.valueOf(totalQuantity));
			                System.out.println("\ncc - " + ccValue + " nfs - " + nfsValue + "\n");
			                incentives.addAll(createIncentives(ccValue, nfsValue, emp, function, customer, null, user, apurationTypes));
		                }
	            	}

	            }
	        }
	    }
	    
	    if(customer.getMechanicApuration() != null) {
	    	if(customer.getMechanicApuration().getName().equalsIgnoreCase("Linear")) {
	    		List<Employee> mechanics = customer.getEmployees().stream()
	    	            .filter(emp -> emp.getFunctions().stream().anyMatch(func -> func.getName().equalsIgnoreCase("Mecânico")))
	    	            .collect(Collectors.toList());
	    		
	    		for (Employee emp : mechanics) {
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
	    	}
	    }

	    return incentives;
	}

	private boolean isRelevantFunction(Function function) {
	    return function.getName().equalsIgnoreCase("Mecânico") || function.getName().equalsIgnoreCase("Consultor Técnico");
	}

	private List<Incentive> createIncentives(BigDecimal ccValue, BigDecimal nfsValue, Employee employee, Function function,
	                                         Customer customer, Integer saleOrdem, User user, Map<String, ApurationType> apurationTypes) {
		
		if(saleOrdem == null) {
			saleOrdem = 0;
		}
		
	    List<Incentive> incentives = new ArrayList<>();
	    Integer maxOrdem = incentiveRepository.findMaxOrderForCustomer(customer.getId());

        int newOrdem = (maxOrdem != null ? maxOrdem : 0) + 1;

	    if (ccValue.compareTo(BigDecimal.ZERO) > 0) {
	        incentives.add(new Incentive(null, newOrdem, null, user.getState(), employee.getPaymentMethod(), apurationTypes.get("Conta Corrente"),
	                employee, employee.getCpf(), ccValue, function, customer, saleOrdem, user));
	    }

	    if (nfsValue.compareTo(BigDecimal.ZERO) > 0) {
	        incentives.add(new Incentive(null, newOrdem, null, user.getState(), employee.getPaymentMethod(), apurationTypes.get("NF Serviço"),
	                employee, employee.getCpf(), nfsValue, function, customer, saleOrdem, user));
	    }
	    
	    return incentives;
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

	        String incentiveOrdem = firstIncentive.getOrdem() != null ? firstIncentive.getOrdem().toString() : "N/A";
	        String saleOrdem = firstIncentive.getSaleOrdem() != null ? firstIncentive.getSaleOrdem().toString() : "N/A";

	        String monthYear;
	        if (firstIncentive.getReferenceDate() != null) {
	            monthYear = firstIncentive.getReferenceDate().format(monthYearFormatter);
	        } else {
	            monthYear = LocalDate.now().format(monthYearFormatter);
	        }

	        String employeeName = firstIncentive.getEmployee() != null ? firstIncentive.getEmployee().getName() : "Sem Nome";
	        String region = firstIncentive.getUser() != null ? firstIncentive.getUser().getState().getState() : "Região Desconhecida";
	        String type = firstIncentive.getApurationType() != null ? firstIncentive.getApurationType().getName() : "Tipo Desconhecido";
	        String modality = firstIncentive.getPaymentMethod() != null ? firstIncentive.getPaymentMethod().getName() : "Modalidade Desconhecida";
	        String function = firstIncentive.getEmployeeFunction() != null ? firstIncentive.getEmployeeFunction().getName() : "Função Desconhecida";
	        String store = firstIncentive.getCustomer() != null ? firstIncentive.getCustomer().getFantasyName() : "Loja Desconhecida";
	        String cnpj = firstIncentive.getCustomer() != null ? firstIncentive.getCustomer().getCnpj() : "CNPJ Desconhecido";

	        BigDecimal total = employeeIncentives.stream()
	            .map(Incentive::getIncentiveValue)
	            .filter(Objects::nonNull)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	        compactedList.add(new CompactIncentive(
	            incentiveOrdem, 
	            saleOrdem, 
	            monthYear, 
	            region, 
	            type, 
	            modality, 
	            cpf, 
	            employeeName, 
	            total, 
	            function, 
	            store, 
	            cnpj
	        ));
	    }

	    printCompactIncentives(compactedList);

	    return compactedList;
	}
	
	public void printCompactIncentives(List<CompactIncentive> compactedIncentives) {
		System.out.println("\nINCENTIVOS COMPACTADOS\n");
	    for (CompactIncentive incentive : compactedIncentives) {
	    	System.out.println("Ordem de incentivo: " + incentive.getIncentiveOrdem());
	    	System.out.println("Ordem de venda: " + incentive.getSaleOrdem());
	        System.out.println("Mês-Ano: " + incentive.getMonthYear());
	        System.out.println("Região: " + incentive.getRegion());
	        System.out.println("Tipo: " + incentive.getType());
	        System.out.println("Modalidade: " + incentive.getModality());
	        System.out.println("CPF: " + incentive.getCpf());
	        System.out.println("Nome do Funcionário: " + incentive.getEmployeeName());
	        System.out.println("Total: R$ " + incentive.getTotal());
	        System.out.println("Função: " + incentive.getFunction());
	        System.out.println("Loja: " + incentive.getStore());
	        System.out.println("CNPJ: " + incentive.getCnpj());
	        System.out.println("-----------------------------------------");
	    }
	}

	
}





































