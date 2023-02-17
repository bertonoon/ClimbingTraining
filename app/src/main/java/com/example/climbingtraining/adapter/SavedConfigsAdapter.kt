package com.example.climbingtraining.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.databinding.FragmentHangboardBinding
import com.example.climbingtraining.model.SimpleHangboard

class SavedConfigsAdapter (
    private val configsList : ArrayList<SimpleHangboard>
        ) : RecyclerView.Adapter<SavedConfigsAdapter.ViewHolder>(){

    inner class ViewHolder (
        private val binding: FragmentHangboardBinding
            ) : RecyclerView.ViewHolder(binding.root) {
                fun bind(config:SimpleHangboard){
                    binding.tvHangTime.text = config.hangTime.toString()
                    binding.tvRestTime.text = config.restTime.toString()
                    binding.tvPauseTime.text = config.pauseTime.toString()
                    binding.tvSetsToEnd.text = config.numberOfSets.toString()
                    binding.tvRoundsToEnd.text = config.numberOfRepeats.toString()
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