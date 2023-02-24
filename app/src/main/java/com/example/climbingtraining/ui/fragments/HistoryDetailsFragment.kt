package com.example.climbingtraining.ui.fragments

import android.opengl.Visibility
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
import com.example.climbingtraining.models.GripType
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import com.example.climbingtraining.utils.SwipeToDeleteCallback
import java.text.SimpleDateFormat
import java.util.*

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
        val sdf = SimpleDateFormat(getString(R.string.date_format), Locale.US)
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

        if (details.gripType == GripType.UNDEFINED && details.additionalWeight <= 0){
            showAddDetailsMessage()
        } else {
            when (details.gripType) {
                GripType.EDGE -> showDetailsForEdge()
                GripType.SLOPER -> showDetailsForSloper()
                GripType.ONE_FINGER -> showDetailsForEdge()
                GripType.TWO_FINGER -> showDetailsForEdge()
                GripType.THREE_FINGER -> showDetailsForEdge()
                else -> showDefaultDetails()
            }
            if (details.additionalWeight > 0 ) {
                binding.llAdditionalWeight.visibility = View.VISIBLE
            }
            else {
                binding.llAdditionalWeight.visibility = View.GONE
            }

        }

    }

    private fun showAddDetailsMessage() {
        binding.apply {
            tvMessage.visibility = View.VISIBLE
            tvMessage.text = getString(R.string.history_add_details)
            llEdgeSize.visibility = View.GONE
            llSlopeAngle.visibility = View.GONE
            llCrimpType.visibility = View.GONE
            llGripType.visibility = View.GONE
            llAdditionalWeight.visibility = View.GONE
        }
    }

    private fun showDefaultDetails(){
        binding.apply{
            llEdgeSize.visibility = View.GONE
            llSlopeAngle.visibility = View.GONE
            llCrimpType.visibility = View.GONE
            llGripType.visibility = View.GONE
            tvMessage.visibility = View.GONE
        }

    }

    private fun showDetailsForEdge(){
        binding.apply{
            llEdgeSize.visibility = View.VISIBLE
            llSlopeAngle.visibility = View.GONE
            llCrimpType.visibility = View.VISIBLE
            tvMessage.visibility = View.GONE
        }
    }
    private fun showDetailsForSloper(){
        binding.apply{
            llEdgeSize.visibility = View.GONE
            llSlopeAngle.visibility = View.VISIBLE
            llCrimpType.visibility = View.GONE
            tvMessage.visibility = View.GONE
        }
    }



}