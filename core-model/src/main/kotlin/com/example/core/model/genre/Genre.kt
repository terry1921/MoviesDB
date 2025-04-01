package com.example.core.model.genre

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val id: Int,
    val name: String
) : Parcelable

@Parcelize
data class GenresResponse(
    val genres: List<Genre>
) : Parcelable

