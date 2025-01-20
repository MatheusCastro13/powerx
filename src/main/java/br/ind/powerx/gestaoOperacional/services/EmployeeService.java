package br.ind.powerx.gestaoOperacional.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeFilterDTO;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.EmployeeSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private FunctionRepository functionRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;
	
	@Transactional
	public void save(Employee emp, List<Long> functionsIds, List<Long> customersIds, Long paymentMethodsId, List<Long> apurationTypesIds) {
		List<Function> functions = functionRepository.findAllById(functionsIds);
		List<Customer> customers = customerRepository.findAllById(customersIds);
		List<ApurationType> apurationTypes = apurationTypeRepository.findAllById(apurationTypesIds);
		

		PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodsId)
				.orElseThrow(()-> new EntityNotFoundException("Método de pagamento não encontrado"));
		
		functions.stream()
			.forEach(f -> {
				f.addEmployee(emp);
				emp.addFunction(f);
			});
		
		customers.stream()
			.forEach(c ->{
				c.addEmployee(emp);
				emp.addCustomer(c);
			});
		
		apurationTypes.stream()
			.forEach(a -> {
				a.addEmployee(emp);
				emp.addApurationType(a);
			});
		
		paymentMethod.addEmployee(emp);
		emp.setPaymentMethod(paymentMethod);
		
		employeeRepository.save(emp);
	}
	
	@Transactional
	public void update(Long id, Employee emp, List<Long> functionsIds, List<Long> customersIds, Long paymentMethodId, List<Long> apurationTypesIds) {
	    List<Function> functions = functionRepository.findAllById(functionsIds);
	    List<Customer> customers = customerRepository.findAllById(customersIds);
	    List<ApurationType> apurationTypes = apurationTypeRepository.findAllById(apurationTypesIds);
	    PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
	            .orElseThrow(() -> new EntityNotFoundException("Método de pagamento não encontrado"));

	    Employee existingEmp = employeeRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Premiado não encontrado"));

	    existingEmp.setCpf(emp.getCpf());
	    existingEmp.setEmail(emp.getEmail());
	    existingEmp.setName(emp.getName());
	    existingEmp.setPhone(emp.getPhone());
	    existingEmp.setBirthDate(emp.getBirthDate());

	    List<Function> currentFunctions = new ArrayList<>(existingEmp.getFunctions());
	    List<Customer> currentCustomers = new ArrayList<>(existingEmp.getCustomers());
	    List<ApurationType> currentApurationTypes = new ArrayList<>(existingEmp.getApurationTypes());

	    for (Function f : currentFunctions) {
	        f.removeEmployee(existingEmp);
	        functionRepository.save(f);
	    }

	    for (Customer c : currentCustomers) {
	        c.removeEmployee(existingEmp);
	        customerRepository.save(c);
	    }

	    for (ApurationType a : currentApurationTypes) {
	        a.removeEmployee(existingEmp);
	        apurationTypeRepository.save(a);
	    }

	    paymentMethod.addEmployee(existingEmp);
	    paymentMethodRepository.save(paymentMethod);

	    existingEmp.getFunctions().clear();
	    existingEmp.getCustomers().clear();
	    existingEmp.getApurationTypes().clear();

	    for (Function f : functions) {
	        existingEmp.addFunction(f);
	        f.addEmployee(existingEmp);
	    }

	    for (Customer c : customers) {
	        existingEmp.addCustomer(c);
	        c.addEmployee(existingEmp);
	    }

	    for (ApurationType a : apurationTypes) {
	        existingEmp.addApurationType(a);
	        a.addEmployee(existingEmp);
	    }

	    existingEmp.setPaymentMethod(paymentMethod);

	    employeeRepository.save(existingEmp);
	}

	
	public List<Employee> findAllByActiveTrue(){
		return employeeRepository.findAllByActiveTrue();
	}
	
	public List<Employee> findAllById(Collection<Long> ids) {
	    if (ids == null || ids.isEmpty()) {
	        return Collections.emptyList();
	    }
	    ids.removeIf(Objects::isNull);
	    return employeeRepository.findAllById(ids);
	}
	

	public void save(Employee e) {
		employeeRepository.save(e);
		
	}

	public Page<Employee> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable); 
    }

	public Page<Employee> filter(EmployeeFilterDTO filter, Pageable pageable) {
		Specification<Employee> spec = Specification.where(null);
		
		if(filter.customers() != null && !filter.customers().isEmpty()) {
			spec = spec.and(EmployeeSpecifications.customersIn(filter.customers()));
		}
		
		if(filter.functions() != null && !filter.functions().isEmpty()) {
			spec = spec.and(EmployeeSpecifications.functionsIn(filter.customers()));
		}
		
		return employeeRepository.findAll(spec, pageable);
	}

	public Employee findById(Long employeeId) {
		return employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Premaido não encontrado"));
	}


}









