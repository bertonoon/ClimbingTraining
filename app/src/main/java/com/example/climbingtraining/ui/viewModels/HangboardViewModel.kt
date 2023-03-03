package com.example.climbingtraining.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.climbingtraining.db.SavedConfigsDao
import com.example.climbingtraining.db.HangboardDatabase
import com.example.climbingtraining.models.*
import com.example.climbingtraining.utils.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import com.example.climbingtraining.models.SingleHangboardHistoryModel

class HangboardViewModel(application: Application) : AndroidViewModel(application) {

    private val hangboardDao = HangboardDatabase.getInstance(application.applicationContext).hangboardDao()
    private val historyDao = HangboardDatabase.getInstance(application.applicationContext).historyDao()
    private val lastHangboardDao = HangboardDatabase.getInstance(application.applicationContext).lastHangboardDao()

    //Live data
    private val _currentHangboard = MutableLiveData<SingleHangboard>()
    val currentHangboard : LiveData<SingleHangboard>
        get() = _currentHangboard

    private val _editedHangboard = MutableLiveData<SingleHangboard>()
    val editedHangboard : LiveData<SingleHangboard>
        get() = _editedHangboard

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

    private val _history = MutableLiveData<List<SingleHangboardHistoryModel>>()
    val history : LiveData<List<SingleHangboardHistoryModel>>
        get() = _history

    private val _dbResultStatus = MutableLiveData<DbResultState>()
    val dbResultStatus : LiveData<DbResultState>
        get() = _dbResultStatus

    private val _historyDetailsHangboard = MutableLiveData<SingleHangboardHistoryModel>()
    val historyDetailsHangboard : LiveData<SingleHangboardHistoryModel>
        get() = _historyDetailsHangboard

    private val _historyEditDetailsHangboard = MutableLiveData<SingleHangboardHistoryModel>()
    val historyEditDetailsHangboard : LiveData<SingleHangboardHistoryModel>
        get() = _historyEditDetailsHangboard

    private val _historyEditFlag = MutableLiveData<Boolean>()
    val historyEditFlag : LiveData<Boolean>
        get() = _historyEditFlag

    private val _secondsToFinish = MutableLiveData<Int>()
    val secondsToFinish : LiveData<Int>
        get() = _secondsToFinish




    //Others
    private lateinit var currentExercise : Exercise
    private  var chosenHangboard: SingleHangboard = BasicHangboardTimes.getBasicTimes()


    fun updateData(timeToFinish: Long,
                   currentState: ExerciseState,
                   setsToFinish : Int,
                   repeatsToFinish : Int){
        _currentTimeToFinish.postValue(timeToFinish)
        _currentHangboardState.postValue(currentState)
        _setsToFinish.postValue(setsToFinish)
        _repeatsToFinish.postValue(repeatsToFinish)
        _secondsToFinish.postValue((timeToFinish/1000).toInt())

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
            try {
                savedConfigsDao.insert(newConfig)
                _dbResultStatus.postValue(DbResultState.CONFIG_SAVE_SUCCESS)
            } catch (e:Exception){
                //Log.i("dbAddHangboard", e.toString())
                _dbResultStatus.postValue(DbResultState.CONFIG_SAVE_FAILED)
            }
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
               // Log.i("dbFetch", e.toString())
                _dbResultStatus.postValue(DbResultState.CONFIG_FETCH_FAILED)
            }
        }
    }

    fun saveHangboard(config: SingleHangboard) {
        addSavedConfig(hangboardDao,config)
    }

    private fun deleteHangboardFromDb(config: SingleHangboard) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                hangboardDao.delete(config)
                _dbResultStatus.postValue(DbResultState.CONFIG_DELETE_SUCCESS)
            } catch (e:Exception){
               // Log.i("dbDelete", e.toString())
                _dbResultStatus.postValue(DbResultState.CONFIG_DELETE_FAILED)
            }
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

    fun saveHangboardToHistory(hangboard: SingleHangboardHistoryModel){
        addNewToHistory(hangboard)
        reload()
    }

    private fun saveToHistory(hangboardType :SingleHangboard){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyDao.insert(
                    SingleHangboardHistoryModel(
                        date = Date(),
                        hangboardType = hangboardType
                    )
                )
                _dbResultStatus.postValue(DbResultState.HISTORY_SAVE_SUCCESS)
            } catch (e:Exception){
                //Log.i("dbSaveToHistory", e.toString())
                _dbResultStatus.postValue(DbResultState.HISTORY_SAVE_FAILED)
            }

        }
    }

    private fun addNewToHistory(hangboard: SingleHangboardHistoryModel){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyDao.insert(hangboard)
                _dbResultStatus.postValue(DbResultState.HISTORY_SAVE_SUCCESS)
            } catch (e:Exception){
                //Log.i("dbSaveToHistory", e.toString())
                _dbResultStatus.postValue(DbResultState.HISTORY_SAVE_FAILED)
            }

        }
    }

    private fun saveLastConfig(hangboard :SingleHangboard){
        viewModelScope.launch(Dispatchers.IO) {
            try{
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
            } catch(e : Exception){
                _dbResultStatus.postValue(DbResultState.CONFIG_SAVE_FAILED)
                //Log.i("dbSaveLastConfig", e.toString())
            }
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
                _dbResultStatus.postValue(DbResultState.HISTORY_FETCH_FAILED)
                //Log.i("dbHistoryFetch", e.toString())
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
                //Log.i("dbLastConfigFetch", e.toString())
            }
        }
    }

    fun setHangboardForEdit(hangboard: SingleHangboard) {
        _editedHangboard.postValue(hangboard)
    }

    fun editHangboard(hangboard: SingleHangboard){
        updateSavedConfigInDb(hangboard)
        cancelEditing()
    }

    private fun updateSavedConfigInDb(newConfig : SingleHangboard) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hangboardDao.update(newConfig)
                _dbResultStatus.postValue(DbResultState.CONFIG_EDIT_SUCCESS)
            } catch(e : Exception) {
                //Log.i("dbUpdate", e.toString())
                _dbResultStatus.postValue(DbResultState.CONFIG_EDIT_FAILED)
            }
        }
        fetchSavedConfigs()
    }

    fun cancelEditing() {
        _editedHangboard.postValue(SingleHangboard())
    }

    fun zeroDbStatuses(){
        _dbResultStatus.postValue(DbResultState.NEUTRAL)
    }


    fun deleteRecordHistory(record: SingleHangboardHistoryModel){
        deleteRecordHistoryFromDb(record)
    }

    private fun deleteRecordHistoryFromDb(record: SingleHangboardHistoryModel) {
        viewModelScope.launch(Dispatchers.IO){
            try {
                historyDao.delete(record)
                _dbResultStatus.postValue(DbResultState.HISTORY_DELETE_SUCCESS)
            } catch (e:Exception){
                //Log.i("dbDelete", e.toString())
                _dbResultStatus.postValue(DbResultState.HISTORY_DELETE_FAILED)
            }
        }
        fetchHistory()

    }

    fun setHistoryDetails(singleHangboardHistoryModel: SingleHangboardHistoryModel) {
        _historyDetailsHangboard.postValue(singleHangboardHistoryModel)
    }

    fun setEditHistoryDetails(singleHangboardHistoryModel: SingleHangboardHistoryModel) {
        setHistoryEditFlag(false)
        _historyEditDetailsHangboard.postValue(singleHangboardHistoryModel)
    }

    fun updateHistoryDetails(newHangboardHistoryModel: SingleHangboardHistoryModel) {
        updateHistoryDetailsInDb(newHangboardHistoryModel)
    }

    private fun updateHistoryDetailsInDb(newHangboardHistoryModel: SingleHangboardHistoryModel){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                historyDao.update(newHangboardHistoryModel)
                _dbResultStatus.postValue(DbResultState.HISTORY_EDIT_SUCCESS)
                _historyDetailsHangboard.postValue(newHangboardHistoryModel)
            } catch(e : Exception) {
                //Log.i("dbUpdate", e.toString())
                _dbResultStatus.postValue(DbResultState.HISTORY_EDIT_FAILED)
            }
        }
        fetchHistory()
    }

    fun setHistoryEditFlag(flag : Boolean){
        _historyEditFlag.postValue(flag)
    }

}