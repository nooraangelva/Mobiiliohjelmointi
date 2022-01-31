package com.example.edistynytmobiiliohjelmointi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.edistynytmobiiliohjelmointi.databinding.FragmentDataBinding
import com.google.gson.GsonBuilder
import java.io.UnsupportedEncodingException
import com.example.edistynytmobiiliohjelmointi.dataclass.TodoItem
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_data.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

// this is the url where we want to get our data from
const val JSON_URL = "https://jsonplaceholder.typicode.com/todos"

class DataFragment : Fragment() {

    private var _binding: FragmentDataBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: RecyclerAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        linearLayoutManager = LinearLayoutManager(context)
        binding.dataRecyclerView.layoutManager = linearLayoutManager

        getData()

        binding.sendDataButton.setOnClickListener {

            sendData()

        }

        return root
    }

    private fun sendData(){

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.POST, JSON_URL,
            Response.Listener { response ->
                // usually APIs return the added new data back
                // when sending new data
                // therefore the response here should contain the JSON version
                // of the data you just sent below
                Log.d("ADVTECH", response)

                val json = JSONTokener(response).nextValue()

                when (json) {
                    is JSONObject -> { //it is a JsonObject
                        val item : TodoItem = gson.fromJson(response, TodoItem::class.java)
                        val rows : List<TodoItem> = listOf(item)

                        Toast.makeText(context, rows.toString(),Toast.LENGTH_SHORT).show()
                    }
                    is JSONArray -> { //it is a JsonArray
                        val rows: List<TodoItem> =
                            gson.fromJson(response, Array<TodoItem>::class.java).toList()

                        Toast.makeText(context, rows.toString(),Toast.LENGTH_SHORT).show()

                    }
                    else -> { //handle the odd scenario
                        val item : TodoItem = gson.fromJson(response, TodoItem::class.java)
                        val rows : List<TodoItem> = listOf(item)

                        Toast.makeText(context, rows.toString(),Toast.LENGTH_SHORT).show()

                    }
                }

            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("ADVTECH", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                // we have to specify a proper header, otherwise Apigility will block our queries!
                // define we are after JSON data!
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }

            // let's build the new data here
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                // this function is only needed when sending data
                var body = ByteArray(0)
                try { // check the example "Converting a Kotlin object to JSON"
                    // create a new TodoItem object here, and convert it to string format (GSON)
                    val newData = TodoItem(1,1,"Roskat", false)
                    val jsonData = gson.toJson(newData)
                    // JSON to bytes
                    body = jsonData.toByteArray(Charsets.UTF_8)
                } catch (e: UnsupportedEncodingException) {
                    // problems with converting our data into UTF-8 bytes
                }
                return body
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        Singleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

    private fun getData(){

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, JSON_URL,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                Log.d("GET-Response", response)
                val rows : List<TodoItem> = gson.fromJson(response, Array<TodoItem>::class.java).toList()

                for(item: TodoItem in rows)
                {
                    // do something here with each item
                    Log.d("GET-Response item todo", item.title)
                }

                // Putting the data to recyclerview
                adapter = RecyclerAdapter(rows)
                binding.dataRecyclerView.adapter = adapter


            },
            Response.ErrorListener {
                // typically this is a connection error
                Log.d("GET-Error", it.toString())
            })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                // basic headers for the data
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }

        // Add the request to the RequestQueue. This has to be done in both getting and sending new data.
        // if using this in an activity, use "this" instead of "context"
        // Access the RequestQueue through your singleton class.
        Singleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
