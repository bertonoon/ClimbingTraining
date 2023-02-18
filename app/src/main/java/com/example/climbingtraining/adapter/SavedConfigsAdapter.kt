package com.example.climbingtraining.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.databinding.FragmentHangboardBinding
import com.example.climbingtraining.model.SimpleHangboard
import com.example.climbingtraining.ui.viewModels.HangboardViewModel

class SavedConfigsAdapter (
    private val configsList : ArrayList<SimpleHangboard>,
    private val viewModel: HangboardViewModel
        ) : RecyclerView.Adapter<SavedConfigsAdapter.ViewHolder>(){

    inner class ViewHolder (
        private val binding: FragmentHangboardBinding
            ) : RecyclerView.ViewHolder(binding.root) {
                fun bind(config:SimpleHangboard){
                    binding.apply {
                        tvHangTime.text = (config.hangTime/1000).toString()
                        tvRestTime.text = (config.restTime/1000).toString()
                        tvPauseTime.text = (config.pauseTime/1000).toString()
                        tvSetsToEnd.text = config.numberOfSets.toString()
                        tvRoundsToEnd.text = config.numberOfRepeats.toString()
                        tvHangboardName.text = config.name.ifEmpty { "Unnamed" }
                        cvHangboard.setOnClickListener {expandView(adapterPosition,binding)}
                        btnSet.setOnClickListener { setHangboard(adapterPosition)}
                        btnDelete.setOnClickListener {
                            deleteHangboard(adapterPosition)
                            expandView(adapterPosition,binding)
                            notifyDataSetChanged()
                        }
                    }
                }
    }

    private fun deleteHangboard(idConfig: Int) {
        viewModel.deleteHangboard(configsList[idConfig])
    }

    private fun setHangboard(idConfig: Int) {
        viewModel.setHangboard(configsList[idConfig])
    }

    private fun expandView(position: Int, binding: FragmentHangboardBinding){
        if (binding.llButtons.visibility == View.GONE){
            binding.llButtons.visibility = View.VISIBLE}
        else {
            binding.llButtons.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentHangboardBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(configsList[position])
    }

    override fun getItemCount(): Int = configsList.size

    fun updateList(newConfigsList: List<SimpleHangboard>?) {
        if (newConfigsList.isNullOrEmpty()) return
        configsList.clear()
        configsList.addAll(newConfigsList)
        notifyDataSetChanged()
    }



}