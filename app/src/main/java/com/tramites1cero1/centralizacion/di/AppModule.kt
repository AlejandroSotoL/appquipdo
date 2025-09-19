package com.tramites1cero1.centralizacion.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.gson.Gson
import com.tramites1cero1.centralizacion.data.network.*
import com.tramites1cero1.centralizacion.data.repository.*
import com.tramites1cero1.centralizacion.domain.repository.*
import com.tramites1cero1.centralizacion.domain.usecase.*
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.tramites1cero1.centralizacion.R
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // --- API SERVICES ---
    @Provides
    @Singleton
    fun provideAuthApiService(): AuthApiService = NetworkProvider.authApiService

    @Provides
    @Singleton
    fun providePeopleInvitatedService() =  NetworkProvider.invitatedPeople

    @Provides
    @Singleton
    fun provideCheckStatusPayment() : StatusOfPayments = NetworkProvider.statusPayment

    @Provides
    @Singleton
    fun provideRemindersApiService() : RemindersApiService = NetworkProvider.remindersApiService

    @Provides
    @Singleton
    fun provideHistoryApiService(): PaymentHistory = NetworkProvider.paymentHistoryService

    @Provides
    @Singleton
    fun providePqrdApiService(): PqrdApiService = NetworkProvider.pqrdApiService

    @Provides
    @Singleton
    fun provideSendEmailApiServe() : SendEmailsService = NetworkProvider.sendEmailService

    @Provides
    @Singleton
    fun provideTaxApiService(): TaxApiService = NetworkProvider.ApiService

    @Provides
    @Singleton
    fun provideFintechPaymentsApiService(): FintechPayments = NetworkProvider.fintechPaymentsApiService

    @Provides
    @Singleton
    fun providePaymentApiService(): PaymentApiService = NetworkProvider.paymentApiService

    @Provides
    @Singleton
    fun provideGoogleApiService(): GoogleApiService = NetworkProvider.googleApiService

    @Provides
    @Singleton
    fun provideMunicipalityApiService(): MunicipalityApiService = NetworkProvider.municipalityApiService

    @Provides
    @Singleton
    fun provideDepartmentApiService(): DepartmentApiService = NetworkProvider.departmentApiService

    @Provides
    @Singleton
    fun provideGeneralesApiService(): GeneralesApiService = NetworkProvider.generalesApiService

    // --- REPOSITORIES ---

    @Provides
    @Singleton
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return LocationProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideGeneralesRepository(
        generalesApiService: GeneralesApiService
    ): GeneralesRepository {
        return GeneralesRepositoryImpl(generalesApiService)
    }

    @Provides
    @Singleton
    fun providePqrdsRepository(
        apiService: PqrdApiService
    ): PqrdRepository {
        return PqrdRepositoryImpl(apiService)
    }
    @Provides
    @Singleton
    fun provideRemindersRepository(
        apiService : RemindersApiService
    ): RemindersRepository {
        return RemindersRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideSendEmailRepository(
        apiService: SendEmailsService,
    ): SendEmailRepository{
        return SendEmailRepositoryImpl(apiService)
    }


    @Provides
    @Singleton
    fun provideMunicipalityRepository(
        apiService: MunicipalityApiService
    ): MunicipalityRepository {
        return MunicipalityRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideDepartmentRepository(apiService: DepartmentApiService): DepartmentRepository {
        return DepartmentRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context , peopleService : PeopleInvitatedService): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(context, context.dataStore , peopleService)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: AuthApiService, @ApplicationContext context: Context): AuthRepository {
        return AuthRepositoryImpl(apiService, context)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        apiService: PaymentHistory,
        statusOfPaymentsApi : StatusOfPayments,
        @ApplicationContext context: Context
    ): HistoryPayRepository{
        return HistoryPayRepositoryImpl(apiService , statusOfPaymentsApi)
    }

    @Provides
    @Singleton
    fun provideTaxRepository(taxApi: TaxApiService, paymentApi: PaymentApiService): TaxRepository {
        return TaxRepositoryImpl(taxApi, paymentApi)
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 600   /// Subir esto a 3600 en produccion, IMPORTANTE
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        /// Valores por defecto para el remote config, siempre deben existir estos archivos en el server
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        return remoteConfig
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(
        remoteConfig: FirebaseRemoteConfig,
        gson: Gson
    ): RemoteConfigRepository {
        return RemoteConfigRepositoryImpl(remoteConfig, gson)
    }


    // --- USE CASES ---

    @Provides
    @Singleton
    fun provideGetTaxesUseCase(repository: TaxRepository): GetTaxesUseCase {
        return GetTaxesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateTransactionUseCase(repository: TaxRepository): CreateTransactionUseCase {
        return CreateTransactionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDownloadInvoiceUseCase(@ApplicationContext context: Context, repository: TaxRepository, taxApiService: TaxApiService,): DownloadInvoiceUseCase {
        return DownloadInvoiceUseCase(repository, context, taxApiService)
    }

    @Provides
    @Singleton
    fun provideGetVenuesUseCase(repository: VenueRepository): GetVenuesUseCase {
        return GetVenuesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateReservationUseCase(repository: VenueRepository): CreateReservationUseCase {
        return CreateReservationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }



}