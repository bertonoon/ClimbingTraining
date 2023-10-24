package com.example.climbingtraining.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.DialogCustomQuestionBinding
import com.example.climbingtraining.databinding.FragmentTimerBinding
import com.example.climbingtraining.models.ExerciseState
import com.example.climbingtraining.models.RunState
import com.example.climbingtraining.models.SingleHangboard
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import java.util.*


class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    lateinit var viewModel: HangboardViewModel
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentTimerBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        activity?.title = "CURRENT SETTING"

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HangboardActivity).viewModel
        initializeUI()
        initializeObservers()
        navController = findNavController()


    }

    private fun bindHangColors() {
        binding.tvCurrentState.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.hang_color
            )
        )
        binding.llTimer.background =
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_circular_hang_background
            )
        binding.flTimer.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.item_circular_color_accent_hang_border
        )


    }

    private fun bindBasicColors() {
        binding.tvCurrentState.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.basic_text_color
            )
        )
        binding.llTimer.background =
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_circular_accent_background
            )
        binding.flTimer.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.item_circular_color_accent_border
        )

    }


    private fun initializeObservers() {
        viewModel.currentHangboard.observe(viewLifecycleOwner) { onCurrentHangboardChange(it) }
        viewModel.currentTimeToFinish.observe(viewLifecycleOwner) { onCurrentTimeToFinish(it) }
        viewModel.currentHangboardState.observe(viewLifecycleOwner) {
            if (viewModel.runState.value != RunState.UNINITIALIZED) {
                binding.tvCurrentState.visibility = View.VISIBLE
                if (it == ExerciseState.INACTIVE) {
                    binding.tvCurrentState.text = getString(R.string.hangboardStateWaiting)
                    bindBasicColors()
                } else {
                    binding.tvCurrentState.text = it.toString().uppercase()
                    when (it) {
                        ExerciseState.HANG -> bindHangColors()
                        else -> bindBasicColors()
                    }
                }
            } else
                binding.tvCurrentState.visibility = View.INVISIBLE

        }
        viewModel.runState.observe(viewLifecycleOwner) {
            onRunStateChange()
        }
        viewModel.repeatsToFinish.observe(viewLifecycleOwner) {
            binding.tvLeftRepeats.text = it.toString()
            viewModel.currentHangboard.value?.let { it1 ->
                setupRepeatsProgressBar(
                    it,
                    it1.numberOfRepeats
                )
            }
        }
        viewModel.setsToFinish.observe(viewLifecycleOwner) {
            binding.tvLeftSets.text = it.toString()
            viewModel.currentHangboard.value?.let { it1 ->
                setupSetsProgressBar(
                    it,
                    it1.numberOfSets
                )
            }
        }

    }

    private fun initializeUI() {
        binding.btnStart.setOnClickListener { viewModel.onStart() }
        binding.btnStopReset.setOnClickListener { onStopResetButton() }
        binding.llHeaders.setOnClickListener {
            navController.navigate(R.id.action_timerFragment_to_addNewHangboardFragment)
        }
        binding.llTimes.setOnClickListener {
            navController.navigate(R.id.action_timerFragment_to_addNewHangboardFragment)
        }
    }

    private fun setupTimeProgressBar(timeToFinish: Long, maxTime: Long) {
        binding.pbProgressBar.max = maxTime.toInt()
        binding.pbProgressBar.progress = timeToFinish.toInt()
    }

    private fun setupSetsProgressBar(setsToFinish: Int, maxSets: Int) {
        binding.pbSets.max = maxSets
        binding.pbSets.progress = setsToFinish
    }

    private fun setupRepeatsProgressBar(repeatsToFinish: Int, maxRepeats: Int) {
        binding.pbRepeats.max = maxRepeats
        binding.pbRepeats.progress = repeatsToFinish
    }

    private fun onRunStateChange() {
        when (viewModel.runState.value) {
            RunState.ACTIVE -> {
                binding.btnStopReset.text = getString(R.string.ButtonStop)
            }
            RunState.STOPPED -> {
                binding.btnStopReset.text = getString(R.string.ButtonReset)
            }
            RunState.INITIALIZED -> {
                binding.btnStopReset.text = getString(R.string.ButtonStop)
                setupTimeProgressBar(1, 1)
                setupRepeatsProgressBar(1, 1)
                setupSetsProgressBar(1, 1)
            }
            RunState.FINISHED -> {
                showDialogForSave()
            }
            else -> {
                binding.btnStopReset.text = getString(R.string.ButtonStop)
            }
        }
    }

    private fun onCurrentTimeToFinish(timeToFinish: Long) {
        binding.tvMainTimer.text =
            String.format("%.1f", timeToFinish.toFloat() / 1000).replace(',', '.')
        setupTimeProgressBar(
            timeToFinish,
            when (viewModel.currentHangboardState.value) {
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
        binding.tvHangTime.text =
            String.format("%.0f", hangboardTimes.hangTime.toFloat() / 1000)
        binding.tvPauseTime.text =
            String.format("%.0f", hangboardTimes.pauseTime.toFloat() / 1000)
        binding.tvRoundsToEnd.text = hangboardTimes.numberOfRepeats.toString()
        binding.tvRestTime.text =
            String.format("%.0f", hangboardTimes.restTime.toFloat() / 1000)
        binding.tvSetsToEnd.text = hangboardTimes.numberOfSets.toString()
    }

    private fun onStopResetButton() {
        when (viewModel.runState.value) {
            RunState.ACTIVE -> {
                viewModel.onStop()
            }
            RunState.STOPPED -> {
                showDialogForSave()
                viewModel.onReset()
            }
            else -> {
                viewModel.onStop()
                // Log.e("UnknownState","Unknown state of viewModel.runState")
            }
        }
    }


    private fun showDialogForSave() {
        val customDialog = Dialog(requireContext())
        val dialogBinding = DialogCustomQuestionBinding.inflate(layoutInflater)
        dialogBinding.tvDialog.text = "Save training?"
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            viewModel.saveCurrentHangboard()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            viewModel.reload()
            customDialog.dismiss()
        }
        customDialog.setOnCancelListener {
            viewModel.reload()
            customDialog.dismiss()
        }
        customDialog.show()
    }

}


