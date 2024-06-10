package RecyclerViewHelper

import Modelo.Connection
import Modelo.DataClassTicket
import alejandro.murcia.aplicacion_crud.R
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adaptador(private var Datos: List<DataClassTicket>) : RecyclerView.Adapter<RecyclerViewHelper.ViewHolder>() {

    fun actualizarLista(nuevaLista:List<DataClassTicket>){
        Datos=nuevaLista
        notifyDataSetChanged()
    }

    //Función para actualizar el recyclerView
    //Cuando actualizo los datos
    fun actualizarListaDespuesDeActualizarDatos(numTicket: Int, titulo: String){
        val index = Datos.indexOfFirst { it.numTicket == numTicket }
        Datos[index].titulo = titulo
        notifyItemChanged(index)
    }

    fun EliminarTicket(numTicket: Int, position: Int){

        //quitar el elementpo de la lista
        val listaTicket = Datos .toMutableList()
        listaTicket.removeAt(position)

        //quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO) {

            //crear un objeto e la clase conexion
            val objConexion=Connection().ExecuteConnect()

            val deleteProducto = objConexion?.prepareStatement("DELETE FROM TB_TICKET WHERE NUM_TICKET = ?;")!!
            deleteProducto.setInt( 1,numTicket)
            deleteProducto.executeUpdate()

            val commit = objConexion.prepareStatement( "commit")!!
            commit.executeUpdate()
        }
        Datos = listaTicket.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()

    }

    fun ActualizarTicket(titulo: String, descripcion: String ,numTicket: Int){

        //1- CREO UNA CORRUTINA
        GlobalScope.launch(Dispatchers.IO){

            //1- Creo un objeto de tipo conexion
            val objConexion = Connection().ExecuteConnect()

            //2- Creo una variable un prepareStatement
            val updateTicket = objConexion?.prepareStatement("UPDATE TB_TICKET SET TÍTULO = ?, DESCRIPCIÓN = ? WHERE NUM_TICKET = ?;")!!
            updateTicket.setString(1, titulo)
            updateTicket.setString(2, descripcion)
            updateTicket.setInt(3,numTicket)
            updateTicket.executeUpdate()

            val commit = objConexion.prepareStatement("COMMIT")!!
            commit.executeUpdate()


            withContext(Dispatchers.Main){
                actualizarListaDespuesDeActualizarDatos(numTicket, titulo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHelper.ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_ticket, parent, false)
        return RecyclerViewHelper.ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = Datos[position]
        holder.textView.text = ticket.titulo
        val item =Datos[position]


        holder.imgBorrar.setOnClickListener {
            //craeamos una alaerta

            //invocamos  el contexto
            val context = holder.itemView.context

            //CREO LA ALERTA

            val builder = AlertDialog.Builder(context)

            //le ponemos titulo a la alerta

            builder.setTitle("¿estas seguro?")

            //ponerle mendsaje a la alerta

            builder.setMessage("Deseas en verdad eliminar el registro")

            //agrgamos los botones

            builder.setPositiveButton("si"){dialog,wich ->
                EliminarTicket(item.numTicket,position)
            }

            builder.setNegativeButton("no"){dialog,wich ->

            }

            //cramos la alerta
            val alertDialog=builder.create()

            //mostramos la alerta

            alertDialog.show()

        }

        holder.imgEditar.setOnClickListener {

            val context = holder.itemView.context

            //Creo la alerta
            val  builder = AlertDialog.Builder(context)
            builder.setTitle("Editar nombre")

            //Agreguemos un cuadro de textos para que el usuario pueda escribir el nuevo nombre
            val cuadritoNuevoNombre = EditText(context)
            cuadritoNuevoNombre.setHint(item.titulo)
            builder.setView(cuadritoNuevoNombre)
            val cuadritoNuevaDesc = EditText(context)
            cuadritoNuevaDesc.setHint(item.descripción)
            builder.setView(cuadritoNuevoNombre)

            builder.setPositiveButton("Actualizar "){ dialog, wich ->
                ActualizarTicket(cuadritoNuevoNombre.text.toString(), cuadritoNuevaDesc.text.toString(), item.numTicket)

            }

            builder.setNegativeButton( "Cancelar"){ dialog, wich ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        /*
        //darle click a la card
        holder.itemView.setOnClickListener{
            val context = holder.itemView.context

            //Cambiamos de pantalla
            //Abro la pantalla de productos
            val pantallaDetalles = Intent(context, Activity_Detalle_Producto::class.java)

            //Abriremos la pantalla
            //Pero antes mandamos los parametros

            pantallaDetalles.putExtra("UUID", item.uuid )
            pantallaDetalles.putExtra("Producto", item.NombreProducto )
            pantallaDetalles.putExtra("Precio", item.precio )
            pantallaDetalles.putExtra("Cantidad", item.cantidad )

            //Inicializamos la actividad

            context.startActivity(pantallaDetalles)
        }
         */

    }
}
