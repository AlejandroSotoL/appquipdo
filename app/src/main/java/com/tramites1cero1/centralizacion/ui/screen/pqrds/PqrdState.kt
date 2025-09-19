package com.tramites1cero1.centralizacion.ui.screen.pqrds

import com.tramites1cero1.centralizacion.data.model.pqrddto.ActividadEconomicaPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.AsuntoInteres
import com.tramites1cero1.centralizacion.data.model.pqrddto.AtencionPreferencial
import com.tramites1cero1.centralizacion.data.model.pqrddto.Ciudad
import com.tramites1cero1.centralizacion.data.model.pqrddto.ClasificacionSolicitud
import com.tramites1cero1.centralizacion.data.model.pqrddto.Departamento
import com.tramites1cero1.centralizacion.data.model.pqrddto.DiscapacidadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.EscolaridadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GeneroPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GrupoEtnicoPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GrupoInteresPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.MedioRespuesta
import com.tramites1cero1.centralizacion.data.model.pqrddto.NivelEstratoPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.NivelSisbenPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.RangoEdadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.Secretaria
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoDocumento
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoSolicitante
import com.tramites1cero1.centralizacion.data.model.pqrddto.VulnerabilidadPQRD

data class PqrdFormState(
    // Paso 1
    val secretaria: Secretaria? = null,
    val asuntoInteres: AsuntoInteres? = null,
    val clasificacionSolicitud: ClasificacionSolicitud? = null,
    val tipoSolicitante: TipoSolicitante? = null,
    val atencionPreferencial: AtencionPreferencial? = null,
    val medioRespuesta: MedioRespuesta? = null,

    // Paso 2
    val tipoDocumento: TipoDocumento? = null,
    val identificacion: String = "",
    val primerNombre: String = "",
    val segundoNombre: String = "",
    val primerApellido: String = "",
    val segundoApellido: String = "",
    val grupoInteres: GrupoInteresPQRD? = null,
    val discapacidad: DiscapacidadPQRD? = null,
    val grupoEtnico: GrupoEtnicoPQRD? = null,
    val genero: GeneroPQRD? = null,
    val rangoEdad: RangoEdadPQRD? = null,
    val actividadEconomica: ActividadEconomicaPQRD? = null,
    val nivelEstrato: NivelEstratoPQRD? = null,
    val nivelSisben: NivelSisbenPQRD? = null,
    val escolaridad: EscolaridadPQRD? = null,
    val vulnerabilidad: VulnerabilidadPQRD? = null,

    // Paso 3
    val pais: String = "Colombia", // Asumiendo un valor por defecto
    val departamento: Departamento? = null,
    val ciudad: Ciudad? = null,
    val razonSocial: String = "",
    val correoElectronico: String = "",
    val direccion: String = "",
    val telefonoCelular: String = "",
    val telefonoFijo: String = "",
    val descripcion: String = "",

    // Pantalla de Términos
    val aceptaTratamientoDatos: Boolean = false,
    val aceptaCondicionesUso: Boolean = false,

    //Archivo
    val nombreArchivo : String? = null,
    val tipoArchivo: String? = null,
    val contenidoArchivoBase64: String? = null,
)

data class PqrdDropdownOptionsState(
    val secretarias: List<Secretaria> = emptyList(),
    val asuntosInteres: List<AsuntoInteres> = emptyList(),
    val clasificacionesSolicitud: List<ClasificacionSolicitud> = emptyList(),
    val tiposSolicitante: List<TipoSolicitante> = emptyList(),
    val atencionesPreferenciales: List<AtencionPreferencial> = emptyList(),
    val mediosRespuesta: List<MedioRespuesta> = emptyList(),
    val tiposDocumento: List<TipoDocumento> = emptyList(),
    val gruposInteres: List<GrupoInteresPQRD> = emptyList(),
    val discapacidades: List<DiscapacidadPQRD> = emptyList(),
    val gruposEtnicos: List<GrupoEtnicoPQRD> = emptyList(),
    val generos: List<GeneroPQRD> = emptyList(),
    val rangosEdad: List<RangoEdadPQRD> = emptyList(),
    val actividadesEconomicas: List<ActividadEconomicaPQRD> = emptyList(),
    val nivelesEstrato: List<NivelEstratoPQRD> = emptyList(),
    val nivelesSisben: List<NivelSisbenPQRD> = emptyList(),
    val escolaridades: List<EscolaridadPQRD> = emptyList(),
    val vulnerabilidades: List<VulnerabilidadPQRD> = emptyList(),
    val departamentos: List<Departamento> = emptyList(),
    val ciudades: List<Ciudad> = emptyList(),
    val isLoadingCiudades: Boolean = false, // Para mostrar un spinner
    val isLoading: Boolean = true
)

data class FormErrorState(
    val secretariaError: Boolean = false,
    val asuntoInteresError: Boolean = false,
    val clasificacionSolicitudError: Boolean = false,
    val tipoSolicitanteError: Boolean = false,
    val atencionPreferencialError: Boolean = false,
    val medioRespuestaError: Boolean = false,
    val tipoDocumentoError: Boolean = false,
    val identificacionError: Boolean = false,
    val primerNombreError: Boolean = false,
    val primerApellidoError: Boolean = false,
    val grupoInteresError: Boolean = false,
    val discapacidadError: Boolean = false,
    val grupoEtnicoError: Boolean = false,
    val generoError: Boolean = false,
    val rangoEdadError: Boolean = false,
    val actividadEconomicaError: Boolean = false,
    val nivelEstratoError: Boolean = false,
    val nivelSisbenError: Boolean = false,
    val escolaridadError: Boolean = false,
    val vulnerabilidadError: Boolean = false,

    val departamentoError: Boolean = false,
    val ciudadError: Boolean = false,
    val razonSocialError: Boolean = false,
    val correoElectronicoError: Boolean = false,
    val direccionError: Boolean = false,
    val telefonoCelularError: Boolean = false,


    val descripcionError: Boolean = false,
    val tipoArchivoError: String? = null, // Usamos un String para el mensaje de error del archivo
    val aceptaTratamientoDatosError: Boolean = false,
    val aceptaCondicionesUsoError: Boolean = false,


)