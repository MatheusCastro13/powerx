package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;

public class UserSpecifications {

	public static Specification<User> positionsIn(List<String> positionsStr) {
		List<Position> positions = positionsStr.stream().map(position -> Position.fromString(position)).collect(Collectors.toList());
		System.out.println("Positions passadas para o sepecifications" + positionsStr);	
		System.out.println("PositionsList transofrmada" + positions);	
        return (root, query, criteriaBuilder) -> 
            root.get("position").in(positions);
    }

    public static Specification<User> statesIn(List<String> statesStr) {
    	List<State> states = statesStr.stream().map(state -> State.fromString(state)).collect(Collectors.toList());
    	System.out.println("State passadas para o sepecifications" + statesStr);	
		System.out.println("StateList transofrmada" + states);	;	
        return (root, query, criteriaBuilder) -> 
            root.get("state").in(states);
    }
}
