package com.isetr.tp5app.ui.liste

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isetr.tp5app.R
import com.isetr.tp5app.databinding.FragmentListeBinding
import com.isetr.tp5app.repository.EtudiantRepository
import com.isetr.tp5app.viewModel.EtudiantViewModel
import com.isetr.tp5app.viewModel.EtudiantViewModelFactory

class ListeFragment : Fragment() {

    private val etudiantViewModel: EtudiantViewModel by activityViewModels {
        EtudiantViewModelFactory(EtudiantRepository())
    }
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!

    private lateinit var etudiantAdapter: EtudAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillRecyclerView()
        observeLiveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fillRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        etudiantAdapter = EtudAdapter(
            etudiants = mutableListOf(),
            onItemClicked = { etudiant ->
                etudiantViewModel.selectEtudiant(etudiant)
                findNavController().navigate(R.id.action_nav_liste_to_nav_details)
            },
            onDeleteClicked = { etudiant ->
                etudiantViewModel.deleteEtudiant(etudiant.id)
            },
            onUpdateClicked = { etudiant ->
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_modifier, null)
                val etMail = dialogView.findViewById<EditText>(R.id.mail)
                val etClasse = dialogView.findViewById<EditText>(R.id.classe)

                etMail.setText(etudiant.email)
                etClasse.setText(etudiant.classe)

                AlertDialog.Builder(requireContext())
                    .setTitle("Modifier les détails de ${etudiant.id}")
                    .setView(dialogView)
                    .setPositiveButton("Modifier") { _, _ ->
                        val updatedEtudiant = etudiant.copy(
                            email = etMail.text.toString(),
                            classe = etClasse.text.toString()
                        )
                        etudiantViewModel.updateEtudiant(updatedEtudiant)
                        Toast.makeText(requireContext(), "Étudiant modifié avec succès", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
            }
        )
        recyclerView.adapter = etudiantAdapter
    }

    private fun observeLiveData() {
        // Observation des données (RecyclerView)
        etudiantViewModel.etudiants.observe(viewLifecycleOwner, Observer { etudiants ->
            etudiantAdapter.updateList(etudiants ?: mutableListOf())
            // Si on a des données, on affiche la liste et on masque le texte d'erreur
            if (!etudiants.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.textError.visibility = View.GONE
            } else if (etudiantViewModel.errorMessages.value == null) {
                // Si la liste est vide, afficher le message "Aucun étudiant trouvé"
                binding.textError.text = "Aucun étudiant trouvé."
                binding.textError.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        })
        etudiantViewModel.errorMessages.observe(viewLifecycleOwner, Observer { errorMsg ->
            if (errorMsg != null) {
                binding.textError.text = errorMsg
                binding.textError.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                // Masquer le texte d'erreur si l'erreur a été effacée
                binding.textError.visibility = View.GONE
            }
        })
    }
}
