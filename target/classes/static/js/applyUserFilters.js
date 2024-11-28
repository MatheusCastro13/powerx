function applyUserFilters() {
    const filterForm = document.getElementById('filterForm');
    
    const selectedPositions = Array.from(document.getElementById('filterPosition').selectedOptions).map(option => option.value);
    const selectedStates = Array.from(document.getElementById('filterState').selectedOptions).map(option => option.value);
    
    const filters = {
        positions: selectedPositions,
        states: selectedStates
    };

    fetch('/users/filter', {
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
