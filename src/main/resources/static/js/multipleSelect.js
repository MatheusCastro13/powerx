$(document).ready(function () {
    $('#multiSelect').select2({
        placeholder: "Selecione opções",
        allowClear: true,
        closeOnSelect: false,
        dropdownParent: $('#newCustomerModal')
    });
});
