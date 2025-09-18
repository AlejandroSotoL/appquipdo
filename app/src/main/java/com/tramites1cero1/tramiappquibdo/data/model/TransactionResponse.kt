package com.tramites1cero1.tramiappquibdo.data.model

data class TransactionResponse(
    val isSuccess: Boolean,
    val message: Any,
    val result: Result,
    val state: Int
)
