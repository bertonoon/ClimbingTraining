package com.example.climbingtraining.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HangboardReceiver : BroadcastReceiver() {

    private val _mData = MutableLiveData<Boolean>(false)
    val mData : LiveData<Boolean>
        get() = _mData


    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("MESSAGE")
        if (message != null) {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
            _mData.postValue(true)
        }
    }
}