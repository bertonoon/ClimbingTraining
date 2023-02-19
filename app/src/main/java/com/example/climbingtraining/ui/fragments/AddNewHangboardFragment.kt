package com.example.climbingtraining.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.FragmentAddNewHangboardBinding
import com.example.climbingtraining.model.SingleHangboard
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel


class AddNewHangboardFragment : Fragment(R.layout.fragment_add_new_hangboard){

    private lateinit var binding: FragmentAddNewHangboardBinding
    lateinit var viewModel: HangboardViewModel
    lateinit var navController: NavController


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
        navController = findNavController()
        binding.btnSet.setOnClickListener {
            setHangboard()
            navController.navigate(R.id.action_addNewHangboardFragment_to_timerFragment2)
        }
        binding.btnSave.setOnClickListener {
            saveHangboard()
            navController.navigate(R.id.action_addNewHangboardFragment_to_savedConfigurationsFragment2)
        }
        binding.btnSaveAndSet.setOnClickListener {
            saveHangboard()
            setHangboard()
            navController.navigate(R.id.action_addNewHangboardFragment_to_timerFragment2)
        }
        binding.btnCancel.setOnClickListener {
            navController.navigate(R.id.action_addNewHangboardFragment_to_savedConfigurationsFragment2)
        }

    }


    private fun getHangboardFromEditTexts() : SingleHangboard{
        return SingleHangboard(
            name = binding.etHangboardName.text.toString(),
            prepareTime = 5000L,
            hangTime = binding.etHangTime.text.toString().toLong() * 1000,
            restTime = binding.etRestTime.text.toString().toLong() * 1000,
            pauseTime = binding.etPauseTime.text.toString().toLong() * 1000,
            numberOfSets = binding.etSets.text.toString().toInt(),
            numberOfRepeats = binding.etRounds.text.toString().toInt()
        )
    }

    private fun setHangboard() {
        viewModel.setHangboard(getHangboardFromEditTexts())
        activity?.currentFocus?.let { view ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun saveHangboard(){
        viewModel.saveHangboard(getHangboardFromEditTexts())
    }



}