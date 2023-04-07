package com.example.climbingtraining.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.FragmentTrainingDetailsBinding
import com.example.climbingtraining.models.CrimpType
import com.example.climbingtraining.models.GripType
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryDetailsFragment : Fragment(R.layout.fragment_training_details) {

    private lateinit var binding: FragmentTrainingDetailsBinding
    private lateinit var viewModel: HangboardViewModel
    private lateinit var navController: NavController

    private lateinit var hangboardDetails: SingleHangboardHistoryModel;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentTrainingDetailsBinding.inflate(inflater, container, false)
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
        viewModel.historyDetailsHangboard.observe(viewLifecycleOwner) {
            hangboardDetails = it
            setDetails(it)
        }
    }

    private fun initializeUI() {
        binding.btnClose.setOnClickListener { navController.navigateUp() }
        binding.btnEdit.setOnClickListener { onEditButton() }
        binding.ivShare.setOnClickListener { onShareButton() }
    }

    private fun onShareButton() {
        val shareDetails: StringBuilder = StringBuilder()
        shareDetails.append("Training details \n")

        val times =
            buildString {
                append("H")
                append(hangboardDetails.hangboardType.hangTime/1000)
                append("s/R")
                append(hangboardDetails.hangboardType.restTime/1000)
                append("s x")
                append(hangboardDetails.hangboardType.numberOfRepeats)
                append("/P")
                append(hangboardDetails.hangboardType.pauseTime/1000)
                append("s x")
                append(hangboardDetails.hangboardType.numberOfSets)
            }
        shareDetails.append(times + "\n")

        if (hangboardDetails.gripType != GripType.UNDEFINED)
            shareDetails.append(hangboardDetails.gripType.toDisplayString() + "\n")
        if (hangboardDetails.gripType == GripType.EDGE ||
            hangboardDetails.gripType == GripType.ONE_FINGER ||
            hangboardDetails.gripType == GripType.TWO_FINGER ||
            hangboardDetails.gripType == GripType.THREE_FINGER
        ) {
            if (hangboardDetails.edgeSize > 0)
                shareDetails.append("Edge: ${hangboardDetails.edgeSize}mm\n")
            if (hangboardDetails.crimpType != CrimpType.UNDEFINED)
                shareDetails.append("Crimp: ${hangboardDetails.crimpType.toDisplayString()}\n")
        }

        if (hangboardDetails.gripType == GripType.SLOPER)
            if (hangboardDetails.slopeAngle > 0)
                shareDetails.append("Slope angle: ${hangboardDetails.slopeAngle}\n")

        if (hangboardDetails.additionalWeight > 0)
            shareDetails.append("Additional weight: ${hangboardDetails.additionalWeight}\n")

        if (hangboardDetails.intensity > 0)
            shareDetails.append("Training intensity: ${hangboardDetails.intensity}")


        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareDetails.toString())

        startActivity(Intent.createChooser(intent, "Share To:"))
    }

    private fun onEditButton() {
        viewModel.historyDetailsHangboard.value?.let { viewModel.setEditHistoryDetails(it) }
        navController.navigate(R.id.action_historyDetailsFragment_to_historyEditDetailsFragment)
    }

    @SuppressLint("SetTextI18n")
    private fun setDetails(details: SingleHangboardHistoryModel) {
        val sdf = SimpleDateFormat(getString(R.string.date_format), Locale.US)
        binding.apply {
            tvHangboardName.text = details.hangboardType.name
            tvDate.text = sdf.format(details.date)
            tvHangTime.text = (details.hangboardType.hangTime / 1000).toString()
            tvRestTime.text = (details.hangboardType.restTime / 1000).toString()
            tvRepeats.text = details.hangboardType.numberOfRepeats.toString()
            tvPauseTime.text = (details.hangboardType.pauseTime / 1000).toString()
            tvSets.text = details.hangboardType.numberOfSets.toString()
            tvGripType.text = showGripType(details.gripType)
            tvEdgeSize.text = "${details.edgeSize} mm"
            tvSlopeAngle.text = "${details.slopeAngle} °"
            tvCrimpType.text = showCrimpType(details.crimpType)
            tvAdditionalWeight.text = "${details.additionalWeight} kg"
            tvNotes.text = details.notes
            tvIntensity.text = "${details.intensity} %"
        }

        if (details.gripType == GripType.UNDEFINED && details.additionalWeight <= 0) {
            showAddDetailsMessage()
        } else {
            when (details.gripType) {
                GripType.EDGE -> showDetailsForEdge()
                GripType.SLOPER -> showDetailsForSloper()
                GripType.ONE_FINGER -> showDetailsForEdge()
                GripType.TWO_FINGER -> showDetailsForEdge()
                GripType.THREE_FINGER -> showDetailsForEdge()
                GripType.PINCH -> showDetailsForOthers()
                GripType.OTHER -> showDetailsForOthers()
                GripType.JUG -> showDetailsForOthers()
                else -> showDefaultDetails()
            }
            if (details.additionalWeight > 0) {
                binding.llAdditionalWeight.visibility = View.VISIBLE
            } else {
                binding.llAdditionalWeight.visibility = View.GONE
            }

            if (details.intensity > 0) {
                binding.llIntensity.visibility = View.VISIBLE
            } else {
                binding.llIntensity.visibility = View.GONE
            }

        }
        if (details.notes.isEmpty()) {
            binding.llNotes.visibility = View.GONE
        } else {
            binding.llNotes.visibility = View.VISIBLE
        }

    }


    private fun showCrimpType(crimpType: CrimpType): String {
        return when (crimpType) {
            CrimpType.CLOSED_CRIMP -> getString(R.string.crimp_menu_option_closed_crimp)
            CrimpType.OPEN_CRIMP -> getString(R.string.crimp_menu_option_open_crimp)
            CrimpType.OPEN_HAND -> getString(R.string.crimp_menu_option_open_hand)
            else -> getString(R.string.undefined)
        }
    }

    private fun showGripType(gripType: GripType): String {
        return when (gripType) {
            GripType.ONE_FINGER -> getString(R.string.grip_menu_option_one_finger)
            GripType.THREE_FINGER -> getString(R.string.grip_menu_option_three_fingers)
            GripType.SLOPER -> getString(R.string.grip_menu_option_sloper)
            GripType.EDGE -> getString(R.string.grip_menu_option_edge)
            GripType.JUG -> getString(R.string.grip_menu_option_jug)
            GripType.TWO_FINGER -> getString(R.string.grip_menu_option_two_fingers)
            GripType.PINCH -> getString(R.string.grip_menu_option_pinch)
            GripType.OTHER -> getString(R.string.grip_menu_option_other)
            else -> getString(R.string.undefined)
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

    private fun showDetailsForOthers() {
        binding.apply {
            llEdgeSize.visibility = View.GONE
            llSlopeAngle.visibility = View.GONE
            llCrimpType.visibility = View.GONE
            llGripType.visibility = View.VISIBLE
            tvMessage.visibility = View.GONE
        }
    }

    private fun showDefaultDetails() {
        binding.apply {
            llEdgeSize.visibility = View.GONE
            llSlopeAngle.visibility = View.GONE
            llCrimpType.visibility = View.GONE
            llGripType.visibility = View.VISIBLE
            tvMessage.visibility = View.GONE
        }

    }

    private fun showDetailsForEdge() {
        binding.apply {
            llEdgeSize.visibility = View.VISIBLE
            llGripType.visibility = View.VISIBLE
            llSlopeAngle.visibility = View.GONE
            llCrimpType.visibility = View.VISIBLE
            tvMessage.visibility = View.GONE
        }
    }

    private fun showDetailsForSloper() {
        binding.apply {
            llEdgeSize.visibility = View.GONE
            llGripType.visibility = View.VISIBLE
            llSlopeAngle.visibility = View.VISIBLE
            llCrimpType.visibility = View.GONE
            tvMessage.visibility = View.GONE
        }
    }


}