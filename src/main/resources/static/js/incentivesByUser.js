async function findUserDocumentNumbers(button) {
	const userId = button.getAttribute('data-user-id');

	const url = `users/incentives/${userId}`;

	try {
		const response = await fetch(url);
		if (!response.ok) {
			throw new Error("Não foi possível encontrar os incentivos desse usuario");
		}

		const data = await response.text();

		const incentivesByUserArea = document.getElementById('incentivesByUserArea');
		incentivesByUserArea.innerHTML = data;

		const modal = new bootstrap.Modal(document.getElementById('showIncentivesModal'));
		modal.show();
		
		const modalUserDetails = bootstrap.Modal.getInstance(document.getElementById(`detailsUserModal${userId}`));
		modalUserDetails.hide();
	}
	catch (error) {
		console.error("Erro:", error);
		alert("Erro ao carregar os incentivos. Tente novamente.");
	}

}

async function loadUserIncentivesDocumentDetails(button) {
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
		downloadBtn.addEventListener("click", function() {
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
		table.className = 'table table-bordered mt-2 table-striped';
		table.style = 'width: 60%';
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

function formatDate(dateString) {
	if (!dateString) return 'N/A';

	const date = new Date(dateString);
	if (isNaN(date)) return 'Data Inválida';

	const month = (date.getMonth() + 1).toString().padStart(2, '0');
	const year = date.getFullYear();
	return `${month}-${year}`;
}
	
	
	
	
	