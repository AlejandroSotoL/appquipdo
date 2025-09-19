package com.tramites1cero1.centralizacion.data.mapper

import androidx.compose.ui.graphics.Color
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.data.model.MunicipalityDTO
import com.tramites1cero1.centralizacion.domain.model.*
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.theme.*
import com.tramites1cero1.centralizacion.domain.model.MunicipalitySocialMedia

enum class TramiteCategory {
    MAIN, OTHER, SOCIAL
}

data class TramiteUiConfig(
    val iconRes: Int,
    val color: Color,
    val category: TramiteCategory
)

private val tramiteConfigMap = mapOf(
    // Trámites Principales (MAIN)
    1 to TramiteUiConfig(R.drawable.icopredial, TramitesSectionColor, TramiteCategory.MAIN), // Predial
    2 to TramiteUiConfig(R.drawable.icoica, TramitesSectionColor, TramiteCategory.MAIN),     // ICA
    3 to TramiteUiConfig(R.drawable.icodecla, TramitesSectionColor, TramiteCategory.MAIN),   // Declaración
    5 to TramiteUiConfig(R.drawable.icoservici, TramitesSectionSecondary, TramiteCategory.MAIN),// Servicios
    6 to TramiteUiConfig(R.drawable.icovehi, TramitesSectionSecondary, TramiteCategory.MAIN),        // Vehículos
    7 to TramiteUiConfig(R.drawable.icorete, TramitesSectionSecondary, TramiteCategory.MAIN),      // retencion ICA

    // Otros Trámites (OTHER)
    4 to TramiteUiConfig(R.drawable.icopqrsdf, OtherSectionColor, TramiteCategory.OTHER), // PQRSDF
    20 to TramiteUiConfig(R.drawable.icoataquepanico, PanicButton, TramiteCategory.OTHER), // Panic!!!!!

    // Módulos Especiales (se tratan como OTROS)
    8 to TramiteUiConfig(R.drawable.icocursos, OtherSectionColor, TramiteCategory.OTHER),      // Cursos
    9 to TramiteUiConfig(R.drawable.icoreservas, OtherSectionColor, TramiteCategory.OTHER) // Reservas
)

private fun getSocialMediaConfig(socialMediaName: String): TramiteUiConfig? {
    val icon = when {
        socialMediaName.contains("Facebook", ignoreCase = true) -> R.drawable.icofacebook
        socialMediaName.contains("Instagram", ignoreCase = true) -> R.drawable.icoinstagram
        socialMediaName.contains("X", ignoreCase = true) -> R.drawable.icoequis
        socialMediaName.contains("Youtube", ignoreCase = true) -> R.drawable.icoyoutube
        socialMediaName.contains("Blogger", ignoreCase = true) -> R.drawable.icoblogger
        else -> return null // Si no se reconoce, no se muestra
    }
    return TramiteUiConfig(icon, SocialMediaColor, TramiteCategory.SOCIAL)
}

// --- MAPPER PRINCIPAL (PÚBLICO) ---
fun MunicipalityDTO.toDomainModel(): MunicipalityModel {

    val integrationModel = this.toIntegrationModel()
    // 1. Mapea los trámites que vienen en la lista `municipalityProcedures`
    val tramitesDesdeApi = this.municipalityProcedures
        .mapNotNull { it.toInfoTramite(this, integrationModel) }

    val onlyProcedures = this.municipalityProcedures

    // 2. Mapea módulos adicionales (si existen en el JSON)
    val cursosTramite = this.courses.firstOrNull()?.let { courseInfo ->
        val config = tramiteConfigMap[8] ?: return@let null
        val action = TramiteAccion.NavegarACursos(getUrl = courseInfo.get, postUrl = courseInfo.post)
        InfoTramite("Cursos", config.iconRes, config.color, action, 8, courseInfo.isActive, config.category)
    }

    val sportsTramite = this.sportsFacilities.firstOrNull()?.let { sportsInfo ->
        val config = tramiteConfigMap[9] ?: return@let null
        val action = TramiteAccion.NavegarAvenues(
            getUrl = sportsInfo.get,
            postUrlReservation = sportsInfo.reservationPost,
            postUrlCalendar = sportsInfo.calendaryPost
        )
        InfoTramite("Reserva de espacios", config.iconRes, config.color, action, 9, sportsInfo.isActive, config.category)
    }

    // 3. Mapea las redes sociales
    val socialTramites = this.municipalitySocialMedia
        .mapNotNull { it.toInfoTramite() }

    // 4. Une todas las fuentes en una sola lista, filtrando por activos
    val allTramites = (tramitesDesdeApi + socialTramites + listOfNotNull(cursosTramite, sportsTramite))
        .distinctBy { it.idtramite } // Evita duplicados si la API trae repetidos


    // 5. Extrae la URL de noticias
    val urlNews = this.newsByMunicipalities.firstOrNull()?.url ?: ""

    // 6. Construye el modelo final con las listas ya separadas por categoría
    return MunicipalityModel(
        idMunicipio = this.id,
        codigoEntidad = this.entityCode,
        nombreMunicipio = "Alcaldía de ${this.name}",
        departamento = this.department.name,
        design = this.theme.toDesignModel("Alcaldía de ${this.name}", this.idShield.url),
        bank = this.bank.nameBank,
        tipoIntegracion = integrationModel,
        newsUrl = urlNews,
        domain = this.domain,
        privacyPolicyUrl = this.dataPrivacy ?: "",
        dataPolicyUrl = this.dataProcessingPrivacy ?: "",
        tramitesPrincipales = allTramites.filter { it.category == TramiteCategory.MAIN },
        otrosTramites = allTramites.filter { it.category == TramiteCategory.OTHER },
        socialLinks = allTramites.filter { it.category == TramiteCategory.SOCIAL },
        municipalityProcedures = onlyProcedures
    )
}


internal fun MunicipalitySocialMedia.toInfoTramite(): InfoTramite? {
    val config = getSocialMediaConfig(this.socialMediaType.name) ?: return null
    val accion = TramiteAccion.AbrirUrlDirecto(this.url)

    return InfoTramite(
        nombre = this.socialMediaType.name,
        icono = config.iconRes,
        color = config.color,
        accion = accion,
        idtramite = this.id + 100, // ID único para evitar colisiones
        isActive = this.isActive,
        category = config.category
    )
}


// --- MAPPERS AUXILIARES (INTERNOS) ---
internal fun MunicipalityDTO.toIntegrationModel(): IntegrationTypeModel {

    if (this.entityCode.isNotBlank()) {
        return IntegrationTypeModel.TramitesporAPP(
            codigoEntidad = this.entityCode,
            campoConsulta = this.queryFields
        )
    } else {
        val findUrl = { name: String ->
            this.municipalityProcedures.find { it.procedures.name.contains(name, ignoreCase = true) }?.integrationType ?: ""
        }
        return IntegrationTypeModel.TramitesporURL(
            urlPredial = findUrl("Predial"),
            urlIca = findUrl("Industria"),
            urlPqrds = findUrl("PQRSDF"),
            urlDeclaracion = findUrl("Declaracion"),
            urlReteIca = findUrl("ReteIca")
        )
    }
}
internal fun MunicipalityProcedure.toInfoTramite(
    parentDto: MunicipalityDTO,
    integracion: IntegrationTypeModel
): InfoTramite? {
    // La clave es usar el ID del procedimiento anidado
    val procedureId = this.procedures.id
    val procedureName = this.procedures.name

    // Busca la configuración en el mapa central. Si no existe, el trámite no se mostrará.
    val config = tramiteConfigMap[procedureId] ?: return null

    // La lógica para determinar la acción se mantiene, ya que depende de la integración
    val accion = when {

        procedureId == 20 -> TramiteAccion.AbrirBotonPanico(this.integrationType)

        this.integrationType.isEmpty() && procedureId == 4 -> {
            TramiteAccion.ShowPqrds
        }

        // 4. Finalmente, el caso para los trámites por APP
        this.integrationType.isEmpty() && procedureId == 5 -> {
            TramiteAccion.NavegarANativo(AppRoutes.PUBLIC_SERVICE_SCREEN)
        }

        this.integrationType.isNotEmpty() && this.integrationType.startsWith("http") -> {
            TramiteAccion.AbrirUrl(this.integrationType)
        }

        this.integrationType.equals("psv", ignoreCase = true) -> {
            TramiteAccion.NavegarAPagoSinValidacion(
                taxId = procedureId,
                taxName = procedureName,
                entityCode = parentDto.entityCode,
                dataPolicyUrl = parentDto.dataProcessingPrivacy ?: "",
                privacyPolicyUrl = parentDto.dataPrivacy ?: ""
            )
        }

        else -> {
            if (integracion is IntegrationTypeModel.TramitesporAPP) {
                TramiteAccion.NavegarAConsultaImpuesto(
                    entityCode = integracion.codigoEntidad,
                    queryFields = integracion.campoConsulta,
                    taxId = procedureId,
                    dataPolicyUrl = parentDto.dataProcessingPrivacy ?: "",
                    privacyPolicyUrl = parentDto.dataPrivacy ?: ""
                )
            } else {
                return null
            } // Si no hay URL de integración y no es por APP, no hay acción posible
        }
    }

    return InfoTramite(
        nombre = procedureName,
        icono = config.iconRes,
        color = config.color,
        accion = accion,
        idtramite = procedureId,
        isActive = this.isActive,
        category = config.category
    )
}

internal fun Theme.toDesignModel(alcaldiaName: String, shield: String): Design {
    return Design(
        NombreAlcaldia = alcaldiaName,
        escudoUrl = shield,
        primaryColor = parseColor(this.primaryColor),
        secondaryColor = parseColor(this.secondaryColor),
        secondaryColorDark = parseColor(this.secondaryColorBlack),
        onPrimaryColorLight = parseColor(this.onPrimaryColorLight, defaultColor = Color.Black),
        onPrimaryColorDark = parseColor(this.onPrimaryColorDark, defaultColor = Color.White)
    )
}



internal fun parseColor(hexString: String?, defaultColor: Color = Color.Gray): Color {
    if (hexString.isNullOrBlank()) return defaultColor
    val colorStr = hexString.trim().removePrefix("0x")
    if (colorStr.length != 8) return defaultColor
    return try {
        Color(colorStr.toULong(16).toLong())
    } catch (e: Exception) {
        e.printStackTrace()
        defaultColor
    }
}