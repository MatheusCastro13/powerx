document.querySelectorAll('.nav-link-custom').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault(); 
        
        const url = this.getAttribute('href'); 

        fetch(url)
            .then(response => response.text())
            .then(html => {
                const optionSelectedDiv = document.getElementById('option-selected');
                const alert = document.getElementById('alert');
                
                if(alert){
					alert.style.display = 'none';
				}
				
                optionSelectedDiv.innerHTML = '';

                optionSelectedDiv.innerHTML = html;
            })
            .catch(error => {
                console.error('Erro ao carregar a página:', error);
            });
    });
});

