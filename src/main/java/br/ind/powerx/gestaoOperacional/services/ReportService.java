package br.ind.powerx.gestaoOperacional.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveReportDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductReportDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleReportDTO;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class ReportService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private IncentiveRepository incentiveRepository;

    public void generateReportSaleAndIncetivesByDocumentNumber(
            Integer documentNumber, HttpServletResponse response) throws IOException, JRException {

        List<Sale> sales = saleRepository.findByDocumentNumber(documentNumber);
        List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(documentNumber);

        String customerName = sales.get(0).getCustomer().getFantasyName();
        String customerCNPJ = sales.get(0).getCustomer().getCnpj();
        LocalDate localDate = incentives.get(0).getReferenceDate();
        Date utilDate = Date.valueOf(localDate);
        String method = sales.get(0).getEmployee().getPaymentMethod().getName();

        List<SaleReportDTO> salesDto = sales.stream()
            .collect(Collectors.groupingBy(sale -> sale.getEmployee().getName()))
            .entrySet()
            .stream()
            .map(entry -> {
                String employeeName = entry.getKey();
                List<ProductReportDTO> products = entry.getValue().stream()
                    .map(sale -> new ProductReportDTO(
                        sale.getProduct().getProductCode(),
                        sale.getProduct().getProductName(),
                        sale.getQuantity()))
                    .collect(Collectors.toList());
                return new SaleReportDTO(employeeName, products);
            })
            .collect(Collectors.toList());
        
        List<IncentiveReportDTO> incentivesDto = incentives.stream().map(incentive -> 
        new IncentiveReportDTO(incentive.getState().getState(),
                               incentive.getApurationType().getName(),
                               incentive.getEmployee().getName(),
                               incentive.getIncentiveValue(),
                               incentive.getEmployeeFunction().getName())
    ).collect(Collectors.toList());

        JRBeanCollectionDataSource salesDataSource = new JRBeanCollectionDataSource(salesDto);

        Map<String, Object> params = new HashMap<>();
        params.put("documentNumber", documentNumber); 
        params.put("customerName", customerName);     
        params.put("customerCNPJ", customerCNPJ);     
        params.put("referenceDate", utilDate);        
        params.put("method", method);    
        params.put("incentives", incentivesDto);

        InputStream reportStream = getClass().getResourceAsStream("/jasper/sale_incentives_report.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, salesDataSource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_" + documentNumber + ".pdf");

        try (OutputStream outStream = response.getOutputStream()) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            outStream.flush();
        }
    }



	
	public void generateLastReportSaleAndIncetives(HttpServletResponse response) throws IOException, JRException {
	    
		Integer lastDocumentNumber = saleRepository.findMaxDocumentNumber();
		
	    List<Sale> sales = saleRepository.findByDocumentNumber(lastDocumentNumber);
	    List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(lastDocumentNumber);
	    String customerName = sales.get(0).getCustomer().getFantasyName();

	    List<SaleReportDTO> salesDto = sales.stream()
        	    .collect(Collectors.groupingBy(sale -> sale.getEmployee().getName()))
        	    .entrySet()
        	    .stream()
        	    .map(entry -> {
        	        String employeeName = entry.getKey(); 
        	        List<ProductReportDTO> products = entry.getValue().stream() 
        	            .map((Sale sale) -> new ProductReportDTO(
        	                sale.getProduct().getProductCode(),
        	                sale.getProduct().getProductName(),
        	                sale.getQuantity()))
        	            .collect(Collectors.toList());
        	        return new SaleReportDTO(employeeName, products);
        	    })
        	    .collect(Collectors.toList());

	    List<IncentiveReportDTO> incentivesDto = incentives.stream().map(incentive -> 
	        new IncentiveReportDTO(incentive.getState().getState(), 
	                               incentive.getApurationType().getName(),
	                               incentive.getEmployee().getName(),
	                               incentive.getIncentiveValue(),
	                               incentive.getEmployeeFunction().getName())
	    ).collect(Collectors.toList());
	    
	    System.out.println("Vendas: " + salesDto.size());
	    System.out.println("Incentivos: " + incentivesDto.size());
	    
	    salesDto.forEach(s->System.out.println(s));
	    incentivesDto.forEach(s->System.out.println(s));

	    InputStream reportStream = getClass().getResourceAsStream("/jasper/sale_incentives_report.jasper");

	    JRBeanCollectionDataSource salesDataSource = new JRBeanCollectionDataSource(salesDto);
	    JRBeanCollectionDataSource incentivesDataSource = new JRBeanCollectionDataSource(incentivesDto);

	    Map<String, Object> params = new HashMap<>();
	    params.put("documentNumber", lastDocumentNumber);
	    params.put("customerName", customerName);
	    params.put("sales", salesDataSource);
	    params.put("incentivesDataSet", incentivesDataSource);

	    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
	    

	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "attachment; filename=relatorio_" + lastDocumentNumber + ".pdf");

	    try (OutputStream outStream = response.getOutputStream()) {
	        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	        outStream.flush();
	    }
	}

}















