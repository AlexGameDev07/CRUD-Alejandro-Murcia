package RecyclerViewHelper

import alejandro.murcia.aplicacion_crud.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.txt_TicketCard)
    val imgEditar: ImageView = view.findViewById(R.id.img_editar)
    val imgBorrar: ImageView = view.findViewById(R.id.img_borrar)

}