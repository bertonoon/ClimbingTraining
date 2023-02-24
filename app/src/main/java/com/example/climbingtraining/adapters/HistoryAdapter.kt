package com.example.climbingtraining.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ItemSingleHistoryBinding
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter (
    private val historyList : ArrayList<SingleHangboardHistoryModel>,
    private val viewModel: HangboardViewModel,
    private val navController: NavController
        ) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemSingleHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SingleHangboardHistoryModel) {
            val sdf = SimpleDateFormat(this.itemView.context.getString(R.string.date_format),
                Locale.US)
            binding.apply {
                tvDate.text = sdf.format(item.date)
                tvName.text = item.hangboardType.name
                cvHangboard.setOnClickListener { openDetails(adapterPosition) }
            }
        }
    }

    private fun openDetails(id:Int) {
        viewModel.setHistoryDetails(historyList[id])
        navController.navigate(R.id.action_historyFragment_to_historyDetailsFragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSingleHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun deleteRecord(idConfig: Int) {
        viewModel.deleteRecordHistory(historyList[idConfig])
        notifyDataSetChanged()
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    fun updateList(newHistory: List<SingleHangboardHistoryModel>?) {
        if (newHistory.isNullOrEmpty()) return
        historyList.clear()
        historyList.addAll(newHistory)
        notifyDataSetChanged()
    }


}
