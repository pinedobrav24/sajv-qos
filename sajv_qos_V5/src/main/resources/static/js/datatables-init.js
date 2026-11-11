
document.addEventListener('DOMContentLoaded', function () {

    // Configuración general para todas las tablas del sistema
    const configuracionGeneral = {

        paging: true,
        searching: true,
        ordering: true,
        info: true,
        autoWidth: false,

        pageLength: 10,

        lengthMenu: [
            [5, 10, 25, 50, -1],
            [5, 10, 25, 50, 'Todos']
        ],

        language: {
            emptyTable: 'No hay registros disponibles',
            info: 'Mostrando _START_ a _END_ de _TOTAL_ registros',
            infoEmpty: 'Mostrando 0 a 0 de 0 registros',
            infoFiltered: '(filtrado de _MAX_ registros totales)',
            lengthMenu: 'Mostrar _MENU_ registros',
            loadingRecords: 'Cargando...',
            processing: 'Procesando...',
            search: 'Buscar:',
            zeroRecords: 'No se encontraron resultados',
            paginate: {
                first: 'Primero',
                last: 'Último',
                next: 'Siguiente',
                previous: 'Anterior'
            }
        }
    };

    // Identificar automáticamente las tablas cuyo ID comienza con "tabla"
    const tablas = document.querySelectorAll('table[id^="tabla"]');

    tablas.forEach(function (tabla) {

        // Evitar inicialización duplicada
        if ($.fn.DataTable.isDataTable(tabla)) {
            return;
        }

        // Configuración individual
        const configuracion = {
            ...configuracionGeneral
        };

        // Columna de operaciones: data-no-order="4"
        const columnasNoOrdenables = tabla.dataset.noOrder;

        if (columnasNoOrdenables) {
            configuracion.columnDefs = [{
                targets: columnasNoOrdenables.split(',').map(Number),
                orderable: false,
                searchable: false
            }];
        }

        // Orden inicial: data-order-column="0"
        if (tabla.dataset.orderColumn !== undefined) {
            configuracion.order = [[
                Number(tabla.dataset.orderColumn),
                tabla.dataset.orderDirection || 'asc'
            ]];
        }

        // Cantidad inicial: data-page-length="25"
        if (tabla.dataset.pageLength) {
            configuracion.pageLength =
                Number(tabla.dataset.pageLength);
        }

        $(tabla).DataTable(configuracion);

        console.log(
            'DataTables inicializado:',
            tabla.id
        );
    });

});
