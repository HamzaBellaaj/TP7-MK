package com.isetr.tp5app.repository

import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.data.RetrofitInstance
import retrofit2.Response

class EtudiantRepository {
    suspend fun getEtudiants(): Response<List<Etudiant>> {
        return RetrofitInstance.api.getEtudiants()
    }

    suspend fun addEtudiant(etudiant: Etudiant): Response<Etudiant> {
        return RetrofitInstance.api.addEtudiant(etudiant)
    }

    suspend fun deleteEtudiant(cin: Int): Response<Void> {
        return RetrofitInstance.api.deleteEtudiant(cin)
    }

    suspend fun updateEtudiant(etudiant: Etudiant): Response<Void> {
        return RetrofitInstance.api.updateEtudiant(etudiant)
    }
}
