package br.ind.powerx.gestaoOperacional.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.services.SpreadsheetService;

@RestController
@RequestMapping("/order")
public class OrderController {

	private final SpreadsheetService spreadsheetService;
	
	@Autowired
	public OrderController(SpreadsheetService spreadsheetService) {
		this.spreadsheetService = spreadsheetService;
	}
	
	@GetMapping()
	public ResponseEntity<byte[]> gerarPlanilha(
	        @RequestParam String dataInicial,
	        @RequestParam String dataFinal,
	        @RequestParam Long clienteId,
	        @RequestParam Long tipoApuracaoId,
	        @RequestParam String formato) throws IOException {

	    LocalDate inicio = LocalDate.parse(dataInicial);
	    LocalDate fim = LocalDate.parse(dataFinal);

	    ByteArrayOutputStream file;
	    String fileName;
	    MediaType mediaType;
	    
	    if(!formato.equalsIgnoreCase("csv")) {
	    	file = spreadsheetService.generatePaymentOrder(inicio, fim, clienteId, tipoApuracaoId);
	    	fileName = "pedido.csv";
	        mediaType = MediaType.parseMediaType("text/csv");
	    }
	    else {
	    	file = spreadsheetService.generatePaymentOrder(inicio, fim, clienteId, tipoApuracaoId);
	    	fileName = "pedido.xlsx";
	        mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    }
	    
	    byte[] fileContent = file.toByteArray();
	    
	    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
        .contentType(mediaType)
        .contentLength(fileContent.length)
        .body(fileContent);
	    
	}
	
	@GetMapping("/state")
	public ResponseEntity<byte[]> gerarPlanilha(
	        @RequestParam String dataInicial,
	        @RequestParam String dataFinal,
	        @RequestParam Long tipoApuracaoId,
	        @RequestParam String state,
	        @RequestParam String formato) throws IOException {

	    LocalDate inicio = LocalDate.parse(dataInicial);
	    LocalDate fim = LocalDate.parse(dataFinal);

	    ByteArrayOutputStream spreedsheet = spreadsheetService.generatePaymentOrder(inicio, fim, tipoApuracaoId, state);

	    byte[] fileContent = spreedsheet.toByteArray();

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pedido.xlsx")
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .contentLength(fileContent.length)
	            .body(fileContent);
	}

}







