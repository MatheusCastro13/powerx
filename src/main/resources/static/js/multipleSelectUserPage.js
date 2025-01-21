$(document).ready(function () {
    $('#newUserModal').on('shown.bs.modal', function () {
        $('#multiSelectNew').select2({
            placeholder: "Selecione os Clientes",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newUserModal')
        });
    });

    $(document).on('shown.bs.modal', '.modal[id^="editUserModal"]', function () {
        const modal = $(this);
        const select = modal.find('#multiSelectEdit');

        select.select2({
            placeholder: "Selecione os Clientes",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: modal 
        });
    });
});
