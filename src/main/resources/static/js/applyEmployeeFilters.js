function applyEmployeeFilters() {
    const filterForm = document.getElementById('filterForm');
    
    const selectedCustomers = Array.from(document.getElementById('filterCustomer').selectedOptions).map(option => option.value);
    const selectedFunctions = Array.from(document.getElementById('filterFunction').selectedOptions).map(option => option.value);
    
    const filters = {
        customers: selectedCustomers,
        functions: selectedFunctions
    };

    fetch('/employees/filter', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify(filters)
    })
    .then(response => response.text())
    .then(html => {
        const divToReplace = document.getElementById('employee-table');
		divToReplace.innerHTML = "";
		divToReplace.innerHTML = html;
        
        const filterModal = bootstrap.Modal.getInstance(document.getElementById('filterModal'));
        filterModal.hide();
    })
    .catch(error => console.error('Erro ao aplicar filtros:', error));
}
