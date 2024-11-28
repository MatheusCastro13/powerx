package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.repositories.PartnerGroupRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PartnerGroupService {

	@Autowired 
	private PartnerGroupRepository groupRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ProductService productService;
	
	public List<Group> findAll() {
		return groupRepository.findAll();
	}

	public void save(Group groupToSave) {
		Group group = new Group();
		group.setName(groupToSave.getName());
		
		for(Customer c : groupToSave.getCustomers()) {
			group.addCustomer(c);
		}
		
		for(Product p : groupToSave.getProducts()) {
			group.addProduct(p);
		}
		
		groupRepository.save(group);
		
	}
	
	public void update(Group groupToUpdate) {
		Group group = groupRepository.findById(groupToUpdate.getId())
				.orElseThrow( ()-> new EntityNotFoundException("Grupo não encontrado"));
		
		group.setName(groupToUpdate.getName());
		
		List<Customer> currenteCustomers = group.getCustomers();
		List<Customer> customersToUpdate = groupToUpdate.getCustomers();
		
		for(Customer c : currenteCustomers) {
			c.setGroup(null);
			customerService.save(c);
		}
		
		group.getCustomers().clear();
		groupRepository.save(group);
		
		for(Customer c : customersToUpdate) {
			group.addCustomer(c);
		}
		
		groupRepository.save(group);
		
		List<Product> currenteProducts = group.getProducts();
		List<Product> productsToUpdate = groupToUpdate.getProducts();
		
		for(Product p : currenteProducts) {
			p.removeGroup(group);
			productService.save(p);
		}
		
		group.getProducts().clear();
		groupRepository.save(group);
		
		for(Product p : productsToUpdate) {
			group.addProduct(p);
		}
		
		groupRepository.save(group);
	}

}



















