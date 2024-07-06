package romilp.foody.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import romilp.foody.databinding.ItemScheduledRecipeBinding
import romilp.foody.viewModels.ScheduledRecipeViewModel

class ScheduledRecipeAdapter(
    private val viewModel: ScheduledRecipeViewModel,
    private val clickListener: (ScheduledRecipeEntity) -> Unit
) : ListAdapter<ScheduledRecipeEntity, ScheduledRecipeAdapter.ScheduledRecipeViewHolder>(DiffCallback()) {

    class ScheduledRecipeViewHolder(private val binding: ItemScheduledRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scheduledRecipe: ScheduledRecipeEntity, viewModel: ScheduledRecipeViewModel, clickListener: (ScheduledRecipeEntity) -> Unit) {
            binding.scheduledRecipe = scheduledRecipe
            binding.viewModel = viewModel
            binding.root.setOnClickListener {
                clickListener(scheduledRecipe)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduledRecipeViewHolder {
        val binding = ItemScheduledRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduledRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduledRecipeViewHolder, position: Int) {
        val scheduledRecipe = getItem(position)
        holder.bind(scheduledRecipe, viewModel, clickListener)
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduledRecipeEntity>() {
        override fun areItemsTheSame(oldItem: ScheduledRecipeEntity, newItem: ScheduledRecipeEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScheduledRecipeEntity, newItem: ScheduledRecipeEntity): Boolean {
            return oldItem == newItem
        }
    }
}
