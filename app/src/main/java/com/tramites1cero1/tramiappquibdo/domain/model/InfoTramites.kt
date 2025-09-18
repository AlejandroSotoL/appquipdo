package com.tramites1cero1.tramiappquibdo.domain.model

import androidx.compose.ui.graphics.Color
import com.tramites1cero1.tramiappquibdo.data.mapper.TramiteCategory

data class InfoTramite(
    val nombre: String,
    val icono: Int, // R.drawable.icopred
    val color: Color,
    val accion: TramiteAccion,
    val idtramite: Int,
    val isActive: Boolean,
    val category: TramiteCategory,
)


sealed class TramiteAccion {
    data object ShowPqrds : TramiteAccion()

    data class AbrirUrl(val url: String) : TramiteAccion()

    data class NavegarANativo(val ruta: String) : TramiteAccion()

    data class AbrirUrlDirecto(val url: String) : TramiteAccion()

    data class AbrirBotonPanico(val baseWhatsappUrl: String) : TramiteAccion()

    data class NavegarAConsultaImpuesto(
        val entityCode: String,
        val queryFields: List<QueryField>,
        val taxId: Int,
        val dataPolicyUrl: String,
        val privacyPolicyUrl: String,
    ) : TramiteAccion()

    data class NavegarACursos(
        val getUrl: String,
        val postUrl: String
    ) : TramiteAccion()

    data class NavegarAvenues(
        val getUrl: String,
        val postUrlReservation: String,
        val postUrlCalendar: String,
    ) : TramiteAccion()

    data class NavegarAPagoSinValidacion(
        val taxId: Int,
        val taxName: String,
        val entityCode: String,
        val dataPolicyUrl: String,
        val privacyPolicyUrl: String
    ) : TramiteAccion()
}

data class TramitesContainer(
    val principales: List<InfoTramite> = emptyList(),
    val otros: List<InfoTramite> = emptyList()
)