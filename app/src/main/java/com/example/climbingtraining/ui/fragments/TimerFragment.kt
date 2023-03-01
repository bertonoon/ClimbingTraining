package com.example.climbingtraining.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.climbingtraining.databinding.DialogCustomQuestionBinding
import com.example.climbingtraining.databinding.FragmentTimerBinding
import com.example.climbingtraining.models.ExerciseState
import com.example.climbingtraining.models.RunState
import com.example.climbingtraining.models.SingleHangboard
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class TimerFragment : Fragment(){

    private lateinit var binding: FragmentTimerBinding
    lateinit var viewModel: HangboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentTimerBinding.inflate(inflater,container,false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel
        initializeUI()
        initializeObservers()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initializeObservers() {
        viewModel.currentHangboard.observe(viewLifecycleOwner){onCurrentHangboardChange(it)}
        viewModel.currentTimeToFinish.observe(viewLifecycleOwner){onCurrentTimeToFinish(it)}
        viewModel.currentHangboardState.observe(viewLifecycleOwner){
            if (viewModel.runState.value != RunState.UNINITIALIZED ) {
                binding.tvCurrentState.visibility = View.VISIBLE
                if ( it == ExerciseState.INACTIVE )
                    binding.tvCurrentState.text = "WAITING FOR START"
                else
                    binding.tvCurrentState.text = it.toString()
            } else
                binding.tvCurrentState.visibility = View.INVISIBLE

        }
        viewModel.runState.observe(viewLifecycleOwner){
            onRunStateChange()
        }
        viewModel.repeatsToFinish.observe(viewLifecycleOwner){
            binding.tvLeftRepeats.text = it.toString()
            viewModel.currentHangboard.value?.let { it1 ->
                setupRepeatsProgressBar(it,
                    it1.numberOfRepeats)
            }
        }
        viewModel.setsToFinish.observe(viewLifecycleOwner){
            binding.tvLeftSets.text = it.toString()
            viewModel.currentHangboard.value?.let { it1 ->
                setupSetsProgressBar(it,
                    it1.numberOfSets)
            }
        }
    }
    private fun initializeUI() {
        binding.btnStart.setOnClickListener { viewModel.onStart() }
        binding.btnStopReset.setOnClickListener { onStopResetButton() }
    }

    private fun setupTimeProgressBar(timeToFinish: Long, maxTime: Long){
        binding.pbProgressBar.max = maxTime.toInt()
        binding.pbProgressBar.progress = timeToFinish.toInt()
    }
    private fun setupSetsProgressBar(setsToFinish: Int,maxSets :Int){
        binding.pbSets.max = maxSets
        binding.pbSets.progress = setsToFinish
    }
    private fun setupRepeatsProgressBar(repeatsToFinish: Int,maxRepeats :Int){
        binding.pbRepeats.max = maxRepeats
        binding.pbRepeats.progress = repeatsToFinish
    }
    private fun onRunStateChange() {
        when (viewModel.runState.value) {
            RunState.ACTIVE -> {
                binding.btnStopReset.text = "STOP"
            }
            RunState.STOPPED -> {
                binding.btnStopReset.text = "RESET"
            }
            RunState.INITIALIZED -> {
                binding.btnStopReset.text = "STOP"
                setupTimeProgressBar(1,1)
                setupRepeatsProgressBar(1,1)
                setupSetsProgressBar(1,1)
            }
            RunState.FINISHED -> {
                showDialogForSave()
            }
            else -> {
                Log.e("UnknownState","Unknown state of viewModel.runState")
            }
        }
    }
    private fun onCurrentTimeToFinish(timeToFinish: Long) {
        binding.tvMainTimer.text = String.format("%.1f",timeToFinish.toFloat()/1000)
        setupTimeProgressBar(timeToFinish,
            when(viewModel.currentHangboardState.value){
                ExerciseState.INACTIVE -> 0
                ExerciseState.PREPARE -> viewModel.currentHangboard.value!!.prepareTime
                ExerciseState.HANG -> viewModel.currentHangboard.value!!.hangTime
                ExerciseState.REST -> viewModel.currentHangboard.value!!.restTime
                ExerciseState.PAUSE -> viewModel.currentHangboard.value!!.pauseTime
                null -> 0
            }
        )
    }
    private fun onCurrentHangboardChange(hangboardTimes: SingleHangboard) {
        binding.tvHangTime.text = String.format("%.0f",hangboardTimes.hangTime.toFloat()/1000)
        binding.tvPauseTime.text = String.format("%.0f",hangboardTimes.pauseTime.toFloat()/1000)
        binding.tvRoundsToEnd.text = hangboardTimes.numberOfRepeats.toString()
        binding.tvRestTime.text = String.format("%.0f",hangboardTimes.restTime.toFloat()/1000)
        binding.tvSetsToEnd.text = hangboardTimes.numberOfSets.toString()
    }
    private fun onStopResetButton(){
        when (viewModel.runState.value) {
            RunState.ACTIVE -> {
                viewModel.onStop()
            }
            RunState.STOPPED -> {
                showDialogForSave()
                viewModel.onReset()
            }
            else -> {
                Log.e("UnknownState","Unknown state of viewModel.runState")
            }
        }
    }


    private fun showDialogForSave() {
        val customDialog = Dialog(requireContext())
        val dialogBinding  = DialogCustomQuestionBinding.inflate(layoutInflater)
        dialogBinding.tvDialog.text = "Save training?"
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener{
            viewModel.saveCurrentHangboard()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            viewModel.reload()
            customDialog.dismiss()
        }
        customDialog.setOnCancelListener{
            viewModel.reload()
            customDialog.dismiss()
        }


        customDialog.show()
    }

}


