package com.isetr.tp5app.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.repository.EtudiantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EtudiantViewModel(
    private val repository: EtudiantRepository = EtudiantRepository()
) : ViewModel() {

    private val _etudiants = MutableLiveData<MutableList<Etudiant>>()
    private val _setudiant = MutableLiveData<Etudiant>()
    private val _addResult = MutableLiveData<Etudiant?>()
    private val _errorMessages = MutableLiveData<String?>()

    val errorMessages: LiveData<String?> = _errorMessages
    val etudiants: LiveData<MutableList<Etudiant>> = _etudiants
    val setudiants: LiveData<Etudiant> = _setudiant
    val addResult: LiveData<Etudiant?> = _addResult

    init {
        getAllEtudiants()
    }

    fun addEtudiant(etudiant: Etudiant) {
        addNewEtudiant(etudiant)
    }

    fun selectEtudiant(etudiant: Etudiant) {
        _setudiant.value = etudiant
    }

    private fun getAllEtudiants() {
        _errorMessages.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getEtudiants()
                val errorMsg = "Erreur de serveur: ${response.code()}"
                if (response.isSuccessful && response.body() != null) {
                    val etudiantsList = response.body()!!
                    if (etudiantsList.isNotEmpty()) {
                        Log.d("API_CALL", "liste étudiant trouvée.")
                        _etudiants.postValue(etudiantsList.toMutableList())
                        _errorMessages.postValue(null)
                    } else {
                        Log.d("API_CALL", "Aucun étudiant trouvé.")
                        _etudiants.postValue(mutableListOf())
                        _errorMessages.postValue("Aucun étudiant trouvé.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    _errorMessages.postValue("$errorMsg - $errorBody")
                    _etudiants.postValue(mutableListOf())
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur de connexion. Vérifiez l'URL de l'API."
                _errorMessages.postValue(errorMsg)
                _etudiants.postValue(mutableListOf())
                Log.e("API_CALL", "Erreur réseau/parsing: ${e.message}", e)
            }
        }
    }

    fun addNewEtudiant(newEtudiant: Etudiant) {
        _errorMessages.postValue(null)
        _addResult.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.addEtudiant(newEtudiant)
                if (response.isSuccessful && response.body() != null) {
                    val createdEtudiant = response.body()
                    _addResult.postValue(createdEtudiant)
                    getAllEtudiants()
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val errorMsg = "Erreur ${response.code()}: $errorBody"
                    _errorMessages.postValue(errorMsg)
                    _addResult.postValue(null)
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur réseau lors de l'ajout: ${e.message}"
                _errorMessages.postValue(errorMsg)
                _addResult.postValue(null)
                Log.e("API_CALL", "Erreur réseau/parsing (POST): ${e.message}", e)
            }
        }
    }

    fun deleteEtudiant(cin: Int) {
        _errorMessages.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.deleteEtudiant(cin)
                if (response.isSuccessful) {
                    getAllEtudiants()
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    _errorMessages.postValue("Erreur suppression (${response.code()}): $errorBody")
                }
            } catch (e: Exception) {
                _errorMessages.postValue("Erreur réseau lors de la suppression: ${e.message}")
            }
        }
    }

    fun updateEtudiant(etudiant: Etudiant) {
        _errorMessages.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.updateEtudiant(etudiant)
                if (response.isSuccessful) {
                    getAllEtudiants()
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    _errorMessages.postValue("Erreur mise à jour (${response.code()}): $errorBody")
                }
            } catch (e: Exception) {
                _errorMessages.postValue("Erreur réseau lors de la mise à jour: ${e.message}")
            }
        }
    }
}
