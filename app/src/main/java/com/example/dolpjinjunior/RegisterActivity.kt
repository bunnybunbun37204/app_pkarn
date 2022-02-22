package com.example.dolpjinjunior

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val registerButton : Button = findViewById(R.id.register_btn)

        val usernameEditText : EditText = findViewById(R.id.username_id)
        val passwordEditText : EditText = findViewById(R.id.password_id)
        val confirmPasswordEditText : EditText = findViewById(R.id.confirm_password_id)

        registerButton.setOnClickListener {
            val username : String = usernameEditText.text.toString()
            val password : String = passwordEditText.text.toString()
            val confirmPassword : String = confirmPasswordEditText.text.toString()

            if (password != confirmPassword){
                Utils.makeToast(this@RegisterActivity, "Password is not same", Toast.LENGTH_SHORT)
            }
            else {
                val GRAPH_URL = "http://192.168.1.31:4000/"
                lifecycleScope.launchWhenResumed {
                    val status = Utils.checkConnection()
                    if (!status) Utils.makeToast(this@RegisterActivity, "No internet", Toast.LENGTH_SHORT)
                    val result = try {
                        ApolloClient.Builder()
                            .serverUrl(GRAPH_URL)
                            .build()
                            .mutation(LoginUserMutation(username, password))
                            .execute()
                    } catch (err : ApolloException) {
                        Log.d("LOG-DEBUGGER", "Err : $err")
                        Utils.makeToast(this@RegisterActivity, "Error Network Connection", Toast.LENGTH_SHORT)
                        throw err
                    }
                    if (result.data?.register == null) {
                        Utils.makeToast(this@RegisterActivity, result.errors?.get(0)?.message.toString(), Toast.LENGTH_SHORT)
                    }
                    else {
                        Config.USER_TOKEN = result.data?.register?.token.toString()
                        Utils.makeToast(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }
}