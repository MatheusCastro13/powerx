function loadEmployeeData(title, customerId, dynamicSectionId) {
        const url = `/customers/${customerId}/employees`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ao buscar dados do cliente: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                const dynamicSections = document.getElementById(dynamicSectionId);
                dynamicSections.innerHTML = "";

                let employees, products;

                if (title === "Consultores") {
                    employees = data.consultores;
                    products = data.produtos;
                } else if (title === "Mecânicos") {
                    employees = data.mecanicos;
                    products = data.produtos;
                }

                if (employees && employees.length > 0) {
                    createApurationSection(title, employees, products, dynamicSectionId, customerId);
                } else {
                    dynamicSections.innerHTML = `Sem ${title.toLowerCase()} nesta loja, clique em avançar.`;
                }
            })
            .catch(error => {
                console.error("Erro ao carregar funcionários:", error);
            });
    }



	function createApurationSection(title, employees, products, dynamicSectionId, customerId) {
	    const section = document.getElementById(dynamicSectionId);
	    if (!section) {
	        console.error(`Erro: Seção dinâmica com ID '${dynamicSectionId}' não encontrada.`);
	        return;
	    }

	    section.innerHTML = "";

	    const sectionTitle = document.createElement("h5");
	    sectionTitle.textContent = `Apuração ${title}`;
	    section.appendChild(sectionTitle);

	    const employeeSelect = document.createElement("select");
	    employeeSelect.classList.add("form-select", "mb-3");
	    employeeSelect.setAttribute("id", `${dynamicSectionId}EmployeeSelect`);
		employeeSelect.style.width = "65%";
	    employeeSelect.innerHTML = `<option value="default">Selecione um ${title.toLowerCase()}</option>`;
	    employees.forEach(employee => {
	        const option = document.createElement("option");
	        option.value = employee.id;
	        option.textContent = employee.name;
	        employeeSelect.appendChild(option);
	    });
	    section.appendChild(employeeSelect);

	    const productTable = document.createElement("table");
	    productTable.classList.add("table", "table-bordered", "mb-3", "table-sm", "table-striped");
	    productTable.setAttribute("id", `${dynamicSectionId}ProductsTable`);
	    productTable.style.width = "65%";
	    productTable.style.fontSize = "0.875rem";

	    productTable.innerHTML = `
	        <thead>
	            <tr>
	                <th>CÓD.</th>
	                <th>Nome</th>
	                <th>Quantidade</th>
	            </tr>
	        </thead>
	        <tbody>
	            ${products
	                .map(
	                    product => `
	                    <tr data-product-id="${product.id}">
	                        <td class="small">${product.productCode}</td>
	                        <td class="small">${product.productName}</td>
	                        <td class="small"><input type="number" class="form-control" name="${title.toLowerCase()}Quantities[${product.id}]"></td>
	                    </tr>`
	                )
	                .join("")}
	        </tbody>
	    `;
	    section.appendChild(productTable);

	    const saveNewButton = document.createElement("button");
	    saveNewButton.type = "button";
	    saveNewButton.classList.add("btn", "btn-primary", "mt-2");
	    saveNewButton.textContent = "Adicionar Venda";
	    saveNewButton.addEventListener("click", function () {
	        saveSale(title, `${dynamicSectionId}EmployeeSelect`, employees, `${dynamicSectionId}ProductsTable`, products);
	    });
	    section.appendChild(saveNewButton);
	}

    
    let salesData = [];

	function saveSale(title, employeeSelectId, employees, productsTableId, products) {
	    const customerSelect = document.getElementById("customerSelect");
	    const selectedCustomerId = customerSelect.value;
	    const employeeSelect = document.getElementById(employeeSelectId);
	    const selectedEmployeeId = employeeSelect.value;
	    const selectedEmployee = employees.find(emp => emp.id == selectedEmployeeId);

	    if (!selectedEmployeeId || selectedEmployeeId === 'default') {
	        alert("Selecione um funcionário válido.");
	        return;
	    }

	    const productsTable = document.getElementById(productsTableId);
	    const rows = productsTable.querySelectorAll("tbody tr");

	    let totalProductsSold = 0;
	    let saleDetails = {
	        customerId: selectedCustomerId,
	        employeeId: selectedEmployee.id,
	        employee: selectedEmployee.name,
			function: title,
	        products: []
	    };

	    rows.forEach(row => {
	        const productId = row.getAttribute('data-product-id');
	        const quantityInput = row.querySelector('input');
	        const quantity = parseInt(quantityInput.value) || 0;

	        if (quantity >= 0) {
	            saleDetails.products.push({
	                productId: productId,
	                productName: products.find(p => p.id == productId).productName,
					productCode: products.find(p => p.id == productId).productCode,
	                quantity: quantity
	            });
	            totalProductsSold += quantity;
	        }
	    });

	    saleDetails.totalProductsSold = totalProductsSold;

	    if (saleDetails.products.length > 0) {
	        salesData.push(saleDetails);
	        alert("Venda salva com sucesso!");

	        const optionToRemove = employeeSelect.querySelector(`option[value="${selectedEmployeeId}"]`);
	        if (optionToRemove) {
	            optionToRemove.remove();
	        }

	        employeeSelect.value = "default";

	        rows.forEach(row => {
	            const quantityInput = row.querySelector('input');
	            if (quantityInput) {
	                quantityInput.value = "";
	            }
	        });
	    } else {
	        alert("Preencha ao menos um produto com uma quantidade válida.");
	    }

	    document.getElementById("finalizeMechanicButton").addEventListener("click", () => {
	        populateResumePane();
	    });

	    document.getElementById("saveAllSalesButton").addEventListener("click", sendSalesToController);
	}


    
    function populateResumePane() {
        const tableBody = document.querySelector("#modalSalesTable tbody");
        tableBody.innerHTML = "";

        salesData.forEach((sale, index) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${sale.employee}</td>
                <td>${sale.totalProductsSold}</td>
                <td>
                    <button class="btn btn-warning btn-sm" onclick="editSale(${index})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="removeSale('${sale.employee}')">Remover</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    }
    
    function editSale(index) {
        const sale = salesData[index];

        const tableBody = document.querySelector("#modalSalesResumeTable tbody");
        tableBody.innerHTML = "";

        sale.products.forEach(product => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${product.productId}</td>
                <td>${product.productCode}</td>
                <td><input type="number" class="form-control" value="${product.quantity}" min="0" data-product-id="${product.productId}"></td>
            `;
            tableBody.appendChild(row);
        });

        document.getElementById("confirmSaleButton").setAttribute("data-sale-index", index);

        const saleModal = new bootstrap.Modal(document.getElementById('sale'));
        saleModal.show();

        document.getElementById("confirmSaleButton").addEventListener("click", () => {
            const saleIndex = document.getElementById("confirmSaleButton").getAttribute("data-sale-index");
            const tableBody = document.querySelector("#modalSalesResumeTable tbody");
            const rows = tableBody.querySelectorAll("tr");

            let totalProductsSold = 0;
            const updatedProducts = [];

            rows.forEach(row => {
                const productId = row.querySelector("input").getAttribute("data-product-id");
                const quantity = parseInt(row.querySelector("input").value) || 0;

                totalProductsSold += quantity;

                updatedProducts.push({
                    productId: productId,
                    productName: row.cells[1].textContent,
                    quantity: quantity
                });
            });

            salesData[saleIndex].products = updatedProducts;
            salesData[saleIndex].totalProductsSold = totalProductsSold;

            populateResumePane();

            const saleModal = bootstrap.Modal.getInstance(document.getElementById('sale'));
            saleModal.hide();
        });
    }



    function removeSale(employeeName) {
        salesData = salesData.filter(sale => sale.employee !== employeeName);
        populateResumePane();
    }
    
    function sendSalesToController() {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        const url = `/sale/calcule`;

        const headers = {
            'Content-Type': 'application/json',
        };

        headers[csrfHeader] = csrfToken;

        fetch(url, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(salesData),
        })
        .then(response => {
			if (!response.ok) {
			        return response.text().then(text => {
			            alert(text);
						window.location.href = "/incentives";
			            throw new Error(`Erro ao enviar vendas: ${text}`);
			        });
			    }
            return response.json();
        })
        .then(responseData => {
            console.log("Resposta Bruta:", responseData);

            let dtoList;
            if (Array.isArray(responseData) && typeof responseData[0] === 'string') {
                dtoList = JSON.parse(responseData[0]);
            } else {
                dtoList = responseData;
            }

            console.log("Dados Processados:", dtoList);

            populateIncentivesTable(dtoList);
        })
        .catch(error => {
            console.error("Erro ao enviar vendas:", error);
        });
		
        const incentivesCalculatedModal = new bootstrap.Modal(document.getElementById('incentivesCalculatedModal'));
        incentivesCalculatedModal.show();
		
		const resumeModal = bootstrap.Modal.getInstance(document.getElementById('resumePane'));
		resumeModal.hide();
    }

    function populateIncentivesTable(dtoList) {
        const tableBody = document.getElementById('incentivesCalculatedTableBody');
        tableBody.innerHTML = '';

		const ccList = dtoList.filter(i => i.apurationType?.trim().toLowerCase() === "conta corrente");
		
		const nfsList = dtoList.filter(i => i.apurationType?.trim().toLowerCase() === "nf serviço");
        
        ccList.forEach(incentive => {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td class="small">${incentive.referenceDate ? formatDate(incentive.referenceDate) : ''}</td>
                <td class="small">${incentive.state || ''}</td>
                <td class="small">${incentive.apurationType || ''}</td>
                <td class="small">${incentive.paymentMethod || ''}</td>
                <td class="small">${incentive.cpf || ''}</td>
                <td class="small">${incentive.employeeName || ''}</td>
                <td class="small">${Number(incentive.incentiveValue).toFixed(2)}</td> <!-- Formata com 2 casas decimais -->
                <td class="small">${incentive.functionName || ''}</td>
                <td class="small">${incentive.customerName || ''}</td>
                <td class="small">${incentive.customerCnpj || ''}</td>
            `;

            tableBody.appendChild(row);
        });
		
		nfsList.forEach(incentive => {
		            const row = document.createElement('tr');

		            row.innerHTML = `
		                <td class="small">${incentive.referenceDate ? formatDate(incentive.referenceDate) : ''}</td>
		                <td class="small">${incentive.state || ''}</td>
		                <td class="small">${incentive.apurationType || ''}</td>
		                <td class="small">${incentive.paymentMethod || ''}</td>
		                <td class="small">${incentive.cpf || ''}</td>
		                <td class="small">${incentive.employeeName || ''}</td>
		                <td class="small">${Number(incentive.incentiveValue).toFixed(2)}</td> <!-- Formata com 2 casas decimais -->
		                <td class="small">${incentive.functionName || ''}</td>
		                <td class="small">${incentive.customerName || ''}</td>
		                <td class="small">${incentive.customerCnpj || ''}</td>
		            `;

		            tableBody.appendChild(row);
		        });
    }


    function formatDate(dateString) {
        if (!dateString) return 'N/A';

        const date = new Date(dateString);
        if (isNaN(date)) return 'Data Inválida';

        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();
        return `${month}-${year}`;
    }

    async function loadDocumentDetails(button) {
        const documentNumber = button.getAttribute('data-document-number');

        const url = `/incentives/${documentNumber}`;

        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Erro ao buscar detalhes: ${response.statusText}`);
            }

            const data = await response.json();

            populateModal(data.sales, data.incentives, data.fantasyName, data.cnpj, data.method, data.date, data.state);

            const modal = new bootstrap.Modal(document.getElementById('documentModal'));
            modal.show();
			
			const downloadBtn = document.getElementById('downloadReport');
					        downloadBtn.addEventListener("click", function(){
								downloadReport(documentNumber);
					        });
							
        } catch (error) {
            console.error("Erro:", error);
            alert("Erro ao carregar os detalhes. Tente novamente.");
        }
    }

	function populateModal(sales, incentives, fantasyName, cnpj, method, date, state) {
	    const headerInfos = document.getElementById('header-infos');
	    const incentivesTableBody = document.getElementById('incentivesTableBody');
	    const salesContainer = document.getElementById('salesContainer');

	    headerInfos.innerHTML = '';
	    incentivesTableBody.innerHTML = '';
	    salesContainer.innerHTML = '';

	    headerInfos.innerHTML = `
	        <div class="row">
				<div class="col-md-3">
					<div>
						<strong>Nome Fantasia:</strong> ${fantasyName || ''}
					</div>
				 	<div>
			            <strong>CNPJ:</strong> ${cnpj || ''}
			    	</div>
					<div>
						<strong>Região:</strong> ${state || ''}
					</div>	
				</div>
				<div class="col-md-3">
			        <div>
			            <strong>Método de Pagamento:</strong> ${method || ''}
			        </div>
			        <div>
			            <strong>Data:</strong> ${date ? formatDate(date) : ''}
			        </div>
				</div>	
			</div>
	    `;

	    const salesGroupedByEmployee = sales.reduce((acc, sale) => {
	        const employeeName = sale.employeeName;
	        if (!acc[employeeName]) acc[employeeName] = [];
	        acc[employeeName].push(sale);
	        return acc;
	    }, {});

	    for (const [employeeName, employeeSales] of Object.entries(salesGroupedByEmployee)) {
	        const title = document.createElement('h5');
	        title.textContent = `Funcionário: ${employeeName}`;
	        title.className = 'mt-4';
	        salesContainer.appendChild(title);

	        const table = document.createElement('table');
			table.style = "width: 60%";
	        table.className = 'table table-bordered table-striped table-sm mt-2';
	        table.innerHTML = `
	            <thead>
	                <tr>
	                    <th>Código do Produto</th>
	                    <th>Descrição do Produto</th>
	                    <th>Quantidade</th>
	                </tr>
	            </thead>
	            <tbody>
	                ${employeeSales.map(sale => `
	                    <tr>
	                        <td class="small">${sale.productCode || ''}</td>
	                        <td class="small">${sale.productName || ''}</td>
	                        <td class="small">${sale.quantity || ''}</td>
	                    </tr>
	                `).join('')}
	            </tbody>
	        `;
	        salesContainer.appendChild(table);
	    }

		const ccIncentives = incentives.filter(i => i.apurationType?.trim().toLowerCase() === "conta corrente");
		const nfsIncentives = incentives.filter(i => i.apurationType?.trim().toLowerCase() === "nf serviço");
		
	    ccIncentives.forEach(incentive => {
	        const row = `
	            <tr>
	                <td class="small">${incentive.apurationType || ''}</td>
	                <td class="small">${incentive.cpf || ''}</td>
	                <td class="small">${incentive.employeeName || ''}</td>
	                <td class="small">${Number(incentive.incentiveValue).toFixed(2)}</td>
	                <td class="small">${incentive.functionName || ''}</td>
	            </tr>`;
	        incentivesTableBody.innerHTML += row;
	    });
		
		nfsIncentives.forEach(incentive => {
			        const row = `
			            <tr>
			                <td class="small">${incentive.apurationType || ''}</td>
			                <td class="small">${incentive.cpf || ''}</td>
			                <td class="small">${incentive.employeeName || ''}</td>
			                <td class="small">${Number(incentive.incentiveValue).toFixed(2)}</td>
			                <td class="small">${incentive.functionName || ''}</td>
			            </tr>`;
			        incentivesTableBody.innerHTML += row;
			    });
	}



    function downloadReport(documentNumber) {
        const url = `/reports/sales/${encodeURIComponent(documentNumber)}`;

        fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/pdf'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro ao gerar relatório: ${response.statusText}`);
            }

            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = `relatorio_${documentNumber}.pdf`;
            document.body.appendChild(a);
            a.click();

            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(error => {
            console.error('Erro ao baixar o relatório:', error);
            alert('Erro ao baixar o relatório. Tente novamente!');
        });
    }
	
	function downloadLastReport() {
	        const url = `/reports/last`;

	        fetch(url, {
	            method: 'GET',
	            headers: {
	                'Accept': 'application/pdf'
	            }
	        })
	        .then(response => {
	            if (!response.ok) {
	                throw new Error(`Erro ao gerar relatório: ${response.statusText}`);
	            }

	            return response.blob();
	        })
	        .then(blob => {
	            const url = window.URL.createObjectURL(blob);

	            const a = document.createElement('a');
	            a.href = url;
	            a.download = `relatorio.pdf`;
	            document.body.appendChild(a);
	            a.click();

	            a.remove();
	            window.URL.revokeObjectURL(url);
				window.location.href = "/incentives";


	        })
	        .catch(error => {
	            console.error('Erro ao baixar o relatório:', error);
	            alert('Erro ao baixar o relatório. Tente novamente!');
	        });
	    }