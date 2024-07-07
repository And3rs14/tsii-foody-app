package romilp.foody.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import romilp.foody.data.database.entities.FavoritesEntity
import romilp.foody.data.database.entities.FoodJokeEntity
import romilp.foody.data.database.entities.RecipesEntity
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import romilp.foody.data.database.entities.SelectedDateEntity
import romilp.foody.data.database.RecipesDatabase
import java.util.Date

@Dao
interface RecipesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledRecipe(scheduledRecipeEntity: ScheduledRecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedDate(selectedDateEntity: SelectedDateEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM scheduled_recipes_table ORDER BY date ASC")
    fun readScheduledRecipes(): Flow<List<ScheduledRecipeEntity>>

    @Query("SELECT * FROM food_joke_table ORDER BY id ASC")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    @Query("SELECT * FROM selected_dates_table ORDER BY id ASC")
    fun readSelectedDates(): Flow<List<SelectedDateEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun deleteScheduledRecipe(scheduledRecipeEntity: ScheduledRecipeEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()

    @Query("DELETE FROM scheduled_recipes_table")
    suspend fun deleteAllScheduledRecipes()

    @Query("DELETE FROM selected_dates_table")
    suspend fun deleteAllSelectedDates()

    @Query("SELECT * FROM selected_dates_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastSelectedDate(): SelectedDateEntity?

    @Query("SELECT * FROM scheduled_recipes_table WHERE date(date / 1000, 'unixepoch') = date(:date / 1000, 'unixepoch')")
    suspend fun getScheduledRecipesOnDate(date: Date): List<ScheduledRecipeEntity>

    @Query("SELECT * FROM scheduled_recipes_table WHERE date = :date AND mealType = :mealType")
    suspend fun getScheduledRecipesOnDateAndType(date: Date, mealType: String): List<ScheduledRecipeEntity>
}
