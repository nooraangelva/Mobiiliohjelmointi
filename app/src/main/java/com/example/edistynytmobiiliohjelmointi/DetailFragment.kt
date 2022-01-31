package com.example.edistynytmobiiliohjelmointi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.edistynytmobiiliohjelmointi.databinding.FragmentDetailBinding
import com.example.edistynytmobiiliohjelmointi.dataclass.TodoItem
import com.example.edistynytmobiiliohjelmointi.dataclass.UserItem
import com.google.gson.GsonBuilder

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    // get fragment parameters from previous fragment
    private val args: DetailFragmentArgs by navArgs()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val gson = GsonBuilder().setPrettyPrinting().create()!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        getData()
        getUser()

        // print out the given parameter into logs
        Log.d("ADVTECH", "" + args.id.toString())


        return root

    }

    private fun getData(){


        // this is the url where we want to get our data from
        val todoDetailUrl = "https://jsonplaceholder.typicode.com/todos/"+args.id.toString()

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, todoDetailUrl,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                Log.d("GET-Response", response)
                val item : TodoItem = gson.fromJson(response, TodoItem::class.java)

                binding.detailCompletedText.text = "Completed: ${item.completed}"
                binding.detailIdText.text = "Id: ${item.id}"
                binding.detailTitleText.text = "Title: ${item.title}"


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
        Singleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

    private fun getUser(){


        // this is the url where we want to get our data from
        val userNameUrl = "https://jsonplaceholder.typicode.com/users/"+args.id.toString()

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(
            Request.Method.GET, userNameUrl,
            Response.Listener { response ->

                // print the response as a whole
                // we can use GSON to modify this response into something more usable
                Log.d("GET-Response", response)
                val item : UserItem = gson.fromJson(response, UserItem::class.java)

                binding.detailUserIdText.text = "User: ${item.username}"


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
        Singleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}