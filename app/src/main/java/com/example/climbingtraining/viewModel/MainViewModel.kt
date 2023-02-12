package com.example.climbingtraining.viewModel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.climbingtraining.model.*
import com.example.climbingtraining.view.MainActivity
import kotlinx.coroutines.NonDisposableHandle.parent

class MainViewModel(application : MainActivity) : ViewModel()  {

    //Live data
    val currentHangboard = MutableLiveData<SimpleHangboard>()
    val currentTimeToFinish = MutableLiveData<Long>()
    val currentHangboardState = MutableLiveData<ExerciseState>()
    val _setsToFinish = MutableLiveData<Int>()
    val _repeatsToFinish = MutableLiveData<Int>()
    val runState = MutableLiveData<RunState>()

    val setsToFinish : LiveData<Int>
        get() = _setsToFinish

    //Others
    private lateinit var currentExercise : Exercise
    private lateinit var chosenHangboard: SimpleHangboard

    fun updateData(timeToFinish: Long,
                   currentState: ExerciseState,
                   setsToFinish : Int,
                   repeatsToFinish : Int){
        currentTimeToFinish.postValue(timeToFinish)
        currentHangboardState.postValue(currentState)
        _setsToFinish.postValue(setsToFinish)
        _repeatsToFinish.postValue(repeatsToFinish)
    }

    private fun updateData(){
        currentTimeToFinish.postValue(currentExercise.getTimeToFinish())
        currentHangboardState.postValue(currentExercise.getState())
        _setsToFinish.postValue(currentExercise.getHangboard().numberOfSets)
        _repeatsToFinish.postValue(currentExercise.getHangboard().numberOfRepeats)
        currentHangboard.postValue(currentExercise.getHangboard())
    }


    private fun initHangboard(){
        currentExercise = Exercise(this)
        runState.postValue(RunState.INITIALIZED)
        updateData()
    }


    fun onViewReady(){
        if (this::currentExercise.isInitialized) return
        initHangboard()
    }

    fun onFinish(){
        chosenHangboard = currentExercise.getHangboard()
        initHangboard()
        setHangboard()
    }

    fun setHangboard(hangboard: SimpleHangboard){
        chosenHangboard =  hangboard
        setHangboard()
    }
    private fun setHangboard(){
        currentExercise.setHangboard(chosenHangboard)
        updateData()
    }

    fun onStart(){
        if (currentExercise.getState() == ExerciseState.INACTIVE
            && runState.value == RunState.INITIALIZED) {
            currentExercise.run()
        } else if (runState.value == RunState.STOPPED){
            currentExercise.resume()
        }
        runState.postValue(RunState.ACTIVE)
    }
    fun onStop(){
        if (currentExercise.getState() != ExerciseState.INACTIVE) {
            currentExercise.stop()
            runState.postValue(RunState.STOPPED)
        }
    }
    fun onReset(){
        currentExercise.reset()
        runState.postValue(RunState.INITIALIZED)
    }


}