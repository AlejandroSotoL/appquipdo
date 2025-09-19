package com.tramites1cero1.centralizacion.data.network

import CreateReminderDto
import com.tramites1cero1.centralizacion.data.model.authenticationRequestFintechDto
import com.tramites1cero1.centralizacion.data.model.authenticationResponseFintechDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

import com.tramites1cero1.centralizacion.data.model.BancolombiaGatewayRequestDTO
import com.tramites1cero1.centralizacion.data.model.BancolombiaGatewayResponseDTO
import com.tramites1cero1.centralizacion.data.model.CreateUserDTO
import com.tramites1cero1.centralizacion.data.model.DocumentTypeDTO
import com.tramites1cero1.centralizacion.data.model.EmailsDtos.EmailDto
import com.tramites1cero1.centralizacion.data.model.LoginDTO
import com.tramites1cero1.centralizacion.data.model.MunicipalityDTO
import com.tramites1cero1.centralizacion.data.model.NewsResponseWrapper
import com.tramites1cero1.centralizacion.data.model.PeopleInvitated
import com.tramites1cero1.centralizacion.data.model.PeopleResponse
import com.tramites1cero1.centralizacion.data.model.RemindersByUserDto
import com.tramites1cero1.centralizacion.data.model.StatusTransactionDto
import com.tramites1cero1.centralizacion.data.model.TaxQueryRequestDTO
import com.tramites1cero1.centralizacion.data.model.TaxQueryResponseDTO
import com.tramites1cero1.centralizacion.data.model.TransactionFintech
import com.tramites1cero1.centralizacion.data.model.TransactionResponse
import com.tramites1cero1.centralizacion.data.model.UserDTO
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordByForgetDto
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordRequestDto
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseExtraDto
import com.tramites1cero1.centralizacion.data.model.historyPaymentDTOs.CreatePaymentHistory
import com.tramites1cero1.centralizacion.data.model.historyPaymentDTOs.PaymentHistoryDTO
import com.tramites1cero1.centralizacion.data.model.historyPaymentDTOs.PaymentHistoryListDTO
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
import com.tramites1cero1.centralizacion.data.model.pqrddto.PqrdAnonimaPost
import com.tramites1cero1.centralizacion.data.model.pqrddto.PqrdIdentificacionPost
import com.tramites1cero1.centralizacion.data.model.pqrddto.RangoEdadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.ResponsePQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.Secretaria
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoDocumento
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoSolicitante
import com.tramites1cero1.centralizacion.data.model.pqrddto.VulnerabilidadPQRD
import com.tramites1cero1.centralizacion.domain.model.Department
import com.tramites1cero1.centralizacion.domain.model.InfoTramite
import com.tramites1cero1.centralizacion.domain.model.Municipality
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface TaxApiService {
    @POST("ImpuestoEntidad/GetImpuestos")
    suspend fun getTaxes(@Body body: TaxQueryRequestDTO): Response<TaxQueryResponseDTO>
    @POST("ImpuestoEntidad/GetDownloadPDF")
    suspend fun downloadInvoice(@Body body: TaxQueryRequestDTO): Response<String>
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}

interface SendEmailsService{
    @POST("api/Email/SendEmail")
    suspend fun sendEmail(@Body emailDto: EmailDto) : ValidationResponseDTO

    @GET("api/Email/SendEmail/ValidationCode")
    suspend fun sendEmailValidationCode(@Query("To") To: String) : ValidationResponseExtraDto
}

interface PaymentApiService {
    @POST("Pasarela/CrearTransaccion")
    suspend fun createTransaction(
        @Body body: BancolombiaGatewayRequestDTO
    ): Response<BancolombiaGatewayResponseDTO>
}

interface PeopleInvitatedService {
    @POST("api/PeopleInvitated/Create")
    suspend fun createPeopleInvitated(
        @Body people: PeopleInvitated
    ): ValidationResponseDTO
}

interface AuthApiService {
    @POST("api/Auth")
    suspend fun loginUser(@Body loginDto: LoginDTO): ValidationResponseDTO

    @POST("api/User/CreateUser")
    suspend fun createUser(@Body user: CreateUserDTO): ValidationResponseDTO

    @GET("/api/User/by-email/")
    suspend fun getUserByEmail(@Query("email") email: String): UserDTO?

    @GET("/api/DocumentType/GetDocumentTypes")
    suspend fun getDocumentType() : List<DocumentTypeDTO>

    @PUT("/api/User/ChangeStatusUser/{id}/status/{status}")
    suspend fun changedStatusUser(@Path("id") id:Int , @Path("status") status: Boolean) : ValidationResponseDTO

    //Recovery  password
    @PUT("api/User/update-password/{userId}")
    suspend fun updatePasswordUser(@Path("userId") userId: Int, @Body request:UpdatePasswordRequestDto): ValidationResponseDTO

    @PUT("api/User/updatePasswordByForget/{userId}")
    suspend fun updatePasswordByForget(
        @Path("userId") userId: Int,
        @Body request: UpdatePasswordByForgetDto
    ): ValidationResponseDTO

    @DELETE("api/User/Delete/{id}")
    suspend fun deleteUser(@Path("id") id:Int) : ValidationResponseDTO

    //What do we this??
    @GET("api/user")
    suspend  fun  getUsers():List<UserDTO>
}

interface GoogleApiService {
        @GET("v1/people/me?personFields=names,emailAddresses,phoneNumbers,birthdays,addresses")
        suspend fun getProfile(@Header("Authorization") token: String): PeopleResponse

}


interface RemindersApiService {
    @GET("api/Reminders/Get/Reminders/ByUser/{userId}")
    suspend fun getRemindersByUser(
        @Path("userId") userId: Int
    ): List<RemindersByUserDto>

    @POST("/api/Reminders/Create/Reminders")
    suspend fun createReminders(@Body request: CreateReminderDto) : ValidationResponseDTO
}

interface  MunicipalityApiService{
    @GET("/api/Municipality/GetInfoBy{id}")
    suspend fun getMunicipalitiesById(@Path("id") id: Int): MunicipalityDTO
    @GET("/api/Municipality/ByDepartamet_{id}")
    suspend fun getDepartmentMunicipalitiesById(@Path("id") id: Int): List<Municipality>
}

interface  DepartmentApiService{
    @GET("/api/Department")
    suspend fun getDepartments(): List<Department>
}

interface  FintechPayments {
    @POST("api/Fintech/transactionFintech/{id}")
    suspend fun transactionFintech(
        @Body transactionFintech: TransactionFintech,
        @Path("id") id: Int
    ): TransactionResponse
}

interface TramitesApiService {
    @GET("api/municipios/{id}/tramites")
    suspend fun getTramitesForMunicipality(@Path("id") municipalityId: Int): List<InfoTramite>
}




interface StatusOfPayments {
    @POST("Login/authenticate")
    suspend fun authenticate(
        @Body loginRequest: LoginRequest
    ): String

    @GET("transaction")
    suspend fun getStatusOfPayment(
        @Header("Authorization") token: String,
        @Query("CodigoEntidad") codigoEntidad: String,
        @Query("Factura") factura: String,
        @Query("IDImpuesto") idImpuesto: String
    ): StatusTransactionDto
}


data class LoginRequest(
    val username: String = "HLF26+ab0xEMPbPlEBe1cl2dNkVtolspdhG+3K3t6e8=",
    val password: String = "HLF26+ab0xEMPbPlEBe1ci4JWvvUJsZagnsjbRJZWPn14keMkZIoEyomwLIE/oo+"
)

data class TokenResponse(
    val token: String
)

interface  PaymentHistory {
    @GET("api/PaymentHistory/User/{id}")
    suspend fun getHistoryPaymentByUser(@Path("id") id:Int) : PaymentHistoryListDTO

    @PUT("api/PaymentHistory/{idHistory}")

        suspend fun updateStatusHistory(
            @Path("idHistory") idHistory: Int,
            @Query("idStatusType") idStatusType: Int
        ): ValidationResponseDTO

    @DELETE("api/PaymentHistory/User/{idUser}/History/{idHistory}")
    suspend fun deleteHistoryByUser(
        @Path("idUser") idUser: Int,
        @Path("idHistory") idHistory: Int
    ): ValidationResponseDTO

    @POST("api/PaymentHistory/")
    suspend fun createPaymentHistory(@Body request: CreatePaymentHistory) : ValidationResponseDTO
}

interface GeneralesApiService {
    @GET("api/Generales/GetDepartamentos")
    suspend fun getDepartamentos(): List<Departamento>

    @GET("api/Generales/CiudadesXDepartamento")
    suspend fun getCiudadesPorDepartamento(
        @Query("id_Departamento") departamentoId: String
    ): List<Ciudad>

}

interface PqrdApiService {
    // ----------- POST -----------
    @POST("PQRD/InsertPQRDAnonima")
    suspend fun insertPQRDAnonima(
        @Body body: PqrdAnonimaPost // aquí defines tu modelo de datos
    ): ResponsePQRD

    @POST("PQRD/InsertPQRDIdentificacion")
    suspend fun insertPQRDIdentificacion(
        @Body body: PqrdIdentificacionPost
    ): ResponsePQRD


    // ----------- GET -----------
    @GET("SecretariaEntidad/ListSecretariaEntidad")
    suspend fun listSecretariaEntidad(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<Secretaria>

    @GET("PQRD/ListAsuntoInteres")
    suspend fun listAsuntoInteres(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<AsuntoInteres>

    @GET("PQRD/ListClasificacionSolicitud")
    suspend fun listClasificacionSolicitud(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<ClasificacionSolicitud>

    @GET("PQRD/ListTipoSolicitante")
    suspend fun listTipoSolicitante(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<TipoSolicitante>

    @GET("PQRD/ListAtencionPreferencial")
    suspend fun listAtencionPreferencial(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<AtencionPreferencial>

    @GET("PQRD/ListMedioRespuesta")
    suspend fun listMedioRespuesta(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<MedioRespuesta>

    @GET("PQRD/ListTipoDocumento")
    suspend fun listTipoDocumento(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<TipoDocumento>

    @GET("PQRD/GetListGrupoInteresPQRD")
    suspend fun getListGrupoInteresPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<GrupoInteresPQRD>

    @GET("PQRD/ListDiscapacidadPQRD")
    suspend fun listDiscapacidadPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<DiscapacidadPQRD>

    @GET("PQRD/GetListGrupoEtnicoPQRD")
    suspend fun getListGrupoEtnicoPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<GrupoEtnicoPQRD>

    @GET("PQRD/ListGeneroPQRD")
    suspend fun listGeneroPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<GeneroPQRD>

    @GET("PQRD/ListRangoEdadPQRD")
    suspend fun listRangoEdadPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<RangoEdadPQRD>

    @GET("PQRD/ListActividadEconomicaPQRD")
    suspend fun listActividadEconomicaPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<ActividadEconomicaPQRD>

    @GET("PQRD/ListNivelEstractoPQRD")
    suspend fun listNivelEstractoPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<NivelEstratoPQRD>

    @GET("PQRD/ListNivelSisbenPQRD")
    suspend fun listNivelSisbenPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<NivelSisbenPQRD>

    @GET("PQRD/ListEscolaridadPQRD")
    suspend fun listEscolaridadPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<EscolaridadPQRD>

    @GET("PQRD/ListVulnerabilidadPQRD")
    suspend fun listVulnerabilidadPQRD(
        @Query("CodigoEntidad") codigoEntidad: String
    ): List<VulnerabilidadPQRD>
}
