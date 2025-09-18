package com.tramites1cero1.tramiappquibdo.domain.model

sealed class IntegrationTypeModel {

    data class TramitesporURL(
        val urlPredial : String,
        val urlIca : String,
        val urlPqrds : String,
        val urlDeclaracion : String,
        val urlReteIca : String,
    ) : IntegrationTypeModel()

    data class TramitesporAPP(
        val codigoEntidad : String,
        val campoConsulta : List<QueryField>,
    ) : IntegrationTypeModel()

}