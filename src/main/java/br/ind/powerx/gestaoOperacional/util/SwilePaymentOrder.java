package br.ind.powerx.gestaoOperacional.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.util.interfaces.PayableOrder;

public class SwilePaymentOrder implements PayableOrder{
	
	private List<Incentive> incentives;
	
	public SwilePaymentOrder(List<Incentive> incentives) {
		this.setIncentiveList(incentives);
	}
	
	@Override
	public ByteArrayOutputStream generateOrder() throws IOException {

	    InputStream fis = getClass().getClassLoader().getResourceAsStream("spreadsheets/modelo-swile.xlsx");
	    
	    if (fis == null) {
	        throw new IOException("Arquivo modelo-swile.xlsx não encontrado no classpath.");
	    }

	    Workbook workbook = new XSSFWorkbook(fis);
	    Sheet sheet = workbook.getSheetAt(0);
	    
	    int currentRow = 1;

	    for (Incentive incentive : incentives) {
	        Row row = sheet.createRow(currentRow++);
	        row.createCell(0).setCellValue(incentive.getEmployee().getName());
	        row.createCell(1).setCellValue(incentive.getEmployee().getCpf());

	        Cell valueCell = row.createCell(2);
	        valueCell.setCellValue(incentive.getIncentiveValue().doubleValue());

	        CellStyle currencyStyle = workbook.createCellStyle();
	        DataFormat format = workbook.createDataFormat();
	        currencyStyle.setDataFormat(format.getFormat("R$ #,##0.00"));
	        valueCell.setCellStyle(currencyStyle);
	    }

	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    workbook.write(outputStream);

	    workbook.close();
	    fis.close();

	    return outputStream;
	}
	
	
	public void setIncentiveList(List<Incentive> incentiveList) {
		this.incentives = incentiveList;
	}

}









