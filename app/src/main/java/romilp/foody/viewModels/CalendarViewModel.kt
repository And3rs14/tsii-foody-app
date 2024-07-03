package romilp.foody.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import romilp.foody.data.Repository
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var selectedDate: Date? = null

    val readScheduledRecipes = repository.local.readScheduledRecipes().asLiveData()

    fun getScheduledRecipesForDate(date: Date) = repository.local.readScheduledRecipes()
        .map { list -> list.filter { it.date == date } }
        .asLiveData()

    fun insertScheduledRecipe(scheduledRecipeEntity: ScheduledRecipeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertScheduledRecipe(scheduledRecipeEntity)
        }
}
