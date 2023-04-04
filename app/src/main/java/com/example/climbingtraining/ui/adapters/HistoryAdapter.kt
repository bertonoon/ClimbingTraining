package com.example.climbingtraining.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ItemSingleHistoryBinding
import com.example.climbingtraining.models.CrimpType
import com.example.climbingtraining.models.GripType
import com.example.climbingtraining.models.SingleHangboardHistoryModel
import com.example.climbingtraining.ui.viewModels.HangboardViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val historyList: ArrayList<SingleHangboardHistoryModel>,
    private val viewModel: HangboardViewModel,
    private val navController: NavController
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemSingleHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SingleHangboardHistoryModel) {
            val sdf = SimpleDateFormat(
                this.itemView.context.getString(R.string.date_format), Locale.US
            )
            binding.apply {
                tvDate.text = sdf.format(item.date)
                tvName.text = item.hangboardType.name
                cvHangboard.setOnClickListener { openDetails(adapterPosition) }
            }

            if (item.crimpType != CrimpType.UNDEFINED
                && item.gripType != GripType.PINCH
                && item.gripType != GripType.JUG
                && item.gripType != GripType.SLOPER
            ) {
                binding.tvCrimpType.text = item.crimpType.shortcutString()
                binding.tvCrimpType.visibility = View.VISIBLE
            } else {
                binding.tvCrimpType.visibility = View.INVISIBLE
            }

            if (item.gripType != GripType.UNDEFINED) {
                binding.tvGripType.text = item.gripType.toDisplayString()
                binding.tvGripType.visibility = View.VISIBLE
            } else {
                binding.tvGripType.visibility = View.INVISIBLE
            }

            if (item.gripType == GripType.SLOPER) {
                binding.tvEdgeOrSlopeType.text = "${item.slopeAngle} °"
                binding.tvEdgeOrSlopeType.visibility = View.VISIBLE
            } else if (item.gripType != GripType.JUG
                && item.gripType != GripType.PINCH
                && item.gripType != GripType.UNDEFINED
            ) {
                binding.tvEdgeOrSlopeType.text = "${item.edgeSize} mm"
                binding.tvEdgeOrSlopeType.visibility = View.VISIBLE
            } else {
                binding.tvEdgeOrSlopeType.visibility = View.INVISIBLE
            }

            if (item.additionalWeight > 0) {
                binding.tvAdditionalWeight.text = "${item.additionalWeight} kg"
                binding.tvAdditionalWeight.visibility = View.VISIBLE
            } else binding.tvAdditionalWeight.visibility = View.INVISIBLE

            if (item.intensity > 0) {
                binding.tvIntensity.text = "${item.intensity} %"
                binding.tvIntensity.visibility = View.VISIBLE
            } else binding.tvIntensity.visibility = View.INVISIBLE
        }
    }

    private fun openDetails(id: Int) {
        viewModel.setHistoryDetails(historyList[id])
        navController.navigate(R.id.action_historyFragment_to_historyDetailsFragment)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSingleHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    fun deleteRecord(id: Int) {
        viewModel.deleteRecordHistory(historyList[id])
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

    fun openEditDetails(id: Int) {
        viewModel.setEditHistoryDetails(historyList[id])
        navController.navigate(R.id.action_historyFragment_to_historyEditDetailsFragment)
    }


}
