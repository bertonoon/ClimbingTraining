package com.example.climbingtraining.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.FragmentAddNewHangboardBinding
import com.example.climbingtraining.models.SingleHangboard
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
        initializeObservers()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun initializeUI() {
        navController = findNavController()
        binding.btnSet.setOnClickListener {
            if (setHangboard())
                navController.navigate(R.id.action_addNewHangboardFragment_to_timerFragment2)
        }
        binding.btnSave.setOnClickListener {
            if (saveHangboard())
                navController.navigate(R.id.action_addNewHangboardFragment_to_savedConfigurationsFragment2)
        }
        binding.btnSaveAndSet.setOnClickListener {
            if (saveHangboard() && setHangboard())
                navController.navigate(R.id.action_addNewHangboardFragment_to_timerFragment2)
        }
        binding.btnCancel.setOnClickListener {
            viewModel.cancelEditing()
            navController.navigateUp()
        }
        binding.btnEdit.setOnClickListener {
            if (editHangboard()){
                navController.navigate(R.id.action_addNewHangboardFragment_to_savedConfigurationsFragment2)
            }
        }
        binding.btnEditAndSet.setOnClickListener {
            if (editHangboard() && setHangboard()){
                navController.navigate(R.id.action_addNewHangboardFragment_to_timerFragment2)
            }
        }

    }

    private fun initializeObservers() {
        viewModel.editedHangboard.observe(viewLifecycleOwner){
            if (it.id > 0) {
                showEditView()
                binding.etHangboardName.setText(it.name)
                binding.etSets.setText(it.numberOfSets.toString())
                binding.etRounds.setText(it.numberOfRepeats.toString())
                binding.etHangTime.setText((it.hangTime/1000).toString())
                binding.etRestTime.setText((it.restTime/1000).toString())
                binding.etPauseTime.setText((it.pauseTime/1000).toString())
            } else {
                showAddView()
            }
        }
    }

    private fun showAddView() {
        binding.tvAddSeries.visibility = View.VISIBLE
        binding.tvEditSeries.visibility = View.GONE
        binding.btnSave.visibility = View.VISIBLE
        binding.btnEdit.visibility = View.GONE
        binding.btnSaveAndSet.visibility = View.VISIBLE
        binding.btnEditAndSet.visibility = View.GONE
    }

    private fun showEditView() {
        binding.tvAddSeries.visibility = View.INVISIBLE
        binding.tvEditSeries.visibility = View.VISIBLE

        binding.btnSave.visibility = View.GONE
        binding.btnEdit.visibility = View.VISIBLE

        binding.btnSaveAndSet.visibility = View.GONE
        binding.btnEditAndSet.visibility = View.VISIBLE
    }

    private fun getHangboardFromEditTexts() : SingleHangboard{
        return SingleHangboard(
            id = viewModel.editedHangboard.value?.id ?: 0,
            name = binding.etHangboardName.text.toString(),
            prepareTime = 5000L,
            hangTime = binding.etHangTime.text.toString().toLong() * 1000,
            restTime = binding.etRestTime.text.toString().toLong() * 1000,
            pauseTime = binding.etPauseTime.text.toString().toLong() * 1000,
            numberOfSets = binding.etSets.text.toString().toInt(),
            numberOfRepeats = binding.etRounds.text.toString().toInt()
        )
    }

    private fun editHangboard() : Boolean{
        if (!validateData()) return false
        viewModel.editHangboard(getHangboardFromEditTexts())
        activity?.currentFocus?.let { view ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        return true
    }

    private fun setHangboard() : Boolean{
        if (!validateData()) return false
        viewModel.setHangboard(getHangboardFromEditTexts())
        activity?.currentFocus?.let { view ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        return true
    }
    private fun saveHangboard() : Boolean{
        if (!validateData()) return false
        viewModel.saveHangboard(getHangboardFromEditTexts())
        setHangboard()
        return true
    }

    private fun validateData() : Boolean{
        if(binding.etHangboardName.text.isNullOrEmpty()) {
            Toast.makeText(context,"Please enter name.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etHangboardName.text.length > 30) {
            Toast.makeText(context,"The name can contain up to 30 characters.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etHangboardName.text.toString().contains("\n")) {
            Toast.makeText(context,"The name cannot contain new line character.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etHangTime.text.isNullOrEmpty()) {
            Toast.makeText(context,"Please enter hang time.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etRestTime.text.isNullOrEmpty()) {
            Toast.makeText(context,"Please enter rest time.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etRounds.text.isNullOrEmpty()) {
            Toast.makeText(context,"Please enter number of repeats.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etPauseTime.text.isNullOrEmpty()) {
            Toast.makeText(context,"Please enter pause time.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.etSets.text.isNullOrEmpty()) {
            Toast.makeText(context,"Please enter number of sets.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!binding.etHangTime.text.isDigitsOnly()) {
            Toast.makeText(context,"Please enter valid hang time.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!binding.etRestTime.text.isDigitsOnly()) {
            Toast.makeText(context,"Please enter valid rest time.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!binding.etRounds.text.isDigitsOnly()) {
            Toast.makeText(context,"Please enter valid number of repeats.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!binding.etPauseTime.text.isDigitsOnly()) {
            Toast.makeText(context,"Please enter valid pause time.",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!binding.etSets.text.isDigitsOnly()) {
            Toast.makeText(context,"Please enter valid number of sets.",Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }


}