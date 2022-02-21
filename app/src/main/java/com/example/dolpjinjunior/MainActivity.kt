package com.example.dolpjinjunior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.example.dolpjinjunior.utils.Utils


class MainActivity : AppCompatActivity() {

    private val GRAPH_URL = "http://192.168.1.31:4000/";
    private val apolloClient = ApolloClient.Builder().serverUrl(GRAPH_URL).build()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //EditText
        val usernameEditText : EditText = findViewById(R.id.username_id)
        val passwordEditText : EditText = findViewById(R.id.password_id)

        //Button
        val loginButton : Button = findViewById(R.id.button_loginid)
        val registerButton : Button = findViewById(R.id.button_register)

        loginButton.setOnClickListener {
            val username : String = usernameEditText.text.toString()
            val password : String = passwordEditText.text.toString()

            //when Login Button
            lifecycleScope.launchWhenResumed {
                val result = apolloClient.mutation(UserAuthMutation(username, password)).execute()
                Utils.makeToast(this@MainActivity, "Login Success", Toast.LENGTH_SHORT)
                Log.i("LOG-INFO","Username : $username Password : $password ")
                Log.i("LOG-INFO","Success ${result.data?.login?.username.toString()}")
            }
        }

    }
}