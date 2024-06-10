package alejandro.murcia.aplicacion_crud

import Modelo.Connection
import Modelo.DataClassTicket
import RecyclerViewHelper.Adaptador
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtTitulo = findViewById<EditText>(R.id.txtTitulo)
        val txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)
        val txtAutor = findViewById<EditText>(R.id.txtAutor)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreoTicket)
        val txtEstado = findViewById<EditText>(R.id.txtEstado)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTicket)
        val rcvProductos = findViewById<RecyclerView>(R.id.rcvElementos)

        ////////////////////////////////TODO:mostrar datos ////////////////////////

        // Asignar un layout al RecyclerView
        rcvProductos.layoutManager = LinearLayoutManager(this)

        // Función para obtener datos
        fun obtenerDatos(): List<DataClassTicket> {
            val tickets = mutableListOf<DataClassTicket>()
            val objConexion = Connection().ExecuteConnect()

            objConexion?.use { connection ->
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM TB_TICKET")

                resultSet.use { rs ->
                    while (rs.next()) {
                        val numTicket = rs.getInt("NUM_TICKET")
                        val titulo = rs.getString("TÍTULO")
                        val descripcion = rs.getString("DESCRIPCIÓN")
                        val autor = rs.getString("AUTOR")
                        val correo = rs.getString("CORREO")
                        val fechaCreacion = rs.getDate("FECHA_CREACIÓN")
                        val estado = rs.getString("ESTADO")
                        val fechaFinalizacion = rs.getString("FECHA_FINALIZACIÓN")

                        val ticket = DataClassTicket (numTicket, titulo, descripcion, autor, correo, fechaCreacion, estado, fechaFinalizacion)
                        tickets.add(ticket)
                    }
                }
            }
            return tickets
        }

        // Asignar un adaptador
        CoroutineScope(Dispatchers.IO).launch {
            val ticketsBd = obtenerDatos()
            withContext(Dispatchers.Main) {
                val miAdapter = Adaptador(ticketsBd)
                rcvProductos.adapter = miAdapter
            }
        }

        btnAgregar.setOnClickListener{
            try {

                GlobalScope.launch (Dispatchers.IO){
                    //Abrir corrutina
                    val objConnection = Connection().ExecuteConnect()

                    val crearTicket = objConnection?.prepareStatement("INSERT INTO TB_TICKET (título, descripción, autor, correo,estado) VALUES (?,?,?,?,?)")!!

                    crearTicket.setString(1, txtTitulo.text.toString())
                    crearTicket.setString( 2, txtDescripcion.text.toString())
                    crearTicket.setString(3, txtAutor.text.toString())
                    crearTicket.setString(4, txtCorreo.text.toString())
                    crearTicket.setString(5, txtEstado.text.toString())
                    crearTicket.executeUpdate()

                    val nuevoticket = obtenerDatos()

                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "ticket guardado", Toast.LENGTH_SHORT).show()
                        (rcvProductos.adapter as? Adaptador)?.actualizarLista(nuevoticket)

                    }




                }


            }
            catch (ex:Exception){
                println("REGISTER: Loco este es el error:$ex")

            }
        }
    }
}