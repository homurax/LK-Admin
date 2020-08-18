package org.light_novel.lkadmin.logic.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SimpleQuery(val page: Int = 1, val query: String) : Parcelable

@Parcelize
class SimpleTypeQuery(
    val query: String,
    @SerializedName("search_type") val searchType: Int?,
    val page: Int = 1
) : Parcelable

@Parcelize
class MultipleQuery(
    @SerializedName("datatype") val dataType: String,
    @SerializedName("qtype") val qType: String,
    val page: Int = 1,
    val query: String
) : Parcelable