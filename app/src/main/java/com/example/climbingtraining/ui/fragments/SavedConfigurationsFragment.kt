package com.example.climbingtraining.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.R
import com.example.climbingtraining.adapter.SavedConfigsAdapter
import com.example.climbingtraining.databinding.FragmentSavedConfigurationsBinding
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class SavedConfigurationsFragment : Fragment(R.layout.fragment_saved_configurations){

    private lateinit var binding: FragmentSavedConfigurationsBinding
    private lateinit var viewModel: HangboardViewModel
    private val adapter = SavedConfigsAdapter(arrayListOf())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSavedConfigurationsBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel
        initializeUI()
        initializeObservers()
        viewModel.onSavedConfigsReady()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeObservers() {
        viewModel.savedConfigs.observe(viewLifecycleOwner){adapter.updateList(it)}
    }

    private fun initializeUI() {
        binding.rvConfigs.adapter = adapter
        binding.rvConfigs.layoutManager = LinearLayoutManager(activity)
    }
}