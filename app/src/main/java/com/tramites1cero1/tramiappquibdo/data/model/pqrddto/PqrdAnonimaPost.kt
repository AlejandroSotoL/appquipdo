package com.tramites1cero1.tramiappquibdo.data.model.pqrddto

data class PqrdAnonimaPost(
    val CodigoEntidad: String,
    val Descripcion: String,
    val Documentos: Documentos,
   // val EmailCiudadano: String,
    val IDAsuntoInteres: Int,
    val IDCLasificacion: Int,
    val IDSecretaria: Int,
    //val Recepcion: String
)