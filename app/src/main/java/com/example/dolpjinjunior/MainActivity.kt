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
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.initialize(this@MainActivity, Config.SECRET_KEY)
        Log.i("LOG-DEBUGGER", "USER TOKEN ${Config.USER_TOKEN}")

        if (Config.USER_TOKEN != "null" && Config.STATUS_BUG == 0) {
            val intent = Intent(this, MainMenu::class.java)
            Utils.makeToast(this@MainActivity, "You have logged in already", Toast.LENGTH_LONG)
            this.startActivity(intent)
        }

        else {
            Utils.clearData(this@MainActivity)
        }

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
                if (username == "" || password == "") {
                    Utils.makeToast(this@MainActivity, "username or password is empty", Toast.LENGTH_SHORT)
                }

                else {
                    val result = try { ApolloClient.Builder()
                        .serverUrl(Config.GRAPHQL_URI)
                        .build()
                        .mutation(UserAuthMutation(username, password)).execute()
                    } catch (exeption : ApolloException) {
                        Utils.makeToast(this@MainActivity, "Error Network", Toast.LENGTH_LONG)
                        throw exeption
                    }
                    Log.i("LOG-INFO","Username : $username Password : $password ")

                    if (result.data?.login == null) {
                        Utils.makeToast(this@MainActivity, result.errors?.get(0)?.message.toString(), Toast.LENGTH_SHORT)
                    }

                    else {
                        Utils.saveData(this@MainActivity, Config.SECRET_KEY, result.data?.login?.token.toString())
                        Utils.initialize(this@MainActivity, Config.SECRET_KEY)
                        Utils.makeToast(this@MainActivity, "Login Success", Toast.LENGTH_SHORT)
                        val context = loginButton.context
                        val intent = Intent(context, MainMenu::class.java)
                        context.startActivity(intent)
                    }
                }

            }
        }

        registerButton.setOnClickListener {
            val context = registerButton.context

            val intent = Intent(context, RegisterActivity::class.java)

            context.startActivity(intent)
        }

    }
}