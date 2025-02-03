package br.ind.powerx.gestaoOperacional.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.dtos.SalesDTO;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.EmployeeService;
import jakarta.persistence.EntityNotFoundException;

@Component
public class SaleQuantityValidator {

    private final EmployeeService empService;
    private final CustomerService customerService;

    @Autowired
    public SaleQuantityValidator(EmployeeService empService, CustomerService customerService) {
        this.empService = empService;
        this.customerService = customerService;
    }

    public boolean check(List<SalesDTO> sales) {
        if (sales.isEmpty()) {
            throw new IllegalArgumentException("A lista de vendas está vazia.");
        }

        Customer customer = customerService.findById(sales.get(0).customerId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!"Apontamento".equalsIgnoreCase(customer.getMechanicApuration().getName())) {
            return true;
        }

        Map<Long, Employee> employees = empService.findAllById(
            sales.stream().map(SalesDTO::employeeId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Employee::getId, e -> e));

        Map<Long, Integer> consultantTotals = calculateTotals(sales, employees, "Consultor Técnico");
        Map<Long, Integer> mechanicTotals = calculateTotals(sales, employees, "Mecânico");

        for (Map.Entry<Long, Integer> entry : mechanicTotals.entrySet()) {
            Long productId = entry.getKey();
            Integer mechanicQuantity = entry.getValue();
            Integer consultantQuantity = consultantTotals.getOrDefault(productId, 0);

            if (mechanicQuantity > consultantQuantity) {
                return false;
            }
        }

        return true;
    }

    private Map<Long, Integer> calculateTotals(List<SalesDTO> sales, Map<Long, Employee> employees, String functionName) {
        Map<Long, Integer> totalPerProduct = new HashMap<>();

        sales.stream()
            .filter(sale -> employees.get(sale.employeeId())
                                     .getFunctions().stream()
                                     .anyMatch(f -> f.getName().equalsIgnoreCase(functionName)))
            .flatMap(sale -> sale.products().stream())
            .forEach(product -> totalPerProduct.merge(product.productId(), product.quantity(), Integer::sum));
        
        for (Map.Entry<Long, Integer> entry : totalPerProduct.entrySet()) {
        	System.out.println("\n \n \n \n \n \n \n \n \n \n \n AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        	System.out.println("Total do produto: " + entry.getKey() + " para " + functionName + ": "  + entry.getValue());
        }

        return totalPerProduct;
    }
}
