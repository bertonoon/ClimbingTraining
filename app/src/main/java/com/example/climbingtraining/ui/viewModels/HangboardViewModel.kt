package com.example.climbingtraining.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.climbingtraining.model.*

class HangboardViewModel() : ViewModel()  {

    //Live data
    private val _currentHangboard = MutableLiveData<SimpleHangboard>()
    val currentHangboard : LiveData<SimpleHangboard>
        get() = _currentHangboard

    private val _currentTimeToFinish = MutableLiveData<Long>()
    val currentTimeToFinish : LiveData<Long>
        get() = _currentTimeToFinish

    private val _currentHangboardState = MutableLiveData<ExerciseState>()
    val currentHangboardState : LiveData<ExerciseState>
        get() = _currentHangboardState

    private val _runState = MutableLiveData<RunState>()
    val runState : LiveData<RunState>
        get() = _runState

    private val _setsToFinish = MutableLiveData<Int>()
    val setsToFinish : LiveData<Int>
        get() = _setsToFinish

    private val _repeatsToFinish = MutableLiveData<Int>()
    val repeatsToFinish : LiveData<Int>
        get() = _repeatsToFinish


    //Others
    private lateinit var currentExercise : Exercise
    private lateinit var chosenHangboard: SimpleHangboard

    fun updateData(timeToFinish: Long,
                   currentState: ExerciseState,
                   setsToFinish : Int,
                   repeatsToFinish : Int){
        _currentTimeToFinish.postValue(timeToFinish)
        _currentHangboardState.postValue(currentState)
        _setsToFinish.postValue(setsToFinish)
        _repeatsToFinish.postValue(repeatsToFinish)
    }

    private fun updateData(){
        _currentTimeToFinish.postValue(currentExercise.getTimeToFinish())
        _currentHangboardState.postValue(currentExercise.getState())
        _setsToFinish.postValue(currentExercise.getHangboard().numberOfSets)
        _repeatsToFinish.postValue(currentExercise.getHangboard().numberOfRepeats)
        _currentHangboard.postValue(currentExercise.getHangboard())
    }


    private fun initHangboard(){
        currentExercise = Exercise(this)
        _runState.postValue(RunState.INITIALIZED)
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
            && _runState.value == RunState.INITIALIZED) {
            currentExercise.run()
        } else if (_runState.value == RunState.STOPPED){
            currentExercise.resume()
        }
        _runState.postValue(RunState.ACTIVE)
    }
    fun onStop(){
        if (currentExercise.getState() != ExerciseState.INACTIVE) {
            currentExercise.stop()
            _runState.postValue(RunState.STOPPED)
        }
    }
    fun onReset(){
        currentExercise.reset()
        _runState.postValue(RunState.INITIALIZED)
    }


}