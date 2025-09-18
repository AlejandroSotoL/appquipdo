package com.tramites1cero1.tramiappquibdo.data.repository

import android.util.Log
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.ActividadEconomicaPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.AsuntoInteres
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.AtencionPreferencial
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.ClasificacionSolicitud
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.DiscapacidadPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.EscolaridadPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.GeneroPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.GrupoEtnicoPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.GrupoInteresPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.MedioRespuesta
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.NivelEstratoPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.NivelSisbenPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.PqrdAnonimaPost
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.PqrdIdentificacionPost
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.RangoEdadPQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.ResponsePQRD
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.Secretaria
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.TipoDocumento
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.TipoSolicitante
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.VulnerabilidadPQRD
import com.tramites1cero1.tramiappquibdo.data.network.PqrdApiService
import com.tramites1cero1.tramiappquibdo.domain.repository.PqrdRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PqrdRepositoryImpl @Inject constructor(
    private val pqrdApiService: PqrdApiService
) : PqrdRepository {

    // ----------- POST -----------
    override suspend fun insertPQRDAnonima(body: PqrdAnonimaPost): ResponsePQRD {
        return pqrdApiService.insertPQRDAnonima(body)
    }

    override suspend fun insertPQRDIdentificacion(body: PqrdIdentificacionPost): ResponsePQRD {
        return pqrdApiService.insertPQRDIdentificacion(body)
    }

    // ----------- GET -----------

    override suspend fun listSecretariaEntidad(codigoEntidad: String): List<Secretaria> {
        return try {
            val result = pqrdApiService.listSecretariaEntidad(codigoEntidad)
            Log.d("PQRD_REPO", "listSecretariaEntidad OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listSecretariaEntidad: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listAsuntoInteres(codigoEntidad: String): List<AsuntoInteres> {
        return try {
            val result = pqrdApiService.listAsuntoInteres(codigoEntidad)
            Log.d("PQRD_REPO", "listAsuntoInteres OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listAsuntoInteres: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listClasificacionSolicitud(codigoEntidad: String): List<ClasificacionSolicitud> {
        return try {
            val result = pqrdApiService.listClasificacionSolicitud(codigoEntidad)
            Log.d("PQRD_REPO", "listClasificacionSolicitud OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listClasificacionSolicitud: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listTipoSolicitante(codigoEntidad: String): List<TipoSolicitante> {
        return try {
            val result = pqrdApiService.listTipoSolicitante(codigoEntidad)
            Log.d("PQRD_REPO", "listTipoSolicitante OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listTipoSolicitante: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listAtencionPreferencial(codigoEntidad: String): List<AtencionPreferencial> {
        return try {
            val result = pqrdApiService.listAtencionPreferencial(codigoEntidad)
            Log.d("PQRD_REPO", "listAtencionPreferencial OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listAtencionPreferencial: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listMedioRespuesta(codigoEntidad: String): List<MedioRespuesta> {
        return try {
            val result = pqrdApiService.listMedioRespuesta(codigoEntidad)
            Log.d("PQRD_REPO", "listMedioRespuesta OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listMedioRespuesta: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listTipoDocumento(codigoEntidad: String): List<TipoDocumento> {
        return try {
            val result = pqrdApiService.listTipoDocumento(codigoEntidad)
            Log.d("PQRD_REPO", "listTipoDocumento OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listTipoDocumento: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getListGrupoInteresPQRD(codigoEntidad: String): List<GrupoInteresPQRD> {
        return try {
            val result = pqrdApiService.getListGrupoInteresPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "getListGrupoInteresPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en getListGrupoInteresPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listDiscapacidadPQRD(codigoEntidad: String): List<DiscapacidadPQRD> {
        return try {
            val result = pqrdApiService.listDiscapacidadPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listDiscapacidadPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listDiscapacidadPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getListGrupoEtnicoPQRD(codigoEntidad: String): List<GrupoEtnicoPQRD> {
        return try {
            val result = pqrdApiService.getListGrupoEtnicoPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "getListGrupoEtnicoPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en getListGrupoEtnicoPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listGeneroPQRD(codigoEntidad: String): List<GeneroPQRD> {
        return try {
            val result = pqrdApiService.listGeneroPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listGeneroPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listGeneroPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listRangoEdadPQRD(codigoEntidad: String): List<RangoEdadPQRD> {
        return try {
            val result = pqrdApiService.listRangoEdadPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listRangoEdadPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listRangoEdadPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listActividadEconomicaPQRD(codigoEntidad: String): List<ActividadEconomicaPQRD> {
        return try {
            val result = pqrdApiService.listActividadEconomicaPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listActividadEconomicaPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listActividadEconomicaPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listNivelEstractoPQRD(codigoEntidad: String): List<NivelEstratoPQRD> {
        return try {
            val result = pqrdApiService.listNivelEstractoPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listNivelEstractoPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listNivelEstractoPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listNivelSisbenPQRD(codigoEntidad: String): List<NivelSisbenPQRD> {
        return try {
            val result = pqrdApiService.listNivelSisbenPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listNivelSisbenPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listNivelSisbenPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listEscolaridadPQRD(codigoEntidad: String): List<EscolaridadPQRD> {
        return try {
            val result = pqrdApiService.listEscolaridadPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listEscolaridadPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listEscolaridadPQRD: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun listVulnerabilidadPQRD(codigoEntidad: String): List<VulnerabilidadPQRD> {
        return try {
            val result = pqrdApiService.listVulnerabilidadPQRD(codigoEntidad)
            Log.d("PQRD_REPO", "listVulnerabilidadPQRD OK -> ${result.size} items")
            result
        } catch (e: Exception) {
            Log.e("PQRD_REPO", "Error en listVulnerabilidadPQRD: ${e.message}", e)
            emptyList()
        }
    }
}