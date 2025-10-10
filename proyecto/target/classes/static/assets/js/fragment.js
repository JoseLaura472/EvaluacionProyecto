function _withSession(ok) {
    $.ajax({
        url: "/adm/cargar-datos",
        method: "GET",
        success: function () { if (typeof ok === 'function') ok(); },
        error: function (xhr) {
            if (xhr.status === 401) {
                Swal.fire({
                    title: 'Sesión expirada',
                    text: 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.',
                    icon: 'warning',
                    confirmButtonText: 'Ir al login'
                }).then((r) => { if (r.isConfirmed) window.location.href = '/form-login'; });
            }
        }
    });
}

// --- Cargar formulario (Registrar)
function cargarFormularioAlert(urlFormulario, idContenedorModal, idFormulario, onLoaded) {
    _withSession(function () {
        $.ajax({
            type: 'POST',
            url: urlFormulario,
            success: function (html) {
                $(idContenedorModal).html(html);

                // Inicializa select2 apuntando al formulario inyectado
                try {
                    $('.select2').select2({ dropdownParent: $(idFormulario) });
                } catch (e) { /* select2 opcional */ }

                // Permite que cada formulario se auto-inicialice si quiere
                if (typeof onLoaded === 'function') onLoaded();

            },
            error: function (xhr) {
                console.error('Error al cargar formulario:', xhr);
                Swal.fire('Error', 'No se pudo cargar el formulario.', 'error');
            }
        });
    });
}

// --- Cargar formulario (Editar)
function cargarFormularioEditAlert(id, urlFormularioBase, idContenedorModal, idFormulario, onLoaded) {
    _withSession(function () {
        $.ajax({
            type: 'POST',
            url: urlFormularioBase + "/" + id,
            success: function (html) {
                $(idContenedorModal).html(html);

                try {
                    $('.select2').select2({ dropdownParent: $(idFormulario) });
                } catch (e) { }

                if (typeof onLoaded === 'function') onLoaded();
            },
            error: function (xhr) {
                console.error('Error al cargar formulario (edit):', xhr);
                Swal.fire('Error', 'No se pudo cargar el formulario para edición.', 'error');
            }
        });
    });
}

// --- Eliminar con confirmación
function eliminarRegistroAlert(nombre, id, urlEliminar, recargarTablaFn, metodoHttp = 'POST') {
    _withSession(function () {
        Swal.fire({
            title: 'Eliminar Registro',
            text: '¿Estás seguro de eliminar a ' + nombre + '?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar',
            reverseButtons: true
        }).then((result) => {
            if (!result.isConfirmed) return;

            $.ajax({
                url: urlEliminar + "/" + id,
                type: metodoHttp,
                success: function (response) {
                    if (typeof recargarTablaFn === 'function') recargarTablaFn();
                    Swal.fire('Eliminado', (response || 'Registro eliminado.'), 'success');
                },
                error: function () {
                    Swal.fire('Error', 'Hubo un problema al eliminar el registro.', 'error');
                }
            });
        });
    });
}

/**
 * Maneja el envío de un formulario inyectado por AJAX.
 * @param {string} selectorFormulario  - Ej: '#formularioTipoParticipante'
 * @param {object} opts                - { modalSelector, recargarTablaFn, onSuccess, onError }
 */
function manejarEnvioFormulario(selectorFormulario, opts = {}) {
    const modalSelector = opts.modalSelector || null;
    const recargarTablaFn = typeof opts.recargarTablaFn === 'function' ? opts.recargarTablaFn : null;
    const onSuccess = typeof opts.onSuccess === 'function' ? opts.onSuccess : null;
    const onError = typeof opts.onError === 'function' ? opts.onError : null;

    // Binding delegado para formularios inyectados
    $(document).off('submit', selectorFormulario).on('submit', selectorFormulario, function (event) {
        event.preventDefault();

        const formEl = this;
        if (formEl.checkValidity() === false) {
            $(formEl).addClass('was-validated');
            return;
        }

        _withSession(function () {
            const formData = new FormData(formEl);
            $.ajax({
                type: 'POST',
                url: $(formEl).attr('action'),
                data: formData,
                contentType: false,
                processData: false,
                success: function (response) {
                    // Cierra modal específico si se indicó
                    if (modalSelector) { $(modalSelector).modal('hide'); }

                    // Refresca tabla si se indicó
                    if (recargarTablaFn) { recargarTablaFn(); }

                    // Mensaje ok
                    Swal.fire('Operación exitosa', (response || 'Completado correctamente.'), 'success');

                    if (onSuccess) onSuccess(response);
                },
                error: function (xhr) {
                    const msg = xhr?.responseText || 'Ha ocurrido un error. Por favor, intenta nuevamente.';
                    Swal.fire('Error', msg, 'error');
                    if (onError) onError(xhr);
                }
            });
        });
    });
}