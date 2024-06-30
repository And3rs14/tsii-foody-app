package romilp.foody.ui

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import romilp.foody.R
import romilp.foody.adapters.PagerAdapter
import romilp.foody.data.database.entities.FavoritesEntity
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import romilp.foody.databinding.ActivityDetailsBinding
import romilp.foody.ui.fragments.ingredients.IngredientsFragment
import romilp.foody.ui.fragments.instructions.InstructionsFragment
import romilp.foody.ui.fragments.overview.OverviewFragment
import romilp.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import romilp.foody.viewModels.MainViewModel
import romilp.foody.viewModels.ScheduledRecipeViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()
    private val scheduledRecipeViewModel: ScheduledRecipeViewModel by viewModels()
    private lateinit var binding: ActivityDetailsBinding

    private var recipeSaved = false
    private var savedRecipeId = 0

    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        setSupportActionBar(binding.toolBar)
        binding.toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.save_to_favorites_menu -> {
                if (!recipeSaved) {
                    saveToFavorite(item)
                } else {
                    removeFromFavorites(item)
                }
                true
            }
            R.id.calendar_menu -> {
                showDatePickerDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this) { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity) {
                    if (savedRecipe.result.recipeId == args.result.recipeId) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }

    private fun saveToFavorite(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(0, args.result)
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved")
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(savedRecipeId, args.result)
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.detailsLayout, message, Snackbar.LENGTH_SHORT).setAction("Okay") {}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }

    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem, R.color.white)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val scheduledRecipeEntity = ScheduledRecipeEntity(
                date = selectedDate.time,
                recipe = args.result
            )
            scheduledRecipeViewModel.insertScheduledRecipe(scheduledRecipeEntity)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            println("DetailsActivity: Fecha seleccionada: ${dateFormat.format(selectedDate.time)}")
            showSnackBar("Recipe scheduled for ${dateFormat.format(selectedDate.time)}")
        }, year, month, day)

        datePickerDialog.show()
    }
}
