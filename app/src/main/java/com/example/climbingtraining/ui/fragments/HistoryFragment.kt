package com.example.climbingtraining.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.R
import com.example.climbingtraining.ui.adapters.HistoryAdapter
import com.example.climbingtraining.databinding.FragmentHistoryBinding
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import com.example.climbingtraining.utils.SwipeToDeleteCallback
import com.example.climbingtraining.utils.SwipeToEditCallback

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

        val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHistory.adapter as HistoryAdapter
                adapter.deleteRecord(viewHolder.adapterPosition)
                viewModel.onHistoryReady()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvHistory)

        val editSwipeHandler = object : SwipeToEditCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvHistory.adapter as HistoryAdapter
                adapter.openEditDetails(viewHolder.adapterPosition)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvHistory)

        binding.fabNewHistoryRecord.setOnClickListener{
            viewModel.setHistoryEditFlag(true)
        }

    }

}