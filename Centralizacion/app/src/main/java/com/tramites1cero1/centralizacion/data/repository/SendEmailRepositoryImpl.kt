package com.tramites1cero1.centralizacion.data.repository

import com.tramites1cero1.centralizacion.data.model.EmailsDtos.EmailDto
import com.tramites1cero1.centralizacion.data.model.RecoveryPasswordUiState
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseExtraDto
import com.tramites1cero1.centralizacion.data.network.SendEmailsService
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import com.tramites1cero1.centralizacion.domain.repository.SendEmailRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SendEmailRepositoryImpl @Inject constructor(
    private val sendEmailService : SendEmailsService,
    ) : SendEmailRepository {

    override suspend fun sendEmail(emailDto: EmailDto): ValidationResponseDTO {
       return try{
            sendEmailService.sendEmail(emailDto)
        }catch (e: Exception){
           ValidationResponseDTO(
               sentencesError = "Tenemos problemas con el envio del correo",
               booleanStatus = false
           );
        } as ValidationResponseDTO
    }

    //Send code to verification, if existed user or not
    override suspend fun sendEmailVeryficatedCode(To:String): ValidationResponseExtraDto {
       return try{
            val result = sendEmailService.sendEmailValidationCode(To)
            result
        }catch (ex: Exception){
           ValidationResponseExtraDto(
               booleanStatus = false,
               sentencesError = "Tenemos problemas para recuperar tu Email"
           );
        }
    }

    override suspend fun verifyCode(
        code: String,
        emailCode: String
    ): ValidationResponseExtraDto {
        return try {
            if (code == emailCode) {
                ValidationResponseExtraDto(
                    booleanStatus = true,
                    sentencesError = "Correct"
                )
            } else {
                ValidationResponseExtraDto(
                    booleanStatus = false,
                    sentencesError = "El código ingresado no es correcto"
                )
            }
        } catch (ex: Exception) {
            ValidationResponseExtraDto(
                booleanStatus = false,
                sentencesError = "Ocurrió un error: ${ex.message}"
            )
        }
    }

}