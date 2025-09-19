package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.data.model.pqrddto.ActividadEconomicaPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.AsuntoInteres
import com.tramites1cero1.centralizacion.data.model.pqrddto.AtencionPreferencial
import com.tramites1cero1.centralizacion.data.model.pqrddto.ClasificacionSolicitud
import com.tramites1cero1.centralizacion.data.model.pqrddto.DiscapacidadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.EscolaridadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GeneroPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GrupoEtnicoPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GrupoInteresPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.MedioRespuesta
import com.tramites1cero1.centralizacion.data.model.pqrddto.NivelEstratoPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.NivelSisbenPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.PqrdAnonimaPost
import com.tramites1cero1.centralizacion.data.model.pqrddto.PqrdIdentificacionPost
import com.tramites1cero1.centralizacion.data.model.pqrddto.RangoEdadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.ResponsePQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.Secretaria
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoDocumento
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoSolicitante
import com.tramites1cero1.centralizacion.data.model.pqrddto.VulnerabilidadPQRD

interface PqrdRepository {

    // ----------- POST -----------
    suspend fun insertPQRDAnonima(body: PqrdAnonimaPost): ResponsePQRD

    suspend fun insertPQRDIdentificacion(body: PqrdIdentificacionPost): ResponsePQRD


    // ----------- GET -----------
    suspend fun listSecretariaEntidad(codigoEntidad: String): List<Secretaria>

    suspend fun listAsuntoInteres(codigoEntidad: String): List<AsuntoInteres>

    suspend fun listClasificacionSolicitud(codigoEntidad: String): List<ClasificacionSolicitud>

    suspend fun listTipoSolicitante(codigoEntidad: String): List<TipoSolicitante>

    suspend fun listAtencionPreferencial(codigoEntidad: String): List<AtencionPreferencial>

    suspend fun listMedioRespuesta(codigoEntidad: String): List<MedioRespuesta>

    suspend fun listTipoDocumento(codigoEntidad: String): List<TipoDocumento>

    suspend fun getListGrupoInteresPQRD(codigoEntidad: String): List<GrupoInteresPQRD>

    suspend fun listDiscapacidadPQRD(codigoEntidad: String): List<DiscapacidadPQRD>

    suspend fun getListGrupoEtnicoPQRD(codigoEntidad: String): List<GrupoEtnicoPQRD>

    suspend fun listGeneroPQRD(codigoEntidad: String): List<GeneroPQRD>

    suspend fun listRangoEdadPQRD(codigoEntidad: String): List<RangoEdadPQRD>

    suspend fun listActividadEconomicaPQRD(codigoEntidad: String): List<ActividadEconomicaPQRD>

    suspend fun listNivelEstractoPQRD(codigoEntidad: String): List<NivelEstratoPQRD>

    suspend fun listNivelSisbenPQRD(codigoEntidad: String): List<NivelSisbenPQRD>

    suspend fun listEscolaridadPQRD(codigoEntidad: String): List<EscolaridadPQRD>

    suspend fun listVulnerabilidadPQRD(codigoEntidad: String): List<VulnerabilidadPQRD>
}