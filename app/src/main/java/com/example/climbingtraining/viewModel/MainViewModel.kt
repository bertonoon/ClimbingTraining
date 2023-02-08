package com.example.climbingtraining.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.climbingtraining.model.*
import com.example.climbingtraining.view.MainActivity

class MainViewModel(application : MainActivity) : ViewModel()  {

    //Live data
    val currentHangboard = MutableLiveData<SimpleHangboard>()
    val currentTimeToFinish = MutableLiveData<Long>()
    val currentHangboardState = MutableLiveData<ExerciseState>()
    val _setsToFinish = MutableLiveData<Int>()
    val _repeatsToFinish = MutableLiveData<Int>()
    val runState = MutableLiveData<RunState>()

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

    private fun initHangboard(){
        currentExercise = Exercise(this)
        currentHangboard.postValue(currentExercise.getHangboard())
        runState.postValue(RunState.INITIALIZED)
        currentTimeToFinish.postValue(currentExercise.getHangboard().prepareTime)
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
        currentHangboard.postValue(currentExercise.getHangboard())
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