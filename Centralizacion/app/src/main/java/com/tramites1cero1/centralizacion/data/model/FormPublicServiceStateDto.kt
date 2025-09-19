package com.tramites1cero1.centralizacion.data.model

data class FormPublicServiceStateDto(
    var direccion: String = "",
    var documento: String = "",
    var email: String = "",
    var primerApellido: String = "",
    var primerNombre: String = "",
    var segundoApellido: String = "",
    var segundoNombre: String = "",
    var telefono: String = "",
    var tipoDocumento: Int = 2,
    var idTramite: Int = 1,
    var fuentePago: Int = 1,
    var tipoImplementacion: Int = 1,
    var estadoUrl: Boolean = true,
    var url: String = "https://alcaldia.gov.co",
    var referencia: String = "pago servicios publicos",
    var factura: String = "",
    var valorPagar: String = "",
    var descripcion: String = "pagos servicios publicos"
)
