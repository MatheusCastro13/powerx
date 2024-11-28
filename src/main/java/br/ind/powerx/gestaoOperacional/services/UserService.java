package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.RegisterDTO;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CustomerService customerService;

	public void save(RegisterDTO registerDTO) throws BadRequestException {
		if (this.userRepository.findByEmail(registerDTO.email()) != null) throw new BadRequestException();

		String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
		
		User user = new User();
		user.setId(null);
		user.setName(registerDTO.name());
		user.setEmail(registerDTO.email());
		user.setPasswordHash(encryptedPassword);
		user.setRole(registerDTO.role());
		user.setPosition(registerDTO.position());
		user.setState(registerDTO.state());
		user.setActive(true);
		user.setCustomers(null);
		user.setPrevisions(null);
		

		userRepository.save(user);
	}
	
	public void save(User userToSave) {
		String encryptedPassword = new BCryptPasswordEncoder().encode(userToSave.getPasswordHash());
		User user = new User();
		user.setId(null);
		user.setName(userToSave.getName());
		user.setEmail(userToSave.getEmail());
		user.setPasswordHash(encryptedPassword);
		user.setRole(userToSave.getRole());
		user.setPosition(userToSave.getPosition());
		user.setState(userToSave.getState());
		user.setActive(true);
		
		for(Customer c : userToSave.getCustomers()) {
			user.addCustomer(c);
		}
		
		userRepository.save(user);
		
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public List<User> findAll(){
		return userRepository.findAll();
	}

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + id));
		
	}

	@Transactional
    public void update(User user, List<Long> customerIds) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + user.getId()));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setPosition(user.getPosition());
        existingUser.setState(user.getState());
        existingUser.setActive(true);

        List<Customer> updatedCustomers = customerService.findAllById(customerIds);
        List<Customer> currentCustomers = existingUser.getCustomers();
        
        for(Customer c : currentCustomers) {
        	c.setUser(null);
        	customerService.save(c);
        }
        
        existingUser.getCustomers().clear();
        userRepository.save(existingUser);
        
        for(Customer c : updatedCustomers) {
        	existingUser.addCustomer(c);
        }

        userRepository.save(existingUser);
    }

	public List<User> filterUsers(List<String> positions, List<String> states) {
        if ((positions == null || positions.isEmpty()) && (states == null || states.isEmpty())) {
            return userRepository.findAll();
        }

        if (positions != null && !positions.isEmpty() && (states == null || states.isEmpty())) {
            return userRepository.findByPositionIn(positions);
        }

        if ((positions == null || positions.isEmpty()) && states != null && !states.isEmpty()) {
            return userRepository.findByStateIn(states);
        }

        return userRepository.findByPositionInAndStateIn(positions, states);
    }

	public List<User> findAllByActiveTrue() {
		return userRepository.findAllByActiveTrue();
	}
}









