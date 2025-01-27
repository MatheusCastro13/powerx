package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
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
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;


@Service
public class CalculeIncentiveService {
	
	private final SaleRepository saleRepository;
	
	private final IncentiveRepository incentiveRepository;
	
	private final IncentiveValueRepository incentiveValueRepository;
	
	private final ApurationTypeRepository apurationTypeRepository;
	
    private final AuthenticationService authenticationService;
	
	private final FunctionRepository functionRepository;

	@Autowired
	public CalculeIncentiveService(SaleRepository saleRepository, IncentiveRepository incentiveRepository, 
			IncentiveValueRepository incentiveValueRepository, ApurationTypeRepository apurationTypeRepository, 
			AuthenticationService authenticationService, FunctionRepository functionRepository) {
		this.saleRepository = saleRepository;
		this.incentiveRepository = incentiveRepository;
		this.incentiveValueRepository = incentiveValueRepository;
		this.apurationTypeRepository = apurationTypeRepository;
		this.authenticationService = authenticationService;
		this.functionRepository = functionRepository;
	}
	

	public List<Incentive> calculateIncentives(List<Sale> sales) {
	    validateSales(sales);
	    
	    sales.stream().forEach(s-> System.out.println(s));

	    User user = authenticationService.getUserAuthenticated();
	    List<Incentive> incentives = new ArrayList<>();
	    Customer customer = sales.get(0).getCustomer();

	    Map<String, ApurationType> apurationTypes = preloadApurationTypes();

	    for (Sale sale : sales) {
	        incentives.addAll(calculateIncentivesForSale(sale, apurationTypes, customer, user));
	    }
	    
	    incentives.addAll(calculateIncentivesForRoles(customer, sales, apurationTypes, user));

	    List<Incentive> incentivesComapcted = compactIncentives(incentives);
	    
	    saleRepository.saveAll(sales);
	    incentiveRepository.saveAll(incentivesComapcted);
	    
	    return incentivesComapcted;
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

	private int calculateConsultantTotalQuantity(List<Sale> sales, Product product, Customer customer) {
	    String mechanicApurationName = customer.getMechanicApuration().getName();
		
		int totalQuantity = 0;
	    
	    for(Sale sale : sales) {
	    	if(sale.getProduct().equals(product) && !(mechanicApurationName.equalsIgnoreCase("Somente Mecânicos"))) {
	    		if(sale.getEmployee().getFunctions().stream().anyMatch(f -> f.getName().equalsIgnoreCase("Consultor Técnico"))) {
	    			totalQuantity += sale.getQuantity();
	    		}
	    	}
	    }
	    System.out.println("\nAquantidade somada para o produto " + product.getProductName() + " = " + totalQuantity + "\n");
	    return totalQuantity;
	}
	
	private int calculateMechanicTotalQuantity(List<Sale> sales, Product product, Customer customer) {
	    String mechanicApurationName = customer.getMechanicApuration().getName();
		
		int totalQuantity = 0;
	    
	    for(Sale sale : sales) {
	    	if(sale.getProduct().equals(product) && (mechanicApurationName.equalsIgnoreCase("Somente Mecânicos"))) {
	    		if(sale.getEmployee().getFunctions().stream().anyMatch(f -> f.getName().equalsIgnoreCase("Mecânico"))) {
	    			totalQuantity += sale.getQuantity();
	    		}
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
		System.out.println("------CALCULANDO INCENTIVO POR VENDA------");
		List<Incentive> incentives = new ArrayList<>();
	    Employee employee = sale.getEmployee();
	    Product product = sale.getProduct();
	    int quantity = sale.getQuantity();
	    
	    System.out.println("Cliente - " + customer.getFantasyName());
	    System.out.println("Premiado - " + employee.getName());
	    System.out.println("Produto vendido - " + product.getProductCode() + " " + product.getProductName());
	    System.out.println("Quantidade vendida - " + quantity);

	    for (Function function : employee.getFunctions()) {
	        if (isRelevantFunction(function)) {
	        	System.out.println("Funcão - " + function.getName());
	        	
	            IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);
	            
	            System.out.println("Valor do incentivo: \n");

	            BigDecimal ccValue = BigDecimal.ZERO;
	            BigDecimal nfsValue = BigDecimal.ZERO;
	            
	            if(value != null) {
                	System.out.println(value);
	            	ccValue = value.getCcValue().multiply(BigDecimal.valueOf(quantity));
		            nfsValue = value.getNfsValue().multiply(BigDecimal.valueOf(quantity));
	                System.out.println("\ncc - " + ccValue + " nfs - " + nfsValue + "\n");
		            incentives.addAll(createIncentives(ccValue, nfsValue, employee, function, sale.getCustomer(), sale.getDocumentNumber(), user, apurationTypes));
	            }
	            
	        }
	    }
        System.out.println("-----------------------------------------------------");

	    return incentives;
	}

	private List<Incentive> calculateIncentivesForRoles(Customer customer, List<Sale> sales,
	                                                    Map<String, ApurationType> apurationTypes, User user) {
		System.out.println("------CALCULANDO INCENTIVO POR FUNÇÃO------");
		
		
		Integer documentNumber = sales.get(0).getDocumentNumber(); 
		
	    List<Incentive> incentives = new ArrayList<>();
	    
	    List<String> roles = functionRepository.findAll().stream()
	    		.filter(function -> !function.getName().equalsIgnoreCase("Mecânico") || !function.getName().equalsIgnoreCase("Consultor Técnico"))
	    		.map(function -> function.getName())
	    		.collect(Collectors.toList());
	    
	    List<Employee> relevantEmployees = customer.getEmployees().stream()
	            .filter(emp -> emp.getFunctions().stream().anyMatch(func -> roles.contains(func.getName())))
	            .collect(Collectors.toList());
	    
	    System.out.println("\nCalculando incentivos por funções fora ct e m\n");
	    
	    System.out.println("   ------CALCULANDO INCENTIVO POR GERENCIA------");
	    
	    for (Employee emp : relevantEmployees) {
	        for (Product product : customer.getGroup().getProducts()) {
	            for (Function function : emp.getFunctions()) {
	            	if(!function.getName().equalsIgnoreCase("Mecânico") && !function.getName().equalsIgnoreCase("Consultor Técnico")) {
	            		int totalQuantity = 0;
	            		
	            		if(!customer.getMechanicApuration().getName().equalsIgnoreCase("Somente Mecânicos")) {
	            			totalQuantity = calculateConsultantTotalQuantity(sales, product, customer);
	            		}
	            		else {
	            			totalQuantity = calculateMechanicTotalQuantity(sales, product, customer);
	            		}
	            		
	            		int currentFunctionQuantity = customer.getEmployees().stream()
	            				.filter(e -> e.getFunctions().stream()
	            				.anyMatch(f -> f.getName().equalsIgnoreCase(function.getName())))
	            				.collect(Collectors.toList())
	            				.size();
	            		
		            	System.out.println("Premiado - " + emp.getName());
		            	System.out.println("Função - " + function.getName());
		            	System.out.println("Pessoas com essa função - " + currentFunctionQuantity);
		            	System.out.println("produto vendido - " + product.getProductCode() + " " + product.getProductName());
		            	System.out.println("Quantidade de produtos - " + totalQuantity);
		            	
		                IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);
		                
		                BigDecimal ccValue = BigDecimal.ZERO;
		                BigDecimal nfsValue = BigDecimal.ZERO;
		                
		                if(value != null) {
		                	System.out.println(value);
		                	ccValue = (value.getCcValue().divide(new BigDecimal(currentFunctionQuantity)).multiply(BigDecimal.valueOf(totalQuantity)));
			                nfsValue = (value.getNfsValue().divide(new BigDecimal(currentFunctionQuantity)).multiply(BigDecimal.valueOf(totalQuantity)));
			                System.out.println("\ncc - " + ccValue + " nfs - " + nfsValue + "\n");
			                incentives.addAll(createIncentives(ccValue, nfsValue, emp, function, customer, documentNumber, user, apurationTypes));
		                }
	            	}

	            }
	        }
	    }
	    System.out.println("   ------------------------------------------");
	    System.out.println("   ------CALCULANDO INCENTIVO POR MECÂNICO------");
	    if(customer.getMechanicApuration() != null) {
	    	if(customer.getMechanicApuration().getName().equalsIgnoreCase("Linear")) {
	    		List<Employee> mechanics = customer.getEmployees().stream()
	    	            .filter(emp -> emp.getFunctions().stream().anyMatch(func -> func.getName().equalsIgnoreCase("Mecânico")))
	    	            .collect(Collectors.toList());
	    		
	    		for (Employee emp : mechanics) {
	    	        for (Product product : customer.getGroup().getProducts()) {
	    	            for (Function function : emp.getFunctions()) {
	    	            	int totalQuantity =  calculateConsultantTotalQuantity(sales, product, customer);
		            		BigDecimal mechanicQuantity = new BigDecimal(mechanics.size());
	    	            	
	    	            	System.out.println("Premiado - " + emp.getName());
			            	System.out.println("Função - " + function.getName());
			            	System.out.println("produto vendido - " + product.getProductCode() + " " + product.getProductName());
			            	System.out.println("Quantidade de produtos - " + totalQuantity);
	    	            	
	    	                IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);
	    	                
	    	                BigDecimal ccValue = BigDecimal.ZERO;
	    	                BigDecimal nfsValue = BigDecimal.ZERO;
	    	                
	    	                if(value != null) {
	    	                	System.out.println(value);
	    	                	ccValue = value.getCcValue().divide(mechanicQuantity).multiply(BigDecimal.valueOf(totalQuantity));
	    		                nfsValue = value.getNfsValue().divide(mechanicQuantity).multiply(BigDecimal.valueOf(totalQuantity));
				                System.out.println("\ncc - " + ccValue + " nfs - " + nfsValue + "\n");
	    		                incentives.addAll(createIncentives(ccValue, nfsValue, emp, function, customer, documentNumber, user, apurationTypes));
	    	                }
 
	    	            }
	    	        }
	    	    }
	    	}
	    }
	    System.out.println("   ------------------------------------------");
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

	    if (ccValue.compareTo(BigDecimal.ZERO) > 0) {
	        incentives.add(new Incentive(null, LocalDate.now(), user.getState(), employee.getPaymentMethod(), apurationTypes.get("Conta Corrente"),
	                employee, employee.getCpf(), ccValue, function, customer, saleOrdem, user));
	    }

	    if (nfsValue.compareTo(BigDecimal.ZERO) > 0) {
	        incentives.add(new Incentive(null, LocalDate.now(), user.getState(), employee.getPaymentMethod(), apurationTypes.get("NF Serviço"),
	                employee, employee.getCpf(), nfsValue, function, customer, saleOrdem, user));
	    }
	    
	    return incentives;
	}

	public List<Incentive> compactIncentives(List<Incentive> incentives) {
	    Map<String, Map<String, BigDecimal>> groupedIncentives = incentives.stream()
	        .collect(Collectors.groupingBy(
	            incentive -> incentive.getEmployee().getName(),
	            Collectors.groupingBy(
	                incentive -> incentive.getApurationType().getName(),
	                Collectors.mapping(
	                    Incentive::getIncentiveValue,
	                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
	                )
	            )
	        ));

	    List<Incentive> compactedList = new ArrayList<>();

	    for (Map.Entry<String, Map<String, BigDecimal>> employeeEntry : groupedIncentives.entrySet()) {
	        String employeeName = employeeEntry.getKey();
	        Map<String, BigDecimal> apurationTotals = employeeEntry.getValue();

	        Incentive reference = incentives.stream()
	            .filter(incentive -> employeeName.equals(incentive.getEmployee().getName()))
	            .findFirst()
	            .orElse(null);

	        if (reference != null) {
	            for (Map.Entry<String, BigDecimal> apurationEntry : apurationTotals.entrySet()) {
	                BigDecimal totalValue = apurationEntry.getValue();

	                Incentive compactedIncentive = new Incentive();
	                compactedIncentive.setState(reference.getState());
	                compactedIncentive.setPaymentMethod(reference.getPaymentMethod());
	                compactedIncentive.setEmployeeFunction(reference.getEmployeeFunction());
	                compactedIncentive.setSaleDocumentNumber(reference.getSaleDocumentNumber());
	                compactedIncentive.setUser(reference.getUser());
	                compactedIncentive.setEmployee(reference.getEmployee());
	                compactedIncentive.setCpf(reference.getCpf());
	                compactedIncentive.setReferenceDate(reference.getReferenceDate());
	                compactedIncentive.setCustomer(reference.getCustomer());
	                compactedIncentive.setApurationType(reference.getApurationType());
	                compactedIncentive.setIncentiveValue(totalValue);

	                compactedList.add(compactedIncentive);
	            }
	        }
	    }

	    printCompactIncentives(compactedList);

	    return compactedList;
	}


	
	public void printCompactIncentives(List<Incentive> compactedIncentives) {
		System.out.println("\nINCENTIVOS COMPACTADOS\n");
	    compactedIncentives.forEach(i -> System.out.println(i));
	}

	
}





































