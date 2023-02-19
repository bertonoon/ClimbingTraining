package com.example.climbingtraining.ui.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.example.climbingtraining.db.SavedConfigsDao
import com.example.climbingtraining.db.HangboardDatabase
import com.example.climbingtraining.model.*
import com.example.climbingtraining.utils.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import com.example.climbingtraining.model.SingleHangboardHistoryModel as SingleHangboardHistoryModel1

class HangboardViewModel(application: Application) : AndroidViewModel(application) {

    private val hangboardDao = HangboardDatabase.getInstance(application.applicationContext).hangboardDao()
    private val historyDao = HangboardDatabase.getInstance(application.applicationContext).historyDao()
    private val lastHangboardDao = HangboardDatabase.getInstance(application.applicationContext).lastHangboardDao()

    //Live data
    private val _currentHangboard = MutableLiveData<SingleHangboard>()
    val currentHangboard : LiveData<SingleHangboard>
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

    private val _savedConfigs = MutableLiveData<List<SingleHangboard>>()
    val savedConfigs : LiveData<List<SingleHangboard>>
        get() = _savedConfigs

    private val _history = MutableLiveData<List<SingleHangboardHistoryModel1>>()
    val history : LiveData<List<SingleHangboardHistoryModel1>>
        get() = _history

    //Others
    private lateinit var currentExercise : Exercise
    private lateinit var chosenHangboard: SingleHangboard


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
        currentExercise = Exercise(this,getApplication())
        setLastHangboard()
        _runState.postValue(RunState.INITIALIZED)
        updateData()
    }


    fun onViewReady(){
        if (this::currentExercise.isInitialized) return
        initHangboard()
    }

    fun onSavedConfigsReady(){
        if (_savedConfigs.value.isNullOrEmpty()){
            fetchSavedConfigs()
        }
    }


    fun onFinish(){
        chosenHangboard = currentExercise.getHangboard()
        _runState.postValue(RunState.FINISHED)
    }

    fun reload(){
        initHangboard()
        setHangboard()
    }


    fun setHangboard(hangboard: SingleHangboard){
        chosenHangboard =  hangboard
        setHangboard()
    }
    private fun setHangboard(){
        currentExercise.setHangboard(chosenHangboard)
        saveLastConfig(chosenHangboard)
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


    private fun addSavedConfig(savedConfigsDao: SavedConfigsDao, newConfig : SingleHangboard) {
        viewModelScope.launch(Dispatchers.IO) {
            savedConfigsDao.insert(newConfig)
        }
        fetchSavedConfigs()
    }

    private fun fetchSavedConfigs(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                hangboardDao.fetchAll().collect(){
                    val result = ArrayList(it)
                    _savedConfigs.postValue(result)
                }
            } catch(e : Exception) {
                Log.i("dbFetch", e.toString())
            }
        }
    }

    fun saveHangboard(config: SingleHangboard) {
        addSavedConfig(hangboardDao,config)
    }

    private fun deleteHangboardFromDb(config: SingleHangboard) {
        viewModelScope.launch(Dispatchers.IO){
            hangboardDao.delete(config)
        }
        fetchSavedConfigs()                           

    }
    fun deleteHangboard(config: SingleHangboard){
        deleteHangboardFromDb(config)
    }

    fun saveCurrentHangboard(){
        saveToHistory(chosenHangboard)
        reload()
    }
    private fun saveToHistory(hangboardType :SingleHangboard){
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insert(
                SingleHangboardHistoryModel1(
                    date = Date(),
                    hangboardType = hangboardType
                )
            )
        }
    }
    private fun saveLastConfig(hangboard :SingleHangboard){
        viewModelScope.launch(Dispatchers.IO) {
            lastHangboardDao.insert(
                LastHangboard(
                    id = 1,
                    prepareTime = hangboard.prepareTime,
                    hangTime = hangboard.hangTime,
                    pauseTime = hangboard.pauseTime,
                    numberOfRepeats = hangboard.numberOfRepeats,
                    restTime = hangboard.restTime,
                    numberOfSets = hangboard.numberOfSets,
                    name = hangboard.name,
                )
            )
        }
    }
    fun onHistoryReady() {
        if (_history.value.isNullOrEmpty()){
            fetchHistory()
        }
    }

    private fun fetchHistory(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                historyDao.fetchAll().collect(){
                    val result = ArrayList(it)
                    _history.postValue(result)
                }
            } catch(e : Exception) {
                Log.i("dbHistoryFetch", e.toString())
            }
        }
    }

    private fun setLastHangboard(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = lastHangboardDao.fetchAll()
                chosenHangboard = SingleHangboard(
                    id = 0,
                    prepareTime = result.prepareTime,
                    hangTime = result.hangTime,
                    pauseTime = result.pauseTime,
                    numberOfRepeats = result.numberOfRepeats,
                    restTime = result.restTime,
                    numberOfSets = result.numberOfSets,
                    name = result.name,
                )
                setHangboard()
            } catch(e : Exception) {
                Log.i("dbLastConfigFetch", e.toString())
            }
        }
    }


}