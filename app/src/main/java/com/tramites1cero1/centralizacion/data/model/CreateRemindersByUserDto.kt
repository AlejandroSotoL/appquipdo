import com.google.gson.annotations.SerializedName

data class CreateReminderDto(
    @SerializedName("ExpirationDate")
    val expirationDate: String?,

    @SerializedName("VigenciaDate")
    val vigenciaDate: String?,

    @SerializedName("ReminderType")
    val reminderType: String?,

    @SerializedName("IdProcedureMunicipality")
    val idProcedureMunicipality: Int?,

    @SerializedName("IdUser")
    val idUser: Int?
)
