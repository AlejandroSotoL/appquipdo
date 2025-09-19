package com.tramites1cero1.centralizacion.ui.navigation

import java.net.URLEncoder

object AppRoutes {
    const val INITIAL_NAV_GRAPH = "initialNavGraph"
    const val MAIN_NAV_GRAPH = "mainNavGraph"
    const val SIGNUP_NAV_GRAPH = "signUpNavGraph"

    const val WELCOMESCREEN = "WelcomeScreen"
    const val SELECT_MUN_SCREEN = "SelectMunScreen"

    const val SIGNUP_STEPONE = "SignUpStepOne"
    const val SIGNUP_STEPTWO = "SignUpStepTwo"
    const val SIGNUP_STEPTHREE = "SignUpStepThree"

    const val MAINSCREEN = "Main"

    const val NEWS_NAV_GRAPH = "newsNavGraph"
    const val NEWS_SCREEN = "NewsScreen"
    const val NEWS_DETAILS_SCREEN = "NewsDetailsScreen"

    const val PUBLIC_SERVICE_SCREEN = "PublicServiceScreen"
    const val PUBLIC_SERVICE_FORM = "PublicServiceForm"
    const val PUBLIC_SERVICE_NAV_GRAPH = "publicServiceNavGraph"

    const val COURSES_SCREEN = "CoursesScreen/{getUrl}/{postUrl}"
    const val VENUES_SCREEN = "VenuesScreen/{getUrl}/{postUrlReservation}/{postUrlCalendar}"



    const val LOGIN_OPTIONS_SCREEN = "LoginOptionsScreen"

    //-----------------HISTORY PAY--------------------
    const val HISTORY_PAY_SCREEN = "HistoryPayScreen"

    //-----------------CONFIGURATIONS USER--------------------
    const val CONFIGURATIONS_USER_SCREEN = "ConfigurationsUserScreen"
    const val CHANGE_ONLY_PASSWORD = "ChangePasswordScreen"
    //-----------------RECOVERY PASSWORD BY FORGET--------------------
    const val RECOVERY_PASSWORD = "UserSettingsScreen"

    const val PAYMENTS_NAV_GRAPH = "paymentsNavGraph"
    const val TAX_QUERY_SCREEN = "TaxQueryScreen/{entityCode}/{queryFieldsJson}/{taxId}/{title}/{dataPolicyUrl}/{privacyPolicyUrl}"
    const val TAX_RESULTS_SCREEN = "TaxResultsScreen"
    const val PSV_SCREEN = "psv_screen/{taxId}/{taxName}"


    //-----------------PQRDS SCREENS--------------------

    const val PQRDS_NAV_GRAPH = "PqrdsNavGraph"
    const val PQRDS_CHOICE_SCREEN = "PqrdsChoiceScreen"
    const val PQRDS_ANONIMAS = "PqrdsAnonimasScreen"
    const val PQRDS_IDENTIFICACION = "PqrdsIdentificacionScreen"
    const val PQRDS_IDENTIFICACION_PASO_1 = "PqrdsIdentificacionPaso1"
    const val PQRDS_IDENTIFICACION_PASO_2 = "PqrdsIdentificacionPaso2"
    const val PQRDS_IDENTIFICACION_PASO_3 = "PqrdsIdentificacionPaso3"
}

