package com.tramites1cero1.tramiappquibdo.ui.screen.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.data.model.CreateUserDTO
import com.tramites1cero1.tramiappquibdo.data.network.GoogleApiService
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

sealed interface LoginEvent {
    data object NavigateToEmailLogin : LoginEvent
    data object NavigateToRegister : LoginEvent
    data object StartGoogleLogin : LoginEvent
    data object ContinueAsGuest : LoginEvent
    data class RequestAdditionalPermissions(val intent: Intent) : LoginEvent
}

@HiltViewModel
class LoginOptionsViewModel @Inject constructor(
    private val googleApiService: GoogleApiService,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _birthDate = MutableStateFlow<String?>(null)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // Configuración de Google Sign-In
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .requestScopes(
            Scope("https://www.googleapis.com/auth/userinfo.profile"),
            Scope("https://www.googleapis.com/auth/user.birthday.read")
        )
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Manejar resultado de Google Sign-In
    suspend fun handleSignInResult(intentData: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intentData)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account)
            } else {
                _error.value = "No se pudo obtener la cuenta de Google."
                Log.e("LoginOptionsViewModel", "No se pudo obtener la cuenta de Google.")
            }
        } catch (e: ApiException) {
            _error.value = "Error Google Sign-In: ${e.statusCode} - ${e.localizedMessage}"
            Log.e("LoginOptionsViewModel", "Error Google Sign-In: ${e.statusCode} - ${e.localizedMessage}")
        } catch (e: Exception) {
            _error.value = "Error inesperado Google Sign-In: ${e.localizedMessage}"
            Log.e("LoginOptionsViewModel", "Error inesperado Google Sign-In: ${e.localizedMessage}")
        }
    }

    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        _loading.value = true
        try {
                val idToken = account.idToken
                if (idToken == null) {
                    _loading.value = false
                    _error.value = "IdToken nulo, no se puede autenticar."
                    Log.d("LoginOptionsViewModel", "IdToken nulo, no se puede autenticar.")
                    return
                }

                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = signInWithCredentialAwait(credential)

                _loading.value = false

                if (authResult.user != null) {
                    val firebaseUser = auth.currentUser
                    val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

                    if (isNewUser) {
                        firebaseUser?.email?.let { email ->
                            viewModelScope.launch {
                                var user = authRepository.getUserInformationByEmail(email)
                                if (user.isSuccess) {
                                    _message.value = "Este usuario ya está registrado. Usa otra cuenta para continuar."
                                    signOut()
                                    delay(2000)
                                    _eventFlow.emit(LoginEvent.NavigateToEmailLogin)
                                }else{

                                    fetchGoogleProfile(account)
                                }


                            }

                        }


                    } else {
                        // Usuario existente: obtener datos desde tu backend por email
                        firebaseUser?.email?.let { email ->
                            viewModelScope.launch {
                                authRepository.getUserInformationByEmail(email)
                                    .onSuccess { user ->
                                        // Marcar usuario online / guardar sesión
                                        authRepository.getOutUser(user.id, true)
                                        val updatedUser = user.copy(loginStatus = true)
                                        authRepository.saveUserSession(updatedUser)
                                        Log.d("LoginOptionsViewModel", "Usuario guardado: $updatedUser")
                                        _eventFlow.emit(LoginEvent.ContinueAsGuest)
                                    }
                                    .onFailure { ex ->
                                        Log.e("LoginOptionsViewModel", "Error obteniendo datos del usuario", ex)
                                    }
                            }
                        } ?: run {
                            // No hay email en firebaseUser
                            Log.d("LoginOptionsViewModel", "No se encontró el correo del usuario autenticado.")
                        }
                    }
                } else {
                    Log.d("LoginOptionsViewModel", "Autenticación con Firebase falló.")                }
    }catch (e: Exception) {
        _loading.value = false

        Log.e("LoginOptionsViewModel", "Error firebaseAuthWithGoogle", e)
    }
    }


    private suspend fun signInWithCredentialAwait(
        credential: com.google.firebase.auth.AuthCredential
    ) = suspendCancellableCoroutine<com.google.firebase.auth.AuthResult> { cont ->
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (!cont.isCompleted && result != null) {
                        cont.resume(result, null)
                    }
                } else {
                    val ex = task.exception ?: Exception("Authentication failed")
                    if (!cont.isCompleted) {
                        cont.resumeWithException(ex)
                    }
                }
            }
    }

    private suspend fun fetchGoogleProfile(account: GoogleSignInAccount) {
        val token = getAccessToken(account)
        if (token != null) {
            try {
                val profile = googleApiService.getProfile("Bearer $token")
                val date = profile.birthdays?.firstOrNull()?.date
                val birth = if (date != null) "%04d-%02d-%02d".format(date.year, date.month, date.day) else null
                _birthDate.value = birth

                val userGoogle = CreateUserDTO(
                    firstName = profile.names?.firstOrNull()?.givenName.orEmpty(),
                    lastName = profile.names?.firstOrNull()?.familyName.orEmpty(),
                    email = profile.emailAddresses?.firstOrNull()?.value.orEmpty(),
                    phoneNumber = profile.phoneNumbers?.firstOrNull()?.value.orEmpty(),
                    birthDate = birth.orEmpty()
                )

                authRepository.saveRegistrationDraft(userGoogle)
                _eventFlow.emit(LoginEvent.NavigateToRegister)
            } catch (e: Exception) {
                _error.value = "Error al traer perfil: ${e.message}"
                Log.e("LoginOptionsViewModel", "Error al traer perfil: ${e.message}")
            }
        } else {
            _error.value = "AccessToken nulo, no se pudo autenticar con Google"
            Log.e("LoginOptionsViewModel", "AccessToken nulo, no se pudo autenticar con Google")
        }
    }

    private suspend fun getAccessToken(account: GoogleSignInAccount): String? = withContext(Dispatchers.IO) {
        val acct = account.account
        if (acct == null) {
            _error.value = "Cuenta de Google nula, no se pudo obtener token."
            Log.e("LoginOptionsViewModel", "Cuenta de Google nula, no se pudo obtener token.")
            return@withContext null
        }

        try {
            val scope = "oauth2:profile email https://www.googleapis.com/auth/user.birthday.read"
            GoogleAuthUtil.getToken(context, acct, scope)
        } catch (e: UserRecoverableAuthException) {
            _error.value = "Se necesitan permisos adicionales."
            Log.e("LoginOptionsViewModel", "Se necesitan permisos adicionales.")
            e.intent?.let { intent ->
                viewModelScope.launch {
                    _eventFlow.emit(LoginEvent.RequestAdditionalPermissions(intent))
                }
            }
            null
        } catch (e: Exception) {
            _error.value = "Error al obtener accessToken: ${e.message}"
            Log.e("LoginOptionsViewModel", "Error al obtener accessToken: ${e.message}")

            null
        }
    }

    fun onEmailLoginClicked() = viewModelScope.launch { _eventFlow.emit(LoginEvent.NavigateToEmailLogin) }
    fun onRegisterClicked() = viewModelScope.launch { _eventFlow.emit(LoginEvent.NavigateToRegister) }
    fun onContinueAsGuestClicked() = viewModelScope.launch { _eventFlow.emit(LoginEvent.ContinueAsGuest) }

    fun signOut() {
        viewModelScope.launch {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient.signOut()

        }
    }
}
