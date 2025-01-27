package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Flag;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Industry;
import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerUpdateDTO;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.FlagRepository;
import br.ind.powerx.gestaoOperacional.repositories.IndustryRepository;
import br.ind.powerx.gestaoOperacional.repositories.MechanicApurationRepository;
import br.ind.powerx.gestaoOperacional.repositories.PartnerGroupRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.CustomerSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	
	private final EmployeeService employeeService;
	
	private final PartnerGroupRepository groupRepository;
	
	private final IndustryRepository industryRepository;
	
	private final FlagRepository flagRepository;
	
	private final UserRepository userRepository;
	
	private final MechanicApurationRepository mechanicApurationuserRepository;
	
	
	@Autowired
    public CustomerService(CustomerRepository customerRepository, EmployeeService employeeService,
    						PartnerGroupRepository groupRepository, IndustryRepository industryRepository, 
    						FlagRepository flagRepository, UserRepository userRepository, 
    						MechanicApurationRepository mechanicApurationuserRepository) {
        this.customerRepository = customerRepository;
        this.employeeService = employeeService;
        this.groupRepository = groupRepository;
        this.industryRepository = industryRepository;
        this.flagRepository = flagRepository;
        this.userRepository = userRepository;
        this.mechanicApurationuserRepository = mechanicApurationuserRepository;
    }
	
	@Transactional
	public void save(Customer customer, Long userId, Long groupId, Long industryId, Long flagId, Long mechanicApurationId, List<Long> employees) {
		
		User user = userRepository.findById(userId)
		        .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
		customer.setUser(user);

		Group group = groupRepository.findById(groupId)
		        .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
		customer.setGroup(group);
		
		Industry industry = industryRepository.findById(industryId)
				.orElseThrow(() -> new EntityNotFoundException("Seguimento não encontrado"));
		customer.setIndustry(industry);
		
		Flag flag = flagRepository.findById(flagId)
				.orElseThrow(() -> new EntityNotFoundException("Marca/Bandeira não encontrado"));
		customer.setFlag(flag);

		MechanicApuration mechanicApuration = mechanicApurationuserRepository.findById(mechanicApurationId)
				.orElseThrow(()-> new EntityNotFoundException("Apuração de mecânico não encontrada"));
		customer.setMechanicApuration(mechanicApuration);
		
		customerRepository.save(customer);

	}
	
	public void save(Customer customer) {
		customerRepository.save(customer);
	}
	
	@Transactional
	public void update(CustomerUpdateDTO customerToUpdate, Long userId, Long groupId, Long industryId, Long flagId, Long mechanicApurationId, List<Long> employeeIds) {
	    Customer existingCustomer = customerRepository.findById(customerToUpdate.id())
	            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

	    existingCustomer.setUnysoftCode(customerToUpdate.unysoftCode());
	    existingCustomer.setCnpj(customerToUpdate.cnpj());
	    existingCustomer.setRegisteredName(customerToUpdate.registeredName());
	    existingCustomer.setFantasyName(customerToUpdate.fantasyName());
	    existingCustomer.setAddress(customerToUpdate.address());
	    existingCustomer.setActive(true);

	    existingCustomer.setGroup(groupRepository.findById(groupId)
	            .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado")));
	    existingCustomer.setIndustry(industryRepository.findById(industryId)
	            .orElseThrow(() -> new EntityNotFoundException("Seguimento não encontrado")));
	    existingCustomer.setFlag(flagRepository.findById(flagId)
	            .orElseThrow(() -> new EntityNotFoundException("Marca/Bandeira não encontrado")));
	    existingCustomer.setUser(userRepository.findById(userId)
	            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")));
	    existingCustomer.setMechanicApuration(mechanicApurationuserRepository.findById(mechanicApurationId)
	            .orElseThrow(() -> new EntityNotFoundException("Apuração de mecânico não encontrada")));

	    List<Employee> newEmployees = employeeService.findAllById(employeeIds);
	    existingCustomer.getEmployees().clear();
	    newEmployees.forEach(existingCustomer::addEmployee);

	}

	
	public Page<Customer> filterCustomers(List<Long> users, List<Long> groups, List<Long> industries, List<Long> flags, Pageable pageable) {

	    Specification<Customer> spec = Specification.where(null);

	    if (users != null && !users.isEmpty()) {
	        spec = spec.and(CustomerSpecifications.userIdIn(users));
	    }

	    if (groups != null && !groups.isEmpty()) {
	        spec = spec.and(CustomerSpecifications.groupIdIn(groups));
	    }

	    if (industries != null && !industries.isEmpty()) {
	        spec = spec.and(CustomerSpecifications.industryIdIn(industries));
	    }

	    if (flags != null && !flags.isEmpty()) {
	        spec = spec.and(CustomerSpecifications.flagIdIn(flags));
	    }

	    return customerRepository.findAll(spec, pageable);
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

	public Optional<Customer> findById(Long cutomerId) {
		return customerRepository.findById(cutomerId);
	}

	public Page<Customer> findAll(Pageable pageable) {
		return customerRepository.findAll(pageable);
	}

	public List<Customer> findAll() {
		return customerRepository.findAll();
	}
}
