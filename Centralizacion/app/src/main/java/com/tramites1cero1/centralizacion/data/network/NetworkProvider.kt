package com.tramites1cero1.centralizacion.data.network
import kotlin.getValue

object NetworkProvider {
    private const val TAX_API_URL = "http://apidatamovil.1cero1.com/api/"
    private const val AUTH_API_URL  = "https://apicentralizate.1cero1.com/"
    private const val FINTECH_PAYMENTS_API_URL = "https://transaccionfintech.1cero1.com/"
    private const val NEWS_API_URL = "https://www.giron-santander.gov.co/NuestraAlcaldia/sSaladePrensa/"
    private const val GOOGLE_API_URL = "https://people.googleapis.com/"
    private val PQRD_API_URL = "https://tramitesservices.1cero1.com/ApiTramites/api/"
    private const val AUTOLIQUIDABLES_API_URL = "https://autoliquidables.1cero1.com/"
    private val STATUS_PAYMENT = "https://www.1cero1pay.com/ApiPayment/api/"

    val statusPayment : StatusOfPayments by lazy {
        RetrofitClient.createService(STATUS_PAYMENT , StatusOfPayments::class.java)
    }

    val invitatedPeople : PeopleInvitatedService by lazy {
        RetrofitClient.createService(AUTH_API_URL ,PeopleInvitatedService::class.java)
    }
    val generalesApiService: GeneralesApiService by lazy {
        RetrofitClient.createService(AUTOLIQUIDABLES_API_URL, GeneralesApiService::class.java)
    }

    val pqrdApiService: PqrdApiService by lazy {
        RetrofitClient.createService(PQRD_API_URL, PqrdApiService::class.java)
    }

    val googleApiService: GoogleApiService by lazy {
        RetrofitClient.createService(GOOGLE_API_URL, GoogleApiService::class.java)
    }

    val ApiService: TaxApiService by lazy {
        RetrofitClient.createService(TAX_API_URL, TaxApiService::class.java)
    }

    val sendEmailService: SendEmailsService by lazy {
        RetrofitClient.createService(AUTH_API_URL ,SendEmailsService::class.java )
    }

    val paymentApiService: PaymentApiService by lazy {
        RetrofitClient.createService(TAX_API_URL, PaymentApiService::class.java)
    }

    val paymentHistoryService: PaymentHistory by lazy {
        RetrofitClient.createService(AUTH_API_URL, PaymentHistory::class.java)
    }

    val authApiService: AuthApiService by lazy {
        RetrofitClient.createService(AUTH_API_URL, AuthApiService::class.java)
    }

    val remindersApiService : RemindersApiService by lazy {
        RetrofitClient.createService(AUTH_API_URL, RemindersApiService::class.java)
    }

    val municipalityApiService : MunicipalityApiService by lazy {
        RetrofitClient.createService(AUTH_API_URL, MunicipalityApiService::class.java)
    }

    val departmentApiService : DepartmentApiService by lazy {
        RetrofitClient.createService(AUTH_API_URL, DepartmentApiService::class.java)
    }

    val fintechPaymentsApiService: FintechPayments by lazy {
        RetrofitClient.createService(AUTH_API_URL, FintechPayments::class.java)
    }
}