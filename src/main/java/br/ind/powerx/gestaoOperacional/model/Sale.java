  package br.ind.powerx.gestaoOperacional.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "sale")
public class Sale {

	@Id
	@GeneratedValue
	private Long id;
	
	@Past
	@Column(name = "reference_date", nullable = false)
	private LocalDate referenceDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "product_id")
	private Product product;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	@OneToMany(mappedBy = "sale", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Incentive> incentives = new ArrayList<>();
	
	public Sale(Customer customer, Employee employee, Product product, Integer quantity) {
		this.customer = customer;
		this.employee = employee;
		this.product = product;
		this.quantity = quantity;
		
	}
	
	public void setCustomer(Customer customer) {
		if(customer != null) {
			this.customer = customer;
		}
	}
	
	public void setEmployee(Employee employee) {
		if(employee != null) {
			this.employee = employee;
		}
	}
	
	public void setProduct(Product product) {
		if(product != null) {
			this.product = product;
		}
	}
	
	public void addIncentive(Incentive incentive) {
		if(incentive != null && !incentives.contains(incentive)) {
			incentives.add(incentive);
			incentive.setSale(this);
		}
	}
	
	public void removeIncentive(Incentive incentive) {
		if(incentives.remove(incentive)) {
			incentive.setSale(null);
		}
	}
	
	@Override
	public String toString() {
		
		return "Cliente: Nome - " + this.customer.getRegisteredName()
					+ "\n cnpj - " + this.customer.getCnpj() +
					"\nVendedor: Nome - " + this.employee.getName()
					+ "\n cpf - " + this.employee.getCpf() + 
					"\nProduto: Nome - " + this.product.getProductName()
					+ "\n codigo - " + this.product.getProductCode() +
					"\nQuantidade: Nome - " + this.quantity
					; 
	}
	
}
























