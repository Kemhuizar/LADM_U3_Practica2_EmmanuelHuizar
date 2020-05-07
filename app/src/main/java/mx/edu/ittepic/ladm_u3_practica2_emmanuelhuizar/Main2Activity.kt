package mx.edu.ittepic.ladm_u3_practica2_emmanuelhuizar

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {
    var baseRemota= FirebaseFirestore.getInstance()
    var dataLista=ArrayList<String>()
    var listaID=ArrayList<String>()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        llenartable()

        button6.setOnClickListener {
            finish()
        }

        switch1.setOnClickListener {
            if (switch1.isChecked){
                llenar()
            }else{
                llenartable()
            }
        }
        buscar1.addTextChangedListener {
            if(buscar1.text.toString()==""){
                llenartable()
            }else{
                buscar1.setHint("Ingresa nombre del producto")
                consulta(buscar1.text.toString())
            }
        }

    }

    private fun consulta(mcc: String) {
        baseRemota.collection("restaurante").whereEqualTo("pedidos.entregado",true).whereEqualTo("pedidos.descripcion",mcc)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var situacion=""
                    var dinero=""
                    if(document.get("pedidos.entregado")==false){
                        situacion = "\n                    Pedido no entregado\n"
                        dinero="SubTotal:  $"
                    }else{
                        situacion = "\n                    Pedido entregado\n"
                        dinero="Total:  $"
                    }
                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nDescripcion: "+document.getString("pedidos.descripcion")+"\n"+
                            "cantidad: "+document.get("pedidos.cantidad")+" unidades"+"\nPrecio: "+document.get("pedidos.precio")+" pesos\n"+
                            "\n                                    "+dinero+(document.get("pedidos.precio").toString().toFloat()*document.get("pedidos.cantidad").toString().toInt())+situacion
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay un historico de pedidos")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista3.adapter=adaptador
            }
        lista3.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            elimarpedido(position)
        }
    }

    private fun llenar(){
        baseRemota.collection("restaurante").whereGreaterThan("pedidos.cantidad",0)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var situacion=""
                    var dinero=""
                    if(document.get("pedidos.entregado")==false){
                        situacion = "\n                    Pedido no entregado\n"
                        dinero="SubTotal:  $"
                    }else{
                        situacion = "\n                    Pedido entregado\n"
                        dinero="Total:  $"
                    }

                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nDescripcion: "+document.getString("pedidos.descripcion")+"\n"+
                            "cantidad: "+document.get("pedidos.cantidad")+" unidades"+"\nPrecio: "+document.get("pedidos.precio")+" pesos\n"+
                            "\n                                    "+dinero+(document.get("pedidos.precio").toString().toFloat()*document.get("pedidos.cantidad").toString().toInt())+situacion
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay un historico de pedidos")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista3.adapter=adaptador
            }
        lista3.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            elimarpedido(position)
        }
    }

    private fun llenartable(){
        baseRemota.collection("restaurante").whereEqualTo("pedidos.entregado",true).whereGreaterThan("pedidos.cantidad",0)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    Toast.makeText(this,"No se pude realizar busqueda", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for(document in querySnapshot!!){
                    var situacion=""
                    var dinero=""
                    if(document.get("pedidos.entregado")==false){
                        situacion = "\n                    Pedido no entregado\n"
                        dinero="SubTotal:  $"
                    }else{
                        situacion = "\n                    Pedido entregado\n"
                        dinero="Total:  $"
                    }

                    var cadena = "Nombre cliente: "+document.getString("nombre")+"\nDescripcion: "+document.getString("pedidos.descripcion")+"\n"+
                            "cantidad: "+document.get("pedidos.cantidad")+" unidades"+"\nPrecio: "+document.get("pedidos.precio")+" pesos\n"+
                            "\n                                    "+dinero+(document.get("pedidos.precio").toString().toFloat()*document.get("pedidos.cantidad").toString().toInt())+situacion
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if(dataLista.size==0){
                    dataLista.add("No hay un historico de pedidos")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataLista)
                lista3.adapter=adaptador
            }
        lista3.setOnItemClickListener { parent, view, position, id ->
            if(listaID.size==0){
                return@setOnItemClickListener
            }
            elimarpedido(position)
        }
    }

    private fun elimarpedido(position: Int) {
        AlertDialog.Builder(this).setTitle("Atencion").setMessage("¿Desea eliminar el pedido \n ${dataLista[position]}?")
            .setPositiveButton("Eliminar"){d,w->
                baseRemota.collection("restaurante").document(listaID[position]).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this,"Se elimino con exito",Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"No se pudo eliminar",Toast.LENGTH_LONG).show()
                    }
            }
            .setNeutralButton("Cancelar"){dialog, which ->
            }
            .show()
    }
}
