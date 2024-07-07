package romilp.foody.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import romilp.foody.data.Repository
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    fun isRecipeScheduledOnDateAndType(recipeId: Int, date: Date, mealType: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isScheduled = withContext(Dispatchers.IO) {
                val scheduledRecipes = repository.local.getScheduledRecipesOnDate(date)
                scheduledRecipes.any { it.recipe.recipeId == recipeId && it.mealType == mealType }

                // Verificar si la receta con el ID dado ya está agendada en esa fecha con ese tipo de comida
                scheduledRecipes.any { it.recipe.recipeId == recipeId }
            }
            callback(isScheduled)
        }
    }

}
