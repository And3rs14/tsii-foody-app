package romilp.foody.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import romilp.foody.data.Repository
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import javax.inject.Inject

@HiltViewModel
class ScheduledRecipeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val readScheduledRecipes = repository.local.readScheduledRecipes().asLiveData()

    fun insertScheduledRecipe(scheduledRecipeEntity: ScheduledRecipeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertScheduledRecipe(scheduledRecipeEntity)
        }

    fun deleteScheduledRecipe(scheduledRecipeEntity: ScheduledRecipeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteScheduledRecipe(scheduledRecipeEntity)
        }

    fun deleteAllScheduledRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllScheduledRecipes()
        }
}
