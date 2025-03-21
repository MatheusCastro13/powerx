package br.ind.powerx.gestaoOperacional.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeBasicInfosDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeFilterDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeRelationshipDTO;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.EmployeeSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;

	@Autowired
	private ApurationTypeRepository apurationTypeRepository;

	@Transactional
	public void save(Employee emp, List<Long> functionsIds, List<Long> customersIds, Long paymentMethodsId,
			List<Long> apurationTypesIds) {
		List<Function> functions = functionRepository.findAllById(functionsIds);
		List<Customer> customers = customerRepository.findAllById(customersIds);
		List<ApurationType> apurationTypes = apurationTypeRepository.findAllById(apurationTypesIds);

		PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodsId)
				.orElseThrow(() -> new EntityNotFoundException("Método de pagamento não encontrado"));

		functions.stream().forEach(f -> {
			f.addEmployee(emp);
			emp.addFunction(f);
		});

		customers.stream().forEach(c -> {
			c.addEmployee(emp);
			emp.addCustomer(c);
		});

		apurationTypes.stream().forEach(a -> {
			a.addEmployee(emp);
			emp.addApurationType(a);
		});

		paymentMethod.addEmployee(emp);
		emp.setPaymentMethod(paymentMethod);

		employeeRepository.save(emp);
	}

	@Transactional
	public void update(Long id, Employee emp, List<Long> functionsIds, List<Long> customersIds, Long paymentMethodId,
			List<Long> apurationTypesIds) {
		List<Function> functions = functionRepository.findAllById(functionsIds);
		List<Customer> customers = customerRepository.findAllById(customersIds);
		List<ApurationType> apurationTypes = apurationTypeRepository.findAllById(apurationTypesIds);
		PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
				.orElseThrow(() -> new EntityNotFoundException("Método de pagamento não encontrado"));

		Employee existingEmp = employeeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Premiado não encontrado"));

		existingEmp.setCpf(emp.getCpf());
		existingEmp.setEmail(emp.getEmail());
		existingEmp.setName(emp.getName());
		existingEmp.setPhone(emp.getPhone());
		existingEmp.setBirthDate(emp.getBirthDate());

		List<Function> currentFunctions = new ArrayList<>(existingEmp.getFunctions());
		List<Customer> currentCustomers = new ArrayList<>(existingEmp.getCustomers());
		List<ApurationType> currentApurationTypes = new ArrayList<>(existingEmp.getApurationTypes());

		for (Function f : currentFunctions) {
			f.removeEmployee(existingEmp);
			functionRepository.save(f);
		}

		for (Customer c : currentCustomers) {
			c.removeEmployee(existingEmp);
			customerRepository.save(c);
		}

		for (ApurationType a : currentApurationTypes) {
			a.removeEmployee(existingEmp);
			apurationTypeRepository.save(a);
		}

		paymentMethod.addEmployee(existingEmp);
		paymentMethodRepository.save(paymentMethod);

		existingEmp.getFunctions().clear();
		existingEmp.getCustomers().clear();
		existingEmp.getApurationTypes().clear();

		for (Function f : functions) {
			existingEmp.addFunction(f);
			f.addEmployee(existingEmp);
		}

		for (Customer c : customers) {
			existingEmp.addCustomer(c);
			c.addEmployee(existingEmp);
		}

		for (ApurationType a : apurationTypes) {
			existingEmp.addApurationType(a);
			a.addEmployee(existingEmp);
		}

		existingEmp.setPaymentMethod(paymentMethod);

		employeeRepository.save(existingEmp);
	}

	public List<Employee> findAllByActiveTrue() {
		return employeeRepository.findAllByActiveTrue();
	}

	public List<Employee> findAllById(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		ids.removeIf(Objects::isNull);
		return employeeRepository.findAllById(ids);
	}

	public void save(Employee e) {
		employeeRepository.save(e);

	}

	public Page<Employee> findAll(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}

	public Page<Employee> filter(EmployeeFilterDTO filter, Pageable pageable) {
		Specification<Employee> spec = Specification.where(null);

		if (filter.customers() != null && !filter.customers().isEmpty()) {
			spec = spec.and(EmployeeSpecifications.customersIn(filter.customers()));
		}

		if (filter.functions() != null && !filter.functions().isEmpty()) {
			spec = spec.and(EmployeeSpecifications.functionsIn(filter.functions()));
		}

		return employeeRepository.findAll(spec, pageable);
	}

	public Employee findById(Long employeeId) {
		return employeeRepository.findById(employeeId)
				.orElseThrow(() -> new EntityNotFoundException("Premaido não encontrado"));
	}

	public void saveBySpreadsheet(MultipartFile file) throws IOException {
		System.out.println("---------INICIO DE SALVANDO PREMIADO POR PLANILHA-------------");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowWithData = getLastRowWithData(sheet);

			Iterator<Row> rowIterator = sheet.iterator();
			boolean isHeader = true;

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (isHeader) {
					isHeader = false;
					continue;
				}

				if (row.getRowNum() > lastRowWithData) {
					break;
				}

				if (isRowEmpty(row)) {
					System.out.println("Linha " + row.getRowNum() + " vazia - pulando");
					continue;
				}

				try {
					String cpf = getStringCellValue(row.getCell(0));
					System.out.println("CPF planilhado - " + cpf);
					
					String name = getStringCellValue(row.getCell(1));
					System.out.println("Nome planilhado - " + name);
					
					String email = getStringCellValue(row.getCell(2));
					System.out.println("Email planilhado - " + email);
					
					String phone = getStringCellValue(row.getCell(3));
					System.out.println("Phone planilhado - " + phone);

					LocalDate dateOfBirth = null;
					Cell dobCell = row.getCell(4);
					if (dobCell != null) {
						if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
							dateOfBirth = dobCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
						} else if (dobCell.getCellType() == CellType.STRING) {
							try {
								dateOfBirth = LocalDate.parse(dobCell.getStringCellValue().trim(), dateFormatter);
							} catch (DateTimeParseException e) {
								System.err.println("Erro na linha " + row.getRowNum() + ": Formato de data inválido - "
										+ dobCell.getStringCellValue());
								continue;
							}
						}
					}
					System.out.println("Data planilhado - " + dateOfBirth);
					
					EmployeeBasicInfosDTO empBasicDto = new EmployeeBasicInfosDTO(cpf, name, email, phone, dateOfBirth);
					Employee empBasic = basicEmpFromDto(empBasicDto);
					
					if(empBasic != null) {
						employeeRepository.save(empBasic);
					}

					Long func_id1 = parseLongFromCell(row.getCell(5));
					System.out.println("ID FUNÇÃO 1 - " + func_id1);
					Long func_id2 = parseLongFromCell(row.getCell(6));
					System.out.println("ID FUNÇÃO 2 - " + func_id2);
					Long cust_id1 = parseLongFromCell(row.getCell(7));
					System.out.println("ID CLIENTE 1 - " + cust_id1);
					Long cust_id2 = parseLongFromCell(row.getCell(8));
					System.out.println("ID CLIENTE 2 - " + cust_id2);
					Long cust_id3 = parseLongFromCell(row.getCell(9));
					System.out.println("ID CLIENTE 3 - " + cust_id3);
					Long cust_id4 = parseLongFromCell(row.getCell(10));
					System.out.println("ID CLIENTE 4 - " + cust_id4);
					Long cust_id5 = parseLongFromCell(row.getCell(11));
					System.out.println("ID CLIENTE 5 - " + cust_id5);
					Long cust_id6 = parseLongFromCell(row.getCell(12));
					System.out.println("ID CLIENTE 6 - " + cust_id6);
					Long cust_id7 = parseLongFromCell(row.getCell(13));
					System.out.println("ID CLIENTE 7 - " + cust_id7);
					Long apur_id1 = parseLongFromCell(row.getCell(14));
					System.out.println("ID APURAÇÃO 1 - " + apur_id1);
					Long apur_id2 = parseLongFromCell(row.getCell(15));
					System.out.println("ID APURAÇÃO 2 - " + apur_id2);
					Long pay_id = parseLongFromCell(row.getCell(16));
					System.out.println("ID METODO  - " + pay_id);

					if (cpf == null || cpf.isBlank()) {
						System.err.println("CPF inválido na linha " + row.getRowNum());
						continue;
					}
					
					EmployeeRelationshipDTO empRelDto = new EmployeeRelationshipDTO(func_id1, func_id2, 
							cust_id1, cust_id2, cust_id3, cust_id4, cust_id5, cust_id6, cust_id7,
							apur_id1, apur_id2, 
							pay_id);
					
					if(empBasic != null) {
						relEmpFromDto(empBasic, empRelDto);
					}
				
				} catch (Exception e) {
					System.err.println("Erro crítico na linha " + row.getRowNum() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		System.out.println("---------FIM DE SALVANDO PREMIADO POR PLANILHA-------------");
	}

	private void relEmpFromDto(Employee emp, EmployeeRelationshipDTO empRelDto) {
		System.out.println("-------------Inicio dos relacionamentos------------");
		if (emp != null && empRelDto != null) {
			emp.setActive(true);

			if (empRelDto.func_id1() != null) {
				Function function1 = functionRepository.findById(empRelDto.func_id1())
						.orElseThrow(() -> new EntityNotFoundException("Função não encontrada"));
				emp.addFunction(function1);
				System.out.println("funcao1 encontrada: " + function1.getName());
			}
			if (empRelDto.func_id2() != null) {
				Function function2 = functionRepository.findById(empRelDto.func_id2())
						.orElseThrow(() -> new EntityNotFoundException("Função não encontrada"));
				emp.addFunction(function2);
				System.out.println("funcao2 encontrada: " + function2.getName());
			}

			if (empRelDto.cust_id1() != null) {
				Customer customer1 = customerRepository.findById(empRelDto.cust_id1())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer1);
				customerRepository.save(customer1);
				System.out.println("Cliente 1 encontrada: " + customer1.getFantasyName());
			}
			if (empRelDto.cust_id2() != null) {
				Customer customer2 = customerRepository.findById(empRelDto.cust_id2())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer2);
				customerRepository.save(customer2);
				System.out.println("Cliente 2 encontrada: " + customer2.getFantasyName());
			}
			if (empRelDto.cust_id3() != null) {
				Customer customer3 = customerRepository.findById(empRelDto.cust_id3())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer3);
				customerRepository.save(customer3);
				System.out.println("Cliente 3 encontrada: " + customer3.getFantasyName());
			}
			if (empRelDto.cust_id4() != null) {
				Customer customer4 = customerRepository.findById(empRelDto.cust_id4())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer4);
				customerRepository.save(customer4);
				System.out.println("Cliente 4 encontrada: " + customer4.getFantasyName());
			}
			if (empRelDto.cust_id5() != null) {
				Customer customer5 = customerRepository.findById(empRelDto.cust_id5())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer5);
				customerRepository.save(customer5);
				System.out.println("Cliente 5 encontrada: " + customer5.getFantasyName());
			}
			if (empRelDto.cust_id6() != null) {
				Customer customer6 = customerRepository.findById(empRelDto.cust_id6())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer6);
				customerRepository.save(customer6);
				System.out.println("Cliente 6 encontrada: " + customer6.getFantasyName());
			}
			if (empRelDto.cust_id7() != null) {
				Customer customer7 = customerRepository.findById(empRelDto.cust_id7())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer7);
				customerRepository.save(customer7);
				System.out.println("Cliente 7 encontrada: " + customer7.getFantasyName());
			}

			if (empRelDto.apur_id1() != null) {
				ApurationType apurationType1 = apurationTypeRepository.findById(empRelDto.apur_id1())
						.orElseThrow(() -> new EntityNotFoundException("Apuração não encontrada"));
				emp.addApurationType(apurationType1);
				System.out.println("Apuração 1 encontrada: " + apurationType1.getName());
			}
			if (empRelDto.apur_id2() != null) {
				ApurationType apurationType2 = apurationTypeRepository.findById(empRelDto.apur_id2())
						.orElseThrow(() -> new EntityNotFoundException("Apuração não encontrada"));
				emp.addApurationType(apurationType2);
				System.out.println("Apuração 2 encontrada: " + apurationType2.getName());
			}

			if (empRelDto.pay_id() != null) {
				PaymentMethod payment = paymentMethodRepository.findById(empRelDto.pay_id())
						.orElseThrow(() -> new EntityNotFoundException("Método não encontrado"));
				emp.setPaymentMethod(payment);
				System.out.println("Metodo 1 encontrada: " + payment.getName());
			}
			System.out.println("-------------Fim dos relacionamentos------------");
			employeeRepository.save(emp);
		}

	}

	private Employee basicEmpFromDto(EmployeeBasicInfosDTO empBasicDto) {
		System.out.println("-------------Inicio DO METODO DO FROM DTO------------");
		Employee emp = new Employee();
		Employee empSearched = findByCpf(empBasicDto.cpf());
		if (empSearched == null) {
			emp.setCpf(empBasicDto.cpf());
			System.out.println("cpf DTO: " + emp.getCpf());
			emp.setName(empBasicDto.name());
			System.out.println("name DTO: " + emp.getName());
			emp.setEmail(empBasicDto.email());
			System.out.println("email DTO: " + emp.getEmail());
			emp.setPhone(empBasicDto.phone());
			System.out.println("phone DTO: " + emp.getPhone());
			emp.setBirthDate(empBasicDto.dateOfBirth());
			System.out.println("date DTO: " + emp.getBirthDate());
			emp.setActive(true);
			System.out.println("-------------FIM DO METODO DO FROM DTO------------");
			return emp;
		}else {
			System.out.println("CPF '" + empBasicDto.cpf()+ "' já encontrado");
		}
		
		System.out.println("-------------FIM DO METODO DO FROM DTO------------");
		return null;
	}

	private String getStringCellValue(Cell cell) {
		if (cell == null)
			return null;

		DataFormatter formatter = new DataFormatter();
		String value = formatter.formatCellValue(cell).trim();
		return value.isEmpty() ? null : value;
	}

	private Long parseLongFromCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == CellType.NUMERIC) {
			return (long) cell.getNumericCellValue();
		} else if (cell.getCellType() == CellType.STRING) {
			try {
				return Long.valueOf(cell.getStringCellValue());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public Employee findByCpf(String cpf) {
		return employeeRepository.findByCpf(cpf);
	}

	private int getLastRowWithData(Sheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		System.out.println("lastrownum - " + lastRowNum);
		for (int i = lastRowNum; i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row != null && !isRowEmpty(row)) {
				return i;
			}
		}
		return -1;
	}

	private boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}

		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (!isCellEmpty(cell)) {
				return false;
			}
		}
		return true;
	}

	private boolean isCellEmpty(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return true;
		}

		if (cell.getCellType() == CellType.STRING) {
			String value = cell.getStringCellValue().trim();
			return value.isEmpty();
		}

		return false;
	}

}
