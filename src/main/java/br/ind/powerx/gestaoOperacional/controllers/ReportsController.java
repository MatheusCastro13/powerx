package br.ind.powerx.gestaoOperacional.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.services.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("/reports")
public class ReportsController {
	
	@Autowired
	private ReportService reportService;

	@GetMapping("/sales/{documentNumber}")
	public void getReport(@PathVariable Integer documentNumber, HttpServletResponse response) throws IOException, JRException {
		response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_" + documentNumber + ".pdf");

        reportService.generateReportSaleAndIncetivesByDocumentNumber(documentNumber, response);
	}
	
	@GetMapping("/last")
	public void getReport(HttpServletResponse response) throws IOException, JRException {
		response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio.pdf");

        reportService.generateLastReportSaleAndIncetives(response);
	}
}
