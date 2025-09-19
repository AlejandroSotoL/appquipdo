package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.data.model.EmailsDtos.EmailDto
import com.tramites1cero1.centralizacion.data.model.RecoveryPasswordUiState
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseExtraDto

interface SendEmailRepository {
    suspend fun sendEmail(emailDto: EmailDto): ValidationResponseDTO

    //Send code to verification, if existed user or not
    suspend fun sendEmailVeryficatedCode(To:String): ValidationResponseExtraDto

    suspend fun verifyCode(code:String , emailCode:String) : ValidationResponseExtraDto

}