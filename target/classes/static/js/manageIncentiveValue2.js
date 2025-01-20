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
    const tableBody = document.querySelector(`#tableBody-${productId}`);

    const newRow = document.createElement('tr');

    const tempId = `temp-${Date.now()}`;

    newRow.id = `row-${tempId}`;

    const customerSelectTemplate = document.querySelector("#customerOptionsTemplate");
    const functionSelectTemplate = document.querySelector("#functionOptionsTemplate");

    const customerSelect = customerSelectTemplate.cloneNode(true);
    customerSelect.id = "";
    customerSelect.name = "customer";

    const functionSelect = functionSelectTemplate.cloneNode(true);
    functionSelect.id = "";
    functionSelect.name = "function";

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
                <input type="number" class="form-control" name="ccValue" placeholder="Conta Corrente" step="any" required>
            </div>
        </td>
        <td>
            <div class="col">
                <input type="number" class="form-control" name="nfsValue" placeholder="NF Serviço" step="any" required>
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





