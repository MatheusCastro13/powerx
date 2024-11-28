function clearUserFilters() {
        document.getElementById('filterPosition').selectedIndex = -1;
        document.getElementById('filterState').selectedIndex = -1;

        fetch('/users/clearFilters', {
            method: 'GET'
        })
        .then(response => response.text())
        .then(html => {
            document.getElementById('tableBody').innerHTML = html;

            const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
            filterModal.hide();
        })
        .catch(error => {
            console.error('Erro ao limpar filtros:', error);
        });
    }
