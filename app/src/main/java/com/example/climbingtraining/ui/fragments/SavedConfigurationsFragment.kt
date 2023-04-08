package com.example.climbingtraining.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.FragmentSavedConfigurationsBinding
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.adapters.SavedConfigsAdapter
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class SavedConfigurationsFragment : Fragment(R.layout.fragment_saved_configurations) {

    private lateinit var binding: FragmentSavedConfigurationsBinding
    private lateinit var viewModel: HangboardViewModel
    private lateinit var adapter: SavedConfigsAdapter
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSavedConfigurationsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel
        navController = findNavController()
        adapter = SavedConfigsAdapter(arrayListOf(), viewModel, navController)
        initializeUI()
        initializeObservers()
        viewModel.onSavedConfigsReady()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeObservers() {
        viewModel.savedConfigs.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvConfigs.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
            } else {
                binding.rvConfigs.visibility = View.VISIBLE
                binding.tvMessage.visibility = View.GONE
                adapter.updateList(it)
            }
        }
    }

    private fun initializeUI() {
        binding.rvConfigs.adapter = adapter
        binding.rvConfigs.layoutManager = LinearLayoutManager(activity)
        binding.fabNewConfig.setOnClickListener {
            navController.navigate(R.id.action_savedConfigurationsFragment_to_addNewHangboardFragment)
        }
    }


}