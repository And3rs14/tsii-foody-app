package romilp.foody.data

import kotlinx.coroutines.flow.Flow
import romilp.foody.data.database.RecipesDAO
import romilp.foody.data.database.entities.SelectedDateEntity
import javax.inject.Inject

class SelectedDateRepository @Inject constructor(private val recipesDAO: RecipesDAO) {

    fun readSelectedDates(): Flow<List<SelectedDateEntity>> {
        return recipesDAO.readSelectedDates()
    }

    suspend fun insertSelectedDate(selectedDateEntity: SelectedDateEntity) {
        recipesDAO.insertSelectedDate(selectedDateEntity)
    }

    suspend fun getLastSelectedDate(): SelectedDateEntity? {
        return recipesDAO.getLastSelectedDate()
    }
}
