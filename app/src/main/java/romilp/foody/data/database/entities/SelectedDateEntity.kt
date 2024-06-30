package romilp.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import romilp.foody.model.Result
import romilp.foody.util.Constants.Companion.SELECTED_DATES_TABLE
import java.util.*

@Entity(tableName = SELECTED_DATES_TABLE)
data class SelectedDateEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var selectedDate: Date,
    var recipe: Result
)
