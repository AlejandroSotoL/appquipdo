package com.tramites1cero1.tramiappquibdo.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Municipality(
    val domain: String,
    val id: Int,
    val name: String,
    val isActive: Boolean
): Parcelable