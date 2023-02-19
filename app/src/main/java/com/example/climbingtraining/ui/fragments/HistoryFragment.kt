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
import com.example.climbingtraining.adapter.HistoryAdapter
import com.example.climbingtraining.adapter.SavedConfigsAdapter
import com.example.climbingtraining.databinding.FragmentHistoryBinding
import com.example.climbingtraining.databinding.FragmentSavedConfigurationsBinding
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class HistoryFragment : Fragment(R.layout.fragment_history){

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var viewModel: HangboardViewModel
    private lateinit var adapter : HistoryAdapter
    private lateinit var navController : NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHistoryBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel
        navController = findNavController()
        adapter = HistoryAdapter(arrayListOf(),viewModel, navController)
        initializeUI()
        initializeObservers()
        viewModel.onHistoryReady()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeObservers() {
        viewModel.history.observe(viewLifecycleOwner){adapter.updateList(it)}
    }

    private fun initializeUI() {
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(activity)
    }

}