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
	
	@Column(name = "unydoft_code", unique = true, length = 10)
    private String unysoftCode;
	
	@Column(name = "cnpj", unique = true, nullable = false, length = 18)
	private String cnpj;
	
	@Column(name = "registered_name", unique = true, nullable = false, length = 255)
	private String registeredName;
	
	@Column(name = "fantasy_name", unique = true, nullable = false, length = 255)
	private String fantasyName;
	
	@Column(length = 255)
	private String address;
	
	@Column(nullable = false)
	private boolean active;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Group group;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mechanic_apuration_id")
	private MechanicApuration mechanicApuration;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "industry_id")
	private Industry industry;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flag_id")
	private Flag flag;
	
	@ManyToMany
	@JoinTable(name = "customer_employee",
    joinColumns = @JoinColumn(name = "customer_id"),
    inverseJoinColumns = @JoinColumn(name = "employee_id"))
	private List<Employee> employees = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Incentive> incentives = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Sale> sales = new ArrayList<>();
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TablePrice> tables = new ArrayList<>();
	
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
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setGroup(Group group) {
		this.group = group;
		group.addCustomer(this);
	}
	
	public void setMechanicApuration(MechanicApuration mechanicApuration) {
		this.mechanicApuration = mechanicApuration;
		mechanicApuration.addCustomer(this);
	}
	
	public void setIndustry(Industry industry) {
		this.industry = industry;
		industry.addCustomer(this);
	}
	
	public void setFlag(Flag flag) {
		this.flag = flag;
		flag.addCustomer(this);
	}
	
	public void addTable(TablePrice table) {
		if(table != null && !tables.contains(table)) {
			tables.add(table);
			if(table.getCustomer() != this) {
				table.setCustomer(this);
			}
		}
	}
	
	public void removeTable(TablePrice table) {
		if(tables.remove(table)) {
			if(table.getCustomer() == this) {
				table.setCustomer(null);
			}
		}
	}
}











