package com.isetr.tp5app.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Etudiant(
    @SerializedName("cin")
    var id: Int,

    @SerializedName("mail")
    var email: String,

    @SerializedName("classe")
    var classe: String,

    @SerializedName("prenom")
    var genre: String
) : Serializable
