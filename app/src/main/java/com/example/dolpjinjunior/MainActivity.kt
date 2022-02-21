package com.example.dolpjinjunior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Utils


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val GRAPH_URL = "http://192.168.1.31:4000/"

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
                try {
                    val result = try { ApolloClient.Builder()
                                .serverUrl(GRAPH_URL)
                                .build()
                                .mutation(UserAuthMutation(username, password)).execute()
                            } catch (exeption : ApolloException) {
                                Utils.makeToast(this@MainActivity, "Error Network", Toast.LENGTH_LONG)
                                throw exeption
                            }
                    Log.i("LOG-INFO","Username : $username Password : $password ")
                    Log.i("LOG-INFO","Success ${result.data?.login?.username.toString()}")

                    if (result.data?.login == null) {
                        Utils.makeToast(this@MainActivity, result.errors?.get(0)?.message.toString(), Toast.LENGTH_SHORT)
                    }

                    else {
                        Utils.makeToast(this@MainActivity, "Login Success", Toast.LENGTH_SHORT)
                    }
                }
                catch (error : Error) {
                    Utils.makeToast(this@MainActivity, "Error maybe caused by internet connection", Toast.LENGTH_SHORT)
                }

            }
        }

        registerButton.setOnClickListener {
            val context = registerButton.context

            val intent = Intent(context, LoginActivity::class.java)

            context.startActivity(intent)
        }

    }
}