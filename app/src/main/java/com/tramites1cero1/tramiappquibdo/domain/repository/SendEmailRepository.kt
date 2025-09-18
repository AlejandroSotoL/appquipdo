package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.EmailsDtos.EmailDto
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseExtraDto

interface SendEmailRepository {
    suspend fun sendEmail(emailDto: EmailDto): ValidationResponseDTO

    //Send code to verification, if existed user or not
    suspend fun sendEmailVeryficatedCode(To:String): ValidationResponseExtraDto

    suspend fun verifyCode(code:String , emailCode:String) : ValidationResponseExtraDto

}