package br.ind.powerx.gestaoOperacional.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "cnpj", unique = true, nullable = false, length = 18)
	private String cnpj;
	
	@Column(name = "registered_name", unique = true, nullable = false, length = 255)
	private String registeredName;
	
	@Column(length = 255)
	private String address;
	
	@Column(nullable = false)
	private boolean active;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "group_id")
	private Group group;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mechanic_apuration_id")
	private MechanicApuration mechanicApuration;
	
	@ManyToMany
	@JoinTable(name = "customer_employee",
    joinColumns = @JoinColumn(name = "customer_id"),
    inverseJoinColumns = @JoinColumn(name = "employee_id"))
	private List<Employee> employees = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	private List<Incentive> incentives = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	private List<Sale> sales = new ArrayList<>();
	
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void addSale(Sale sale) {
		if(sale != null && !sales.contains(sale)) {
			sales.add(sale);
			sale.setCustomer(this);
		}
	}
	
	public void removeSale(Sale sale) {
		if(sales.remove(sale)) {
			sale.setCustomer(null);
		}
	}
	
	public void addIncentive(Incentive incentive) {
		if(incentive != null && !incentives.contains(incentive)) {
			incentives.add(incentive);
			incentive.setCustomer(this);
		}
	}
	
	public void removeIncentive(Incentive incentive) {
		if(incentives.remove(incentive)) {
			incentive.setCustomer(null);
		}
	}
	
	public void addEmployee(Employee emp) {
		if(emp != null && !employees.contains(emp)) {
			employees.add(emp);
			emp.addCustomer(this);
		}
	}
	
	public void removeEmployee(Employee emp) {
		if(employees.remove(emp)) {
			emp.removeCustomer(this);
		}
	}
	
}











