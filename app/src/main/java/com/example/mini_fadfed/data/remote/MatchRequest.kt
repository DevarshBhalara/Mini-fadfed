package com.example.mini_fadfed.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MatchRequest(
    val algo: String,
    val segment: String
//    val gender: GenderReq,
//    val country: List<String>
): Parcelable

enum class GenderReq {
    MALE,
    FEMALE,
    BOTH
}