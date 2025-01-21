$(document).ready(function () {
    $('#newCustomerModal').on('shown.bs.modal', function () {
        $('#multiSelectNew').select2({
            placeholder: "Selecione os Premiados",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newCustomerModal')
        });
    });

    $(document).on('shown.bs.modal', '.modal[id^="editCustomerModal"]', function () {
        const modal = $(this);
        const select = modal.find('#multiSelectEdit');

        select.select2({
            placeholder: "Selecione os Premiados",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: modal 
        });
    });
});
