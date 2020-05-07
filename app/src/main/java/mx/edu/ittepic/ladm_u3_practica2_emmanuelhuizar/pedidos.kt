package mx.edu.ittepic.ladm_u3_practica2_emmanuelhuizar

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_pedidos.*
import kotlinx.android.synthetic.main.clientes.*
import kotlin.math.pow

class pedidos : AppCompatActivity() {
    var baseRemota= FirebaseFirestore.getInstance()
    var dataLista=ArrayList<String>()
    var listaID=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        llenartable()

        button4.setOnClickListener {
            finish()
        }

        button3.setOnClickListener {
            var v = Intent(this,Main2Activity::class.java)
            startActivity(v)
        }
        buscar2.addTextChangedListener {
            if(buscar2.text.toString()==""){
                llenartable()
            }else{
                buscar2.setHint("Ingresa nombre del cliente")
                consulta(buscar2.text.toString())
            }
        }

        nuevocliente.setOnClickListener {
            var dialogo= Dialog(this)
            dialogo.setContentView(R.layout.clientes)

            var telefono = dialogo.findViewById<EditText>(R.id.editText)
            var direccion = dialogo.findViewById<EditText>(R.id.editText2)
            var nombre = dialogo.findViewById<EditText>(R.id.editText3)
            var button2 = dialogo.findViewById<Button>(R.id.button2)
            var button5 = dialogo.findViewById<Button>(R.id.button5)

            button2.setOnClickListener {
                if(nombre.text.toString().equals("") || telefono.text.toString().equals("") || direccion.text.toString().equals("")){
                    Toast.makeText(this,"Campos vacios",Toast.LENGTH_LONG).show()
                }else{
                    var datosInsertar = hashMapOf(
                        "nombre" to nombre.text.toString(),
                        "telefono" to telefono.text.toString(),
                        "domicilio" to direccion.text.toString(),
                        "pedidos" to hashMapOf(
                            "cantidad" to 0,
                            "descripcion" to "",
                            "entregado" to true,
                            "precio" to 0
                        )
                    )

                    baseRemota.collection("restaurante").add(datosInsertar as Any)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Se inserto correctamente",Toast.LENGTH_LONG).show()
                            dialogo.dismiss()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this,"IMPORTANTE No se pudo insertar",Toast.LENGTH_LONG).show()
                        }

                }

            }

            button5.setOnClickListener {
                dialogo.dismiss()
            }
            dialogo.show()
        }


    }

    private fun consulta(mcc: String) {
        baseRemota.collection("restaurante").whereEqualTo("pedidos.cantidad",0).whereEqualTo("nombre",mcc)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nTelefono: "+document.getString("telefono")+"\n"+
                            "Domicilio: "+document.get("domicilio")
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay pedidos por entregar")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista2.adapter=adaptador
            }
        lista2.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            Agregarpedido(listaID[position])
        }
    }

    private fun llenartable() {
        baseRemota.collection("restaurante").whereEqualTo("pedidos.cantidad",0)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nTelefono: "+document.getString("telefono")+"\n"+
                            "Domicilio: "+document.get("domicilio")
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay pedidos por entregar")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista2.adapter=adaptador
            }
        lista2.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            Agregarpedido(listaID[position])
        }
    }

    private fun Agregarpedido(position: String) {
        var dialogo= Dialog(this)
        dialogo.setContentView(R.layout.pedido)

        var descripcion = dialogo.findViewById<EditText>(R.id.editText7)
        var cantidad = dialogo.findViewById<EditText>(R.id.editText8)
        var peso = dialogo.findViewById<EditText>(R.id.editText9)
        var button7 = dialogo.findViewById<Button>(R.id.button7)
        var button8 = dialogo.findViewById<Button>(R.id.button8)

        button7.setOnClickListener {
            if(descripcion.text.toString().equals("") || cantidad.text.toString().equals("") || peso.text.toString().equals("")){
                Toast.makeText(this,"Campos vacios",Toast.LENGTH_LONG).show()
            }else{
                var pedido = hashMapOf(
                    "cantidad" to cantidad.text.toString().toInt(),
                    "descripcion" to descripcion.text.toString(),
                    "entregado" to false,
                    "precio" to peso.text.toString().toFloat()
                )
                baseRemota.collection("restaurante").document(position).update("pedidos",pedido as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Se ingreso pedido correctamente",Toast.LENGTH_LONG).show()
                        dialogo.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Error no en la red", Toast.LENGTH_LONG).show()
                    }
            }
        }
        button8.setOnClickListener {
            dialogo.dismiss()
        }
        dialogo.show()
    }
}
