package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);
	
    List<User> findByPositionIn(List<String> positions);

    List<User> findByStateIn(List<String> states);

    List<User> findByPositionInAndStateIn(List<String> positions, List<String> states);

	List<User> findAllByActiveTrue();
}
