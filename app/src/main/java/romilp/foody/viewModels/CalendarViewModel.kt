package romilp.foody.ui.fragments.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import romilp.foody.data.SelectedDateRepository
import romilp.foody.data.database.entities.SelectedDateEntity
import romilp.foody.model.Result
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: SelectedDateRepository
) : ViewModel() {
    var selectedDate: Date? = null

    fun insertSelectedDate(date: Date, recipe: Result) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSelectedDate(SelectedDateEntity(selectedDate = date, recipe = recipe))
        }
    }

    fun getLastSelectedDate() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedDate = repository.getLastSelectedDate()?.selectedDate
        }
    }
}
