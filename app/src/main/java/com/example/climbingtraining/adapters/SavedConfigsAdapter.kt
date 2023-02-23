package com.example.climbingtraining.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.climbingtraining.R
import com.example.climbingtraining.databinding.ItemSingleHangboardBinding
import com.example.climbingtraining.models.SingleHangboard
import com.example.climbingtraining.ui.viewModels.HangboardViewModel


class SavedConfigsAdapter (
    private val configsList : ArrayList<SingleHangboard>,
    private val viewModel: HangboardViewModel,
    private val navController: NavController
        ) : RecyclerView.Adapter<SavedConfigsAdapter.ViewHolder>(){

    inner class ViewHolder (
        private val binding: ItemSingleHangboardBinding
            ) : RecyclerView.ViewHolder(binding.root) {
                fun bind(config:SingleHangboard){
                    binding.apply {
                        tvHangTime.text = (config.hangTime/1000).toString()
                        tvRestTime.text = (config.restTime/1000).toString()
                        tvPauseTime.text = (config.pauseTime/1000).toString()
                        tvSetsToEnd.text = config.numberOfSets.toString()
                        tvRoundsToEnd.text = config.numberOfRepeats.toString()
                        tvHangboardName.text = config.name.ifEmpty { "Unnamed" }
                        cvHangboard.setOnClickListener {expandView(adapterPosition,binding)}
                        btnSet.setOnClickListener { setHangboard(adapterPosition)}
                        btnEdit.setOnClickListener { editHangboard(adapterPosition) }
                        btnDelete.setOnClickListener { deleteHangboard(adapterPosition,binding) }
                    }
                }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteHangboard(idConfig: Int, binding: ItemSingleHangboardBinding) {
        viewModel.deleteHangboard(configsList[idConfig])
        binding.llButtons.visibility = View.GONE
        notifyDataSetChanged()
    }

    private fun editHangboard(idConfig: Int){
        viewModel.setHangboardForEdit(configsList[idConfig])
        navController.navigate(R.id.action_savedConfigurationsFragment_to_addNewHangboardFragment)
    }

    private fun setHangboard(idConfig: Int) {
        viewModel.setHangboard(configsList[idConfig])
        navController.navigate(R.id.action_savedConfigurationsFragment_to_timerFragment)

    }

    private fun expandView(position: Int, binding: ItemSingleHangboardBinding){
        if (binding.llButtons.visibility == View.GONE){
            expand(binding.llButtons)
        }
        else {
            binding.llButtons.animate()
            collapse(binding.llButtons)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSingleHangboardBinding.inflate(LayoutInflater.from(parent.context),
        parent,
        false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(configsList[position])
    }

    override fun getItemCount(): Int = configsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newConfigsList: List<SingleHangboard>?) {
        if (newConfigsList.isNullOrEmpty()) return
        configsList.clear()
        configsList.addAll(newConfigsList)
        notifyDataSetChanged()
    }

    private fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = 2*(targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    private fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = 2*(initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }



}