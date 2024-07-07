package romilp.foody.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import romilp.foody.R
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import romilp.foody.databinding.ItemGroupHeaderBinding
import romilp.foody.databinding.ItemScheduledRecipeBinding
import romilp.foody.viewModels.ScheduledRecipeViewModel
import java.util.Calendar
import java.util.Date

class ScheduledRecipeAdapter(
    private val viewModel: ScheduledRecipeViewModel,
    private val currentDate: Date, // Pasar la fecha actual al adaptador
    private val clickListener: (ScheduledRecipeEntity) -> Unit,
) : ListAdapter<ScheduledRecipeAdapter.Item, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    sealed class Item {
        data class Header(val title: String) : Item()
        data class RecipeItem(val recipe: ScheduledRecipeEntity) : Item()
    }

    class HeaderViewHolder(private val binding: ItemGroupHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(headerTitle: String) {
            binding.headerTitle = headerTitle
            binding.executePendingBindings()
        }
    }

    class RecipeViewHolder(private val binding: ItemScheduledRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scheduledRecipe: ScheduledRecipeEntity, viewModel: ScheduledRecipeViewModel, isDeletable: Boolean, clickListener: (ScheduledRecipeEntity) -> Unit, backgroundColor: Int) {
            binding.scheduledRecipe = scheduledRecipe
            binding.viewModel = viewModel
            binding.isDeletable = isDeletable
            binding.root.setBackgroundColor(backgroundColor)
            binding.root.setOnClickListener {
                clickListener(scheduledRecipe)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemGroupHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_ITEM -> {
                val binding = ItemScheduledRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RecipeViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Item.Header -> (holder as HeaderViewHolder).bind(item.title)
            is Item.RecipeItem -> {
                val scheduledRecipe = item.recipe
                val isDeletable = isSameDayOrAfter(scheduledRecipe.date, currentDate)
                val backgroundColor = when (scheduledRecipe.mealType) {
                    "Desayuno" -> ContextCompat.getColor(holder.itemView.context, R.color.yellow)
                    "Almuerzo" -> ContextCompat.getColor(holder.itemView.context, R.color.orange)
                    "Cena" -> ContextCompat.getColor(holder.itemView.context, R.color.light_blue)
                    else -> ContextCompat.getColor(holder.itemView.context, R.color.white)
                }
                (holder as RecipeViewHolder).bind(scheduledRecipe, viewModel, isDeletable, clickListener, backgroundColor)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Header -> VIEW_TYPE_HEADER
            is Item.RecipeItem -> VIEW_TYPE_ITEM
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    private fun isSameDayOrAfter(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance().apply {
            time = date1
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calendar2 = Calendar.getInstance().apply {
            time = date2
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return !calendar1.before(calendar2)
    }
}
