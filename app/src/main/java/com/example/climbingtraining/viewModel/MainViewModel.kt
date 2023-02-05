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
    val runState = MutableLiveData<RunState>()


    //Others
    private lateinit var currentExercise : Exercise
    private lateinit var chosenHangboard: SimpleHangboard

    fun updateData(timeToFinish: Long,currentState: ExerciseState){
        currentTimeToFinish.postValue(timeToFinish)
        currentHangboardState.postValue(currentState)
    }

    fun onViewReady(){
        chosenHangboard = BasicHangboardTimes.getBasicTimes()
        currentExercise = Exercise(chosenHangboard,this)
        runState.postValue(RunState.INITIALIZED)
        currentHangboard.postValue(chosenHangboard)
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