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
import com.example.climbingtraining.adapters.HistoryAdapter
import com.example.climbingtraining.adapters.SavedConfigsAdapter
import com.example.climbingtraining.databinding.FragmentSavedConfigurationsBinding
import com.example.climbingtraining.databinding.FragmentTrainingDetailsBinding
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import com.example.climbingtraining.utils.SwipeToDeleteCallback
import java.text.SimpleDateFormat

class HistoryDetailsFragment : Fragment(R.layout.fragment_training_details){

    private lateinit var binding: FragmentTrainingDetailsBinding
    private lateinit var viewModel: HangboardViewModel
    private lateinit var navController : NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentTrainingDetailsBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel
        navController = findNavController()
        initializeUI()
        initializeObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeObservers() {
        viewModel.historyDetailsHangboard.observe(viewLifecycleOwner){setDetails(it)}
    }

    private fun initializeUI() {
        binding.btnClose.setOnClickListener { navController.navigateUp() }
    }

    private fun setDetails(details: SingleHangboardHistoryModel){
        val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm")
        binding.apply {
            tvHangboardName.text = details.hangboardType.name
            tvDate.text = sdf.format(details.date)
            tvHangTime.text = (details.hangboardType.hangTime/1000).toString()
            tvRestTime.text = (details.hangboardType.restTime/1000).toString()
            tvRepeats.text = details.hangboardType.numberOfRepeats.toString()
            tvPauseTime.text = (details.hangboardType.pauseTime/1000).toString()
            tvSets.text = details.hangboardType.numberOfSets.toString()

            tvGripType.text = details.gripType.toString()
            tvEdgeSize.text = details.edgeSize.toString()
            tvSlopeAngle.text = details.slopeAngle.toString()
            tvCrimpType.text = details.crimpType.toString()
            tvAdditionalWeight.text = details.additionalWeight.toString()
        }
    }



}