package romilp.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import romilp.foody.model.Result
import romilp.foody.util.Constants.Companion.SCHEDULED_RECIPES_TABLE
import java.util.*

@Entity(tableName = SCHEDULED_RECIPES_TABLE)
data class ScheduledRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: Date,
    var recipe: Result,
    var mealType: String
)
