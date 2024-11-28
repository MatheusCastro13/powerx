function removeIncentiveValue(productId, incentiveId) {
    fetch(`/products/incentiveValue/delete/${productId}/${incentiveId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        }
    })
    .then(response => {
        if (response.ok) {
            const row = document.querySelector(`#row-${incentiveId}`);
            row.remove();
        } else {
            console.error('Erro ao remover o valor do incentivo.');
        }
    })
    .catch(error => console.error('Erro:', error));
}

function addIncentiveRow(productId) {
    // Identificar o tbody da tabela do produto específico
    const tableBody = document.querySelector(`#tableBody-${productId}`);

    // Criar uma nova linha <tr>
    const newRow = document.createElement('tr');

    // Gerar um ID único temporário para a nova linha
    const tempId = `temp-${Date.now()}`;

    // Definir o ID para a nova linha
    newRow.id = `row-${tempId}`;

    // Clonar as opções dos templates ocultos
    const customerSelectTemplate = document.querySelector("#customerOptionsTemplate");
    const functionSelectTemplate = document.querySelector("#functionOptionsTemplate");

    const customerSelect = customerSelectTemplate.cloneNode(true);
    customerSelect.id = "";  // Remover ID para evitar duplicação
    customerSelect.name = "customer";

    const functionSelect = functionSelectTemplate.cloneNode(true);
    functionSelect.id = "";  // Remover ID para evitar duplicação
    functionSelect.name = "function";

    // Adicionar o conteúdo HTML para os inputs e selects na nova linha
    newRow.innerHTML = `
        <td>
            <div class="col">
                ${customerSelect.outerHTML}
            </div>
        </td>
        <td>
            <div class="col">
                ${functionSelect.outerHTML}
            </div>
        </td>
        <td>
            <div class="col">
                <input type="number" class="form-control" name="ccValue" placeholder="Conta Corrente" required>
            </div>
        </td>
        <td>
            <div class="col">
                <input type="number" class="form-control" name="nfsValue" placeholder="NF Serviço" required>
            </div>
        </td>
        <td>
            <div class="col">
                <button type="button" class="btn btn-danger" onclick="removeRow('${tempId}')">
                    Remover
                </button>
            </div>
        </td>
    `;

    // Adicionar a nova linha ao tbody
    tableBody.appendChild(newRow);
}

function removeRow(rowId) {
    const row = document.querySelector(`#row-${rowId}`);
    if (row) {
        row.remove();
    } else {
        console.error(`Linha com ID #row-${rowId} não encontrada.`);
    }
}

function removeRow(rowId) {
    const row = document.querySelector(`#row-${rowId}`);
    if (row) {
        row.remove();
    } else {
        console.error(`Linha com ID #row-${rowId} não encontrada.`);
    }
}




