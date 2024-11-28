package br.ind.powerx.gestaoOperacional.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.MechanicApurationRepository;
import br.ind.powerx.gestaoOperacional.repositories.PartnerGroupRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	
	private final EmployeeService employeeService;
	
	private final PartnerGroupRepository groupRepository;
	
	private final UserRepository userRepository;
	
	private final MechanicApurationRepository mechanicApurationuserRepository;
	
	
	@Autowired
    public CustomerService(CustomerRepository customerRepository, EmployeeService employeeService,
    						PartnerGroupRepository groupRepository, UserRepository userRepository, 
    						MechanicApurationRepository mechanicApurationuserRepository) {
        this.customerRepository = customerRepository;
        this.employeeService = employeeService;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mechanicApurationuserRepository = mechanicApurationuserRepository;
    }
	
	@Transactional
	public void save(Customer customer, Long userId, Long groupId, Long mechanicApurationId, List<Long> employees) {
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
		customer.setUser(user);

		Group group = groupRepository.findById(groupId)
		        .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
		customer.setGroup(group);

		MechanicApuration mechanicApuration = mechanicApurationuserRepository.findById(mechanicApurationId)
				.orElseThrow(()-> new EntityNotFoundException("Apuração de mecânico não encontrada"));
		customer.setMechanicApuration(mechanicApuration);
		
		customerRepository.save(customer);

	}
	
	public void save(Customer customer) {
		customerRepository.save(customer);
	}
	
	@Transactional
	public void update(Customer customerToUpdate, Long userId, Long groupId, Long mechanicApurationId, List<Long> employees) {
		Customer existingCustomer = customerRepository.findById(customerToUpdate.getId())
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
		existingCustomer.setCnpj(customerToUpdate.getCnpj());
		existingCustomer.setRegisteredName(customerToUpdate.getRegisteredName());
		existingCustomer.setAddress(customerToUpdate.getAddress());
		
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
		group.addCustomer(existingCustomer);
		existingCustomer.setGroup(group);
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
		user.addCustomer(existingCustomer);
		existingCustomer.setUser(user);
		
		MechanicApuration mechanicApuration = mechanicApurationuserRepository.findById(mechanicApurationId)
				.orElseThrow(()-> new EntityNotFoundException("Apuração de mecânico não encontrada"));
		mechanicApuration.addCustomer(existingCustomer);
		existingCustomer.setMechanicApuration(mechanicApuration);
		
		List<Employee> currentEmployees = existingCustomer.getEmployees();
		
		List<Employee> employeesToUpdate = employeeService.findAllById(employees);
		
		List<Employee> employeesToRemove = new ArrayList<>(currentEmployees);

		for (Employee e : employeesToRemove) {
		    e.removeCustomer(existingCustomer);
		    employeeService.save(e);
		}

		existingCustomer.getEmployees().clear();
		customerRepository.save(existingCustomer);

		
		for(Employee e : employeesToUpdate) {
			existingCustomer.addEmployee(e);
		}
		
		existingCustomer.setActive(true);
		
		customerRepository.save(existingCustomer);
	}
	
	public List<Customer> filterCustomers(List<Long> users, List<Long> groups) {

        if ((users == null || users.isEmpty()) && (groups == null || groups.isEmpty())) {
            return customerRepository.findAll();
        }

        if (users != null && !users.isEmpty() && (groups == null || groups.isEmpty())) {
            return customerRepository.findByUserIdIn(users);
        }

        if ((users == null || users.isEmpty()) && groups != null && !groups.isEmpty()) {
            return customerRepository.findByGroupIdIn(groups);
        }

        return customerRepository.findByUserIdInAndGroupIdIn(users, groups);
    }
	
	
	public List<Customer> findAllByActiveTrue(){
		return customerRepository.findAllByActiveTrue();
	}


	public List<Customer> findAllByUserIdNull() {
		return customerRepository.findAllByUserIdNull();
	}


	public List<Customer> findAllById(List<Long> customers) {
		return customerRepository.findAllById(customers);
	}


	public List<Customer> findAllByGroupIdNull() {
		return customerRepository.findAllByGroupIdNull();
	}

	public Optional<Customer> findById(Long apurationTypeId) {
		return customerRepository.findById(apurationTypeId);
	}
}
