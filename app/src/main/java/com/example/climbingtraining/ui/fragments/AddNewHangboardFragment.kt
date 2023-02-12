package com.example.climbingtraining.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.FragmentAddNewHangboardBinding
import com.example.climbingtraining.model.SimpleHangboard
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class AddNewHangboardFragment : Fragment(R.layout.fragment_add_new_hangboard){

    private lateinit var binding: FragmentAddNewHangboardBinding
    lateinit var viewModel: HangboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentAddNewHangboardBinding.inflate(inflater,container,false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel
        initializeUI()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initializeUI() {
        binding.btnSet.setOnClickListener { setHangboard() }
    }

    private fun setHangboard() {
        val hangboardConfig = SimpleHangboard(
            prepareTime = 4000L,
            hangTime = binding.etHangTime.text.toString().toLong() * 1000,
            restTime = binding.etRestTime.text.toString().toLong() * 1000,
            pauseTime = binding.etPauseTime.text.toString().toLong() * 1000,
            numberOfSets = binding.etSets.text.toString().toInt(),
            numberOfRepeats = binding.etRounds.text.toString().toInt()
        )
        viewModel.setHangboard(hangboardConfig)
    }



}