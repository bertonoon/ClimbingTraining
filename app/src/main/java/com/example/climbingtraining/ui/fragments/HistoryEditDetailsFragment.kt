package com.example.climbingtraining.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.FragmentHistoryEditDetailsBinding
import com.example.climbingtraining.models.CrimpType
import com.example.climbingtraining.models.GripType
import com.example.climbingtraining.models.SingleHangboard
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.activities.HangboardActivity
import com.example.climbingtraining.ui.viewModels.HangboardViewModel



class HistoryEditDetailsFragment : Fragment(R.layout.fragment_history_edit_details) {

    private lateinit var binding: FragmentHistoryEditDetailsBinding
    private lateinit var viewModel: HangboardViewModel
    private lateinit var navController : NavController
    private lateinit var oldHangboardHistoryModel : SingleHangboardHistoryModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentHistoryEditDetailsBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = (activity as HangboardActivity).viewModel

        oldHangboardHistoryModel = if (viewModel.historyEditDetailsHangboard.value != null) {
            viewModel.historyEditDetailsHangboard.value!!
        } else {
            SingleHangboardHistoryModel()
        }
        setDetails(oldHangboardHistoryModel)
        navController = findNavController()
        initializeUI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeUI() {
        binding.btnClose.setOnClickListener { navController.navigateUp() }
        binding.etGripType.apply {
            isFocusable = false
            setOnClickListener { showGripTypeMenu() }
        }
        binding.etCrimpType.apply {
            isFocusable = false
            setOnClickListener { showCrimpTypeMenu() }
        }
        binding.btnSave.setOnClickListener {
            updateHistoryDetails()
            navController.navigateUp()
        }
    }

    private fun setDetails(details: SingleHangboardHistoryModel) {
        binding.apply {
            etHangTime.setText((details.hangboardType.hangTime/1000).toString())
            etRestTime.setText((details.hangboardType.restTime/1000).toString())
            etRepeats.setText((details.hangboardType.numberOfRepeats).toString())
            etPauseTime.setText((details.hangboardType.pauseTime/1000).toString())
            etSets.setText((details.hangboardType.numberOfSets).toString())
            etHangboardName.setText(details.hangboardType.name)
            etEdgeSize.setText(details.edgeSize.toString())
            etSlopeAngle.setText(details.slopeAngle.toString())
            etAdditionalWeight.setText(details.additionalWeight.toString())
            etGripType.setText(
                when(details.gripType){
                    GripType.ONE_FINGER -> getString(R.string.grip_menu_option_one_finger)
                    GripType.THREE_FINGER -> getString(R.string.grip_menu_option_three_fingers)
                    GripType.SLOPER -> getString(R.string.grip_menu_option_sloper)
                    GripType.EDGE -> getString(R.string.grip_menu_option_edge)
                    GripType.JUG -> getString(R.string.grip_menu_option_jug)
                    GripType.TWO_FINGER -> getString(R.string.grip_menu_option_two_fingers)
                    GripType.PINCH -> getString(R.string.grip_menu_option_pinch)
                    GripType.OTHER -> getString(R.string.grip_menu_option_other)
                    else -> getString(R.string.undefined)
            })
            etCrimpType.setText(
                when(details.crimpType){
                    CrimpType.CLOSED_CRIMP -> getString(R.string.crimp_menu_option_closed_crimp)
                    CrimpType.OPEN_CRIMP -> getString(R.string.crimp_menu_option_open_crimp)
                    CrimpType.OPEN_HAND -> getString(R.string.crimp_menu_option_open_hand)
                    else -> getString(R.string.undefined)
                })
            etNote.setText(details.notes)
        }
        showSuitableLayout()
    }

    private fun showGripTypeMenu() {
        val menuGripType = PopupMenu(requireContext(),binding.etGripType)
        menuGripType.menuInflater.inflate(R.menu.grip_type_menu,menuGripType.menu)
        menuGripType.setOnMenuItemClickListener{ item ->
            when(item.itemId){
                R.id.edgeOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_edge))
                R.id.sloperOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_sloper))
                R.id.jugOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_jug))
                R.id.oneFingerOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_one_finger))
                R.id.twoFingerOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_two_fingers))
                R.id.threeFingerOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_three_fingers))
                R.id.pinchOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_pinch))
                R.id.otherOption -> binding.etGripType.setText(getString(R.string.grip_menu_option_other))
                else -> binding.etGripType.setText(GripType.UNDEFINED.toString())
            }
            showSuitableLayout()
            true
        }
        menuGripType.show()
    }
    private fun showCrimpTypeMenu() {
        val menuCrimpType = PopupMenu(requireContext(),binding.etCrimpType)
        menuCrimpType.menuInflater.inflate(R.menu.crimp_type_menu,menuCrimpType.menu)
        menuCrimpType.setOnMenuItemClickListener{ item ->
            when(item.itemId){
                R.id.openHandOption -> binding.etCrimpType.setText(getString(R.string.crimp_menu_option_open_hand))
                R.id.openCrimpOption -> binding.etCrimpType.setText(getString(R.string.crimp_menu_option_open_crimp))
                R.id.closedCrimpOption -> binding.etCrimpType.setText(getString(R.string.crimp_menu_option_closed_crimp))
                else -> binding.etCrimpType.setText(CrimpType.UNDEFINED.toString())
            }
            true
        }
        menuCrimpType.show()
    }


    private fun showSuitableLayout(){
        var showEdgeOption = false
        var showSlopeAngleOption = false
        var showCrimpTypeOption = false

        when (binding.etGripType.text.toString()){
            getString(R.string.grip_menu_option_one_finger) -> {
                showEdgeOption = true
                showSlopeAngleOption = false
                showCrimpTypeOption = false
            }
            getString(R.string.grip_menu_option_three_fingers) -> {
                showEdgeOption = true
                showSlopeAngleOption = false
                showCrimpTypeOption = true
            }
            getString(R.string.grip_menu_option_sloper) -> {
                showEdgeOption = false
                showSlopeAngleOption = true
                showCrimpTypeOption = false
            }
            getString(R.string.grip_menu_option_edge) -> {
                showEdgeOption = true
                showSlopeAngleOption = false
                showCrimpTypeOption = true
            }
            getString(R.string.grip_menu_option_jug) -> {
                showEdgeOption = false
                showSlopeAngleOption = false
                showCrimpTypeOption = false
            }
            getString(R.string.grip_menu_option_two_fingers) -> {
                showEdgeOption = true
                showSlopeAngleOption = false
                showCrimpTypeOption = false
            }
            getString(R.string.grip_menu_option_pinch) -> {
                showEdgeOption = false
                showSlopeAngleOption = false
                showCrimpTypeOption = false
            }
            getString(R.string.grip_menu_option_other) -> {
                showEdgeOption = false
                showSlopeAngleOption = false
                showCrimpTypeOption = false
            }
            getString(R.string.undefined) -> {
                showEdgeOption = false
                showSlopeAngleOption = false
                showCrimpTypeOption = false
            }
        }

        if (showEdgeOption) binding.llEdgeSize.visibility = View.VISIBLE
        else binding.llEdgeSize.visibility = View.GONE

        if (showSlopeAngleOption) binding.llSlopeAngle.visibility = View.VISIBLE
        else binding.llSlopeAngle.visibility = View.GONE

        if (showCrimpTypeOption) binding.llCrimpType.visibility = View.VISIBLE
        else binding.llCrimpType.visibility = View.GONE

    }

    private fun updateHistoryDetails(){
        val newHangboardHistoryModel = getHistoryDetails()
        Log.i("NewHangboard",newHangboardHistoryModel.toString())
        viewModel.updateHistoryDetails(newHangboardHistoryModel)
    }

    private fun getHistoryDetails(): SingleHangboardHistoryModel {
        return SingleHangboardHistoryModel(
            id = oldHangboardHistoryModel.id,
            date = oldHangboardHistoryModel.date,
            hangboardType = getHangboardType(),
            notes = getNotes(),
            gripType = getGripType(),
            crimpType = getCrimpType(),
            edgeSize = getEdgeSize(),
            slopeAngle = getSlopeAngle(),
            additionalWeight = getAdditionalWeight()
        )
    }

    private fun getHangboardType() : SingleHangboard{
        return SingleHangboard(
            id = 0,
            prepareTime = oldHangboardHistoryModel.hangboardType.prepareTime,
            hangTime = getTimeFromEditText(binding.etHangTime),
            pauseTime = getTimeFromEditText(binding.etPauseTime),
            numberOfRepeats = getNumberOf(binding.etRepeats),
            restTime = getTimeFromEditText(binding.etRestTime),
            numberOfSets = getNumberOf(binding.etSets),
            name = getName()

        )
    }

    private fun getNumberOf(et : EditText) : Int {
        return et.text.toString().ifEmpty {
            "0"
        }.toInt()
    }
    private fun getName() : String{
        return binding.etHangboardName.text.toString().ifEmpty {
            ""
        }
    }
    private fun getTimeFromEditText(et : EditText) : Long{
        return et.text.toString().ifEmpty {
            "0"
        }.toLong() * 1000
    }
    private fun getNotes(): String {
        return binding.etNote.text.toString().ifEmpty {
            ""
        }
    }

    private fun getGripType(): GripType {
        return when(binding.etGripType.text.toString()){
            getString(R.string.grip_menu_option_edge) -> GripType.EDGE
            getString(R.string.grip_menu_option_sloper) -> GripType.SLOPER
            getString(R.string.grip_menu_option_jug) -> GripType.JUG
            getString(R.string.grip_menu_option_one_finger) -> GripType.ONE_FINGER
            getString(R.string.grip_menu_option_two_fingers) -> GripType.TWO_FINGER
            getString(R.string.grip_menu_option_three_fingers) -> GripType.THREE_FINGER
            getString(R.string.grip_menu_option_pinch) -> GripType.PINCH
            getString(R.string.grip_menu_option_other) -> GripType.OTHER
            else -> GripType.UNDEFINED
        }
    }

    private fun getCrimpType(): CrimpType {
        return when(binding.etCrimpType.text.toString()){
            getString(R.string.crimp_menu_option_open_hand) -> CrimpType.OPEN_HAND
            getString(R.string.crimp_menu_option_open_crimp) -> CrimpType.OPEN_CRIMP
            getString(R.string.crimp_menu_option_closed_crimp) -> CrimpType.CLOSED_CRIMP
            else -> CrimpType.UNDEFINED
        }
    }

    private fun getEdgeSize(): Int {
        return binding.etEdgeSize.text.toString().ifEmpty {
            "0"
        }.toInt()
    }

    private fun getSlopeAngle(): Int {
        return binding.etSlopeAngle.text.toString().ifEmpty {
            "0"
        }.toInt()
    }

    private fun getAdditionalWeight(): Float {
        return binding.etSlopeAngle.text.toString().ifEmpty {
            "0.0"
        }.toFloat()
    }


}