package br.ind.powerx.gestaoOperacional.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.util.interfaces.PayableOrder;

public class YouCardPaymentOrder implements PayableOrder {
    private List<Incentive> incentives;

    public YouCardPaymentOrder(List<Incentive> incentives) {
        this.incentives = incentives;
    }

    @Override
    public ByteArrayOutputStream generateOrder() throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        	CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
        			.setHeader("Documento", "Nome", "Valor", "Obsevacao")
        			.build())){
        	
        	for(Incentive incentive : incentives) {
        		String value = formatIncentiveValue(incentive.getIncentiveValue());
        		csvPrinter.printRecord(incentive.getCpf(), incentive.getEmployee().getName(), value, incentive.getCustomer().getFantasyName());
        	}
        	
        	csvPrinter.flush();
        }

        return outputStream;
    }
    
    private String formatIncentiveValue(BigDecimal value) {
    	return value.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
    }

}
