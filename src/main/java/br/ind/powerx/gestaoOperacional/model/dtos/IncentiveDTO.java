package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncentiveDTO {
    private LocalDate referenceDate;
    private String state;
    private String apurationType;
    private String paymentMethod;
    private String cpf;
    private String employeeName;
    private BigDecimal incentiveValue;
    private String functionName;
    private String customerName;
    private String customerCnpj;
}

