function applyCustomerFilters() {
    const filterForm = document.getElementById('filterForm');
    
    const selectedUsers = Array.from(document.getElementById('filterUser').selectedOptions).map(option => option.value);
    const selectedGroups = Array.from(document.getElementById('filterGroup').selectedOptions).map(option => option.value);
    
    const filters = {
        users: selectedUsers,
        groups: selectedGroups
    };

    fetch('/customers/filter', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify(filters)
    })
    .then(response => response.text())
    .then(html => {
        document.getElementById('tableBody').innerHTML = html;
        
        const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
        filterModal.hide();
    })
    .catch(error => console.error('Erro ao aplicar filtros:', error));
}
