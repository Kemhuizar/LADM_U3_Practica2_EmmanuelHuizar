package mx.edu.ittepic.ladm_u3_practica2_emmanuelhuizar

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota= FirebaseFirestore.getInstance()
    var dataLista=ArrayList<String>()
    var listaID=ArrayList<String>()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buscar.addTextChangedListener {
            if(buscar.text.toString()==""){
                llenartabla()
            }else{
                buscar.setHint("Ingresa nombre del producto")
                consulta(buscar.text.toString())
            }
        }

        button.setOnClickListener {
            var v = Intent(this,pedidos::class.java)
            startActivity(v)
        }

        llenartabla()
    }

    private fun llenartabla() {
        baseRemota.collection("restaurante").whereEqualTo("pedidos.entregado",false)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nTelefono: "+document.getString("telefono")+"\n"+
                            "Nombre producto: "+document.get("pedidos.descripcion")+
                            "\nCantidad: "+document.get("pedidos.cantidad")+
                            "\nPrecio:  $"+document.get("pedidos.precio")+
                            "\n                                    Total:  $"+(document.get("pedidos.precio").toString().toFloat()*document.get("pedidos.cantidad").toString().toInt())
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay pedidos por entregar")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista.adapter=adaptador
            }
        lista.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            entregar(position)
        }
    }

    private fun consulta(mcc: String) {
        baseRemota.collection("restaurante").whereEqualTo("pedidos.entregado",false).whereEqualTo("pedidos.descripcion",mcc)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda",Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nTelefono: "+document.getString("telefono")+"\n"+
                            "Nombre producto: "+document.get("pedidos.descripcion")+
                            "\nCantidad: "+document.get("pedidos.cantidad")+
                            "\nPrecio:  $"+document.get("pedidos.precio")+
                            "\n                                    Total:  $"+(document.get("pedidos.precio").toString().toFloat()*document.get("pedidos.cantidad").toString().toInt())
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay pedidos por entregar")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista.adapter=adaptador
            }
        lista.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            entregar(position)
        }
    }

    private fun entregar(position: Int) {
        AlertDialog.Builder(this).setTitle("Atencion").setMessage("¿Se entregara el pedido \n ${dataLista[position]}?")
            .setPositiveButton("Entregar"){d,w->
                entregarpedido(listaID[position])
            }
            .setNegativeButton("Actulizar"){d,w->
                actulizar(listaID[position])
            }
            .setNeutralButton("Cancelar"){dialog, which ->
            }
            .show()
    }

    private fun actulizar(s: String) {
        var dialogo= Dialog(this)
        dialogo.setContentView(R.layout.pedido)

        var descripcion = dialogo.findViewById<EditText>(R.id.editText7)
        var cantidad = dialogo.findViewById<EditText>(R.id.editText8)
        var precio = dialogo.findViewById<EditText>(R.id.editText9)
        var button7 = dialogo.findViewById<Button>(R.id.button7)
        var button8 = dialogo.findViewById<Button>(R.id.button8)

        button7.setText("ACTUALIZAR REGISTRO")

        baseRemota.collection("restaurante").document(s).get()
            .addOnSuccessListener {
                descripcion.setText(it.getString("pedidos.descripcion"))
                precio.setText(it.get("pedidos.precio").toString())
                cantidad.setText(it.get("pedidos.cantidad").toString())

            }
            .addOnFailureListener {
                Toast.makeText(this,"Error no hay conexion de red",Toast.LENGTH_LONG).show()
            }

        button7.setOnClickListener {
            if(descripcion.text.toString().equals("") || cantidad.text.toString().equals("") || precio.text.toString().equals("")){
                Toast.makeText(this,"Campos vacios",Toast.LENGTH_LONG).show()
            }else{
                baseRemota.collection("restaurante").document(s)
                    .update(
                        "pedidos.descripcion",descripcion.text.toString(),
                        "pedidos.cantidad",cantidad.text.toString().toInt(),
                        "pedidos.precio",precio.text.toString().toFloat()
                    )
                    .addOnSuccessListener {
                        Toast.makeText(this,"Se actualizo correctamente",Toast.LENGTH_LONG).show()
                        dialogo.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Error no se peude actualizar", Toast.LENGTH_LONG).show()
                    }
            }
        }
        button8.setOnClickListener {
            dialogo.dismiss()
        }
        dialogo.show()

    }

    private fun entregarpedido(s: String) {
        baseRemota.collection("restaurante").document(s)
            .update(
                "pedidos.entregado",true
            )
            .addOnSuccessListener {
                Toast.makeText(this,"Se entrego correctamente",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error no en la red", Toast.LENGTH_LONG).show()
            }
    }
}
