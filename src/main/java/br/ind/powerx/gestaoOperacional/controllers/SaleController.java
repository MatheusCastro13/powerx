  package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SalesDTO;
import br.ind.powerx.gestaoOperacional.services.SaleService;
import br.ind.powerx.gestaoOperacional.util.SaleQuantityValidator;
import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/sale")
public class SaleController {
	
	private final SaleService saleService;
    private final SaleQuantityValidator saleQuantityValidator;

    @Autowired
    public SaleController(SaleService saleService, SaleQuantityValidator saleQuantityValidator) {
        this.saleService = saleService;
        this.saleQuantityValidator = saleQuantityValidator;
    }
	
	@PostMapping("/calcule")
	@ResponseBody
	public ResponseEntity<?> saveSales(@RequestBody List<SalesDTO> sales) {
        System.out.println("ENTROU NO CONTROLADOR");

        try {
            boolean valid = saleQuantityValidator.check(sales);
            if (!valid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Existem quantidades de aplicações maiores que as quantidades de vendas.");
            }

            List<Incentive> incentives = saleService.saveSales(sales);

            List<IncentiveDTO> dtoList = incentives.stream()
                    .map(incentive -> new IncentiveDTO(
                            incentive.getReferenceDate(),
                            incentive.getState().toString(),
                            incentive.getApurationType().getName(),
                            incentive.getPaymentMethod().getName(),
                            incentive.getCpf(),
                            incentive.getEmployee().getName(),
                            incentive.getIncentiveValue(),
                            incentive.getEmployeeFunction().getName(),
                            incentive.getCustomer().getFantasyName(),
                            incentive.getCustomer().getCnpj()))
                    .collect(Collectors.toList());

            dtoList.forEach(System.out::println);
            return ResponseEntity.ok(dtoList);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar as vendas. Tente novamente.");
        }
    }


}
