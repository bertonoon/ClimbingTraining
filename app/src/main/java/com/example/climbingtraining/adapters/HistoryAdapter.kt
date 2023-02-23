package com.example.climbingtraining.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.databinding.ItemSingleHistoryBinding
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import java.text.SimpleDateFormat

class HistoryAdapter (
    private val historyList : ArrayList<SingleHangboardHistoryModel>,
    private val viewModel: HangboardViewModel,
    private val navController: NavController
        ) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemSingleHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SingleHangboardHistoryModel) {
            val sdf = SimpleDateFormat("yyyy-mm-dd HH:mm")
            binding.apply {
                tvDate.text = sdf.format(item.date)
                tvName.text = item.hangboardType.name
            }
        }
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
