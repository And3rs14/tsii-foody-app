package romilp.foody.ui.fragments.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import romilp.foody.R
import romilp.foody.models.CalendarDateModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val listener: (CalendarDateModel, Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    private val list = ArrayList<CalendarDateModel>()
    private var selectedPosition = -1

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(calendarDateModel: CalendarDateModel) {
            val calendarDay = itemView.findViewById<TextView>(R.id.tv_calendar_day)
            val calendarDate = itemView.findViewById<TextView>(R.id.tv_calendar_date)
            val cardView = itemView.findViewById<CardView>(R.id.card_calendar)

            val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
            val dateFormat = SimpleDateFormat("dd", Locale.ENGLISH)

            calendarDay.text = dayFormat.format(calendarDateModel.date)
            calendarDate.text = dateFormat.format(calendarDateModel.date)

            // Cambiar el color de fondo y texto si está seleccionado
            if (adapterPosition == selectedPosition) {
                cardView.setCardBackgroundColor(Color.parseColor("#6200EA")) // Morado
                calendarDay.setTextColor(Color.WHITE)
                calendarDate.setTextColor(Color.WHITE)
            } else {
                cardView.setCardBackgroundColor(Color.WHITE)
                calendarDay.setTextColor(Color.BLACK)
                calendarDate.setTextColor(Color.BLACK)
            }

            cardView.setOnClickListener {
                listener.invoke(calendarDateModel, adapterPosition)
                setSelectedPosition(adapterPosition) // Actualiza la selección
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_date, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }

    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        if (previousPosition != -1) notifyItemChanged(previousPosition) // Actualiza la vista anterior
        notifyItemChanged(selectedPosition) // Actualiza la nueva vista seleccionada
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}
