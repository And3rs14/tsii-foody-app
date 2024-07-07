package romilp.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import romilp.foody.data.database.entities.FavoritesEntity
import romilp.foody.data.database.entities.FoodJokeEntity
import romilp.foody.data.database.entities.RecipesEntity
import romilp.foody.data.database.entities.SelectedDateEntity
import romilp.foody.data.database.entities.ScheduledRecipeEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FoodJokeEntity::class, ScheduledRecipeEntity::class, SelectedDateEntity::class],
    version = 3,  // Incrementa la versión de la base de datos
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class, DateTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipesDao(): RecipesDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `scheduled_recipes_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `recipe` TEXT NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `selected_dates_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `selectedDate` INTEGER NOT NULL, `recipe` TEXT NOT NULL)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `scheduled_recipes_table` ADD COLUMN `mealType` TEXT NOT NULL DEFAULT 'Desayuno'")
            }
        }
    }
}

