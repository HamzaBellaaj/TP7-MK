package com.isetr.tp5app.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EtudiantApi {
    @GET(value = "api/etudiants")
    suspend fun getEtudiants(): Response<List<Etudiant>>

    @POST(value = "api/addEtudiant")
    suspend fun addEtudiant(@Body etudiant: Etudiant): Response<Etudiant>

    @DELETE(value = "api/delete/{cin}")
    suspend fun deleteEtudiant(@Path("cin") cin: Int): Response<Void>

    @PUT(value = "api/update")
    suspend fun updateEtudiant(@Body etudiant: Etudiant): Response<Void>
}
