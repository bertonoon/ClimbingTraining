package com.example.climbingtraining.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                    binding.apply {
                        tvHangTime.text = config.hangTime.toString()
                        tvRestTime.text = config.restTime.toString()
                        tvPauseTime.text = config.pauseTime.toString()
                        tvSetsToEnd.text = config.numberOfSets.toString()
                        tvRoundsToEnd.text = config.numberOfRepeats.toString()
                        tvHangboardName.text = "Name"
                        cvHangboard.setOnClickListener {onClick(adapterPosition,binding)}
                    }
                }
    }

    private fun onClick(position: Int, binding: FragmentHangboardBinding){
        //Toast.makeText(context,position.toString(),Toast.LENGTH_SHORT).show()
        Log.i("wcisniete",position.toString())
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