$(document).ready(function () {
    $('#newEmployeeModal').on('shown.bs.modal', function () {
        $('#multiSelectFunctionsNew, #multiSelectCustomersNew, #multiSelectApurationTypesNew').select2({
            placeholder: "Selecione uma opção",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newEmployeeModal')
        });
    });

    $('div[id^="editEmployeeModal"]').on('shown.bs.modal', function () {
        const modalId = $(this).attr('id');
        $(`#${modalId} select[id^="multiSelectFunctionsEdit"], 
           #${modalId} select[id^="multiSelectCustomersEdit"],
           #${modalId} select[id^="multiSelectApurationTypesEdit"]`).select2({
            placeholder: "Selecione uma opção",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $(`#${modalId}`)
        });
    });
});
