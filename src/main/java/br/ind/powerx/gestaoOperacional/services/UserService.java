package br.ind.powerx.gestaoOperacional.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.RegisterDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.UserUpdateDTO;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomerService customerService;

	public void save(RegisterDTO registerDTO) throws BadRequestException {
		if (this.userRepository.findByEmail(registerDTO.email()) != null)
			throw new BadRequestException();

		String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());

		User user = new User();
		user.setId(null);
		user.setName(registerDTO.name());
		user.setEmail(registerDTO.email());
		user.setPhone(registerDTO.phone());
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
		user.setUnysoftCode(userToSave.getUnysoftCode());
		user.setName(userToSave.getName());
		user.setCpf(userToSave.getCpf());
		user.setBirthday(userToSave.getBirthday());
		user.setAddress(userToSave.getAddress());
		user.setEmail(userToSave.getEmail());
		user.setPhone(userToSave.getPhone());
		user.setPasswordHash(encryptedPassword);
		user.setRole(userToSave.getRole());
		user.setPosition(userToSave.getPosition());
		user.setState(userToSave.getState());
		user.setActive(true);
		user.setStartOfActivities(userToSave.getStartOfActivities());

		for (Customer c : userToSave.getCustomers()) {
			user.addCustomer(c);
		}

		userRepository.save(user);

	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + id));

	}

	@Transactional
	public void update(UserUpdateDTO userUpdateDTO, List<Long> customerIds) {
		User existingUser = getUserById(userUpdateDTO.id());

		updateUserDetails(existingUser, userUpdateDTO);

		if (customerIds != null) {
			updateUserCustomers(existingUser, customerIds);
		}

		userRepository.save(existingUser);
	}

	private User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + id));
	}

	private void updateUserDetails(User user, UserUpdateDTO dto) {
		user.setUnysoftCode(dto.unysoftCode());
		user.setName(dto.name());
		user.setCpf(dto.cpf());
		user.setAddress(dto.address());
		user.setBirthday(dto.birthday());
		user.setEmail(dto.email());
		user.setPhone(dto.phone());
		user.setRole(dto.role());
		user.setPosition(dto.position());
		user.setState(dto.state());
		user.setActive(true);
	}
	
	private void updateUserDetails(User user, User dto) {
		user.setUnysoftCode(dto.getUnysoftCode());
		user.setName(dto.getName());
		user.setCpf(dto.getCpf());
		user.setAddress(dto.getAddress());
		user.setBirthday(dto.getBirthday());
		user.setEmail(dto.getEmail());
		user.setRole(dto.getRole());
		user.setPosition(dto.getPosition());
		user.setState(dto.getState());
		user.setActive(true);
	}
	
	private void updateUserCustomers(User user, List<Long> customerIds) {
	    List<Customer> updatedCustomers = customerService.findAllById(customerIds);

	    for (Customer customer : new ArrayList<>(user.getCustomers())) {
	        user.removeCustomer(customer);
	    }

	    for (Customer customer : updatedCustomers) {
	        user.addCustomer(customer);
	    }
	}


	public Page<User> filterUsers(List<String> positions, List<String> states, Pageable pageable) {
		Specification<User> spec = Specification.where(null);

		System.out.println("States no service :" + states);
		System.out.println("Positions no service :" + positions);

		if ((positions != null && !positions.isEmpty())) {
			spec = spec.and(UserSpecifications.positionsIn(positions));
		}

		if ((states != null && !states.isEmpty())) {
			spec = spec.and(UserSpecifications.statesIn(states));
		}

		return userRepository.findAll(spec, pageable);
	}

	public List<User> findAllByActiveTrue() {
		return userRepository.findAllByActiveTrue();
	}


	@Transactional
	public void update(Long id, User user) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User não encontrado"));
		updateUserDetails(existingUser, user);
	}
}




