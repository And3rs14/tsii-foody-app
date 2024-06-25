package romilp.foody.ui.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import romilp.foody.R
import romilp.foody.databinding.FragmentCalendarBinding
import romilp.foody.models.CalendarDateModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private val calendarList = ArrayList<CalendarDateModel>()
    private lateinit var adapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        adapter = CalendarAdapter { calendarDateModel, position ->
            // Manejar el clic en la fecha
            adapter.setSelectedPosition(position)
            updateNoRecipesText()
        }

        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.calendarRecyclerView.adapter = adapter

        setUpCalendar()

        binding.ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            adapter.clearSelection() // Limpia la selección al cambiar el mes
            setUpCalendar()
        }

        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            adapter.clearSelection() // Limpia la selección al cambiar el mes
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
        adapter.setData(ArrayList(calendarList))

        // Marcar el día actual si no se ha seleccionado otro día
        if (adapter.getSelectedPosition() == -1) {
            scrollToToday()
        }
    }

    private fun scrollToToday() {
        val currentDate = Calendar.getInstance(Locale.ENGLISH).time
        val todayPosition = calendarList.indexOfFirst {
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
            sdf.format(it.date) == sdf.format(currentDate)
        }
        if (todayPosition != -1) {
            cal.time = currentDate // Actualizar el calendario a la fecha actual
            adapter.setSelectedPosition(todayPosition)
            binding.calendarRecyclerView.scrollToPosition(todayPosition)
            binding.tvDateMonth.text = sdf.format(cal.time) // Actualizar el mes mostrado
        }
        updateNoRecipesText()
    }

    private fun goToToday() {
        cal.time = Calendar.getInstance(Locale.ENGLISH).time
        adapter.clearSelection() // Limpia la selección al cambiar el mes
        setUpCalendar()
        scrollToToday()
    }

    private fun updateNoRecipesText() {
        binding.noRecipesTextView.text = "Aún no tienes recetas agendadas en este día, selecciona una."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
