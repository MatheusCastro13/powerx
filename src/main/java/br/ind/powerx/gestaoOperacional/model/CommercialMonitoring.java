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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommercialMonitoring {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "monitoring_name", unique = true, nullable = false, length = 100)
	private String name;
	
	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
    @OneToMany(mappedBy = "commercialMonitoring", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    private List<MonitoringSale> monitoringSales = new ArrayList<>();
    
    @OneToMany(mappedBy = "commercialMonitoring", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    private List<ProductStock> productStocks = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
 
    public void addMonitoringSale(MonitoringSale sale) {
    	if(sale != null && !monitoringSales.contains(sale)) {
    		monitoringSales.add(sale);
    		if(sale.getCommercialMonitoring() != this) {
    			sale.setCommercialMonitoring(this);
    		}
    	}
    }
    
    public void removeMonitoringSale(MonitoringSale sale) {
    	if(monitoringSales.remove(sale)){
    		if(sale.getCommercialMonitoring() == this) {
    			sale.setCommercialMonitoring(null);
    		}
    	}
    }
    
    public void addProductStock(ProductStock productStock) {
    	if(productStock != null && !productStocks.contains(productStock)) {
    		productStocks.add(productStock);
    		if(productStock.getCommercialMonitoring() != this) {
    			productStock.setCommercialMonitoring(this);
    		}
    	}
    }
    
    public void removeProductStock(ProductStock productStock) {
    	if(productStocks.remove(productStock)){
    		if(productStock.getCommercialMonitoring() == this) {
    			productStock.setCommercialMonitoring(null);
    		}
    	}
    }
	
}
























