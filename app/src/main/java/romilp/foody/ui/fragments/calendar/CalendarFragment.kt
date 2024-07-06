package romilp.foody.ui.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import romilp.foody.R
import romilp.foody.data.database.entities.ScheduledRecipeEntity
import romilp.foody.databinding.FragmentCalendarBinding
import romilp.foody.models.CalendarDateModel
import romilp.foody.ui.adapters.ScheduledRecipeAdapter
import romilp.foody.viewModels.CalendarViewModel
import romilp.foody.viewModels.ScheduledRecipeViewModel
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private val calendarList = ArrayList<CalendarDateModel>()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var scheduledRecipeAdapter: ScheduledRecipeAdapter

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val scheduledRecipeViewModel: ScheduledRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        calendarAdapter = CalendarAdapter { calendarDateModel, position ->
            // Manejar el clic en la fecha
            calendarViewModel.selectedDate = calendarDateModel.date
            calendarAdapter.setSelectedPosition(position)
            loadScheduledRecipes(calendarDateModel.date)
        }

        scheduledRecipeAdapter = ScheduledRecipeAdapter(scheduledRecipeViewModel)

        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.calendarRecyclerView.adapter = calendarAdapter

        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerView.adapter = scheduledRecipeAdapter

        setUpCalendar()

        binding.ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }

        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            setUpCalendar()
        }

        binding.btnToday.setOnClickListener {
            goToToday()
        }

        binding.noRecipesTextView.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_recipesFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Restaurar la fecha seleccionada y actualizar la vista al volver
        if (calendarViewModel.selectedDate != null) {
            cal.time = calendarViewModel.selectedDate!! // Actualizar el calendario al mes de la fecha seleccionada
        }
        setUpCalendar()
    }

    private fun setUpCalendar() {
        binding.tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        calendarList.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarAdapter.setData(ArrayList(calendarList))
        binding.calendarRecyclerView.scrollToPosition(0) // Empezar desde el primer día del mes

        // Seleccionar el día presente la primera vez que se entra a la vista calendario
        if (calendarViewModel.selectedDate == null) {
            calendarViewModel.selectedDate = Calendar.getInstance(Locale.ENGLISH).time
        }

        updateSelection()
    }

    private fun updateSelection() {
        val selectedDate = calendarViewModel.selectedDate
        val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)
        val selectedMonth = selectedDate?.let { SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(it) }

        if (selectedDate != null && currentMonth == selectedMonth) {
            val selectedPosition = calendarList.indexOfFirst {
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
                sdf.format(it.date) == SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(selectedDate)
            }
            if (selectedPosition != -1) {
                calendarAdapter.setSelectedPosition(selectedPosition)
                binding.calendarRecyclerView.scrollToPosition(selectedPosition)
            } else {
                calendarAdapter.clearSelection()
            }
        } else {
            calendarAdapter.clearSelection()
        }
        loadScheduledRecipes(selectedDate!!)
    }

    private fun scrollToToday() {
        val currentDate = Calendar.getInstance(Locale.ENGLISH).time
        val todayPosition = calendarList.indexOfFirst {
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
            sdf.format(it.date) == sdf.format(currentDate)
        }
        if (todayPosition != -1) {
            cal.time = currentDate // Actualizar el calendario a la fecha actual
            calendarAdapter.setSelectedPosition(todayPosition)
            binding.calendarRecyclerView.scrollToPosition(todayPosition)
            binding.tvDateMonth.text = sdf.format(cal.time) // Actualizar el mes mostrado
            calendarViewModel.selectedDate = currentDate
        }
        loadScheduledRecipes(currentDate)
    }

    private fun goToToday() {
        cal.time = Calendar.getInstance(Locale.ENGLISH).time
        calendarViewModel.selectedDate = cal.time // Actualizar la fecha seleccionada en el ViewModel
        setUpCalendar()
        scrollToToday()
    }

    private fun loadScheduledRecipes(date: Date) {
        scheduledRecipeViewModel.readScheduledRecipes.observe(viewLifecycleOwner, Observer { recipes ->
            val filteredRecipes = recipes.filter { scheduledRecipe ->
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
                sdf.format(scheduledRecipe.date) == sdf.format(date)
            }
            scheduledRecipeAdapter.submitList(filteredRecipes)
            updateNoRecipesText(filteredRecipes)
        })
    }

    private fun updateNoRecipesText(filteredRecipes: List<ScheduledRecipeEntity>) {
        if (filteredRecipes.isEmpty()) {
            val selectedDate = calendarViewModel.selectedDate!!
            val currentDate = Calendar.getInstance(Locale.ENGLISH).time
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)

            val message = if (dateFormat.format(selectedDate) == dateFormat.format(currentDate)) {
                "Aún no tienes recetas agendadas hoy, selecciona una."
            } else {
                "Aún no tienes recetas agendadas en ${displayFormat.format(selectedDate)}, selecciona una."
            }
            binding.noRecipesTextView.visibility = View.VISIBLE
            binding.noRecipesTextView.text = message
        } else {
            binding.noRecipesTextView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
