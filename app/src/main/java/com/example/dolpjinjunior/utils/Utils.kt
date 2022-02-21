package com.example.dolpjinjunior.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException

class Utils {

    companion object {
        //make Toast
        fun makeToast(context : AppCompatActivity, text : String, length : Int) {
            Toast.makeText(context, text, length).show()
        }

        suspend fun checkConnection() : Boolean {
            val GRAPH_URL = "http://192.168.1.31:4000/"
            return try {
                ApolloClient.Builder().serverUrl(GRAPH_URL).build()
                Log.e("LOG-ERROR", "TRUE")
                true
            } catch (err : ApolloException) {
                Log.e("LOG-ERROR", "ERRRR")
                false
            }

        }

    }
}