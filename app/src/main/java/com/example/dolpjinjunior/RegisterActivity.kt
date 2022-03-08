package com.example.dolpjinjunior

/* Import necessary library */
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils

/* This File controls Login Page In activity_register.xml */
class RegisterActivity : AppCompatActivity() {

    /* This function use for when The layout start*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Call for making layout display */
        setContentView(R.layout.activity_register)

        /* Get the Button variable*/
        val registerButton: Button = findViewById(R.id.register_btn)
        val backButton: Button = findViewById(R.id.backBtn)

        /* Get the Edittext variable*/
        val usernameEditText: EditText = findViewById(R.id.username_id)
        val passwordEditText: EditText = findViewById(R.id.password_id)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password_id)

        /* When the register button are pressed */
        registerButton.setOnClickListener {
            /* Get username and password from the widgets */
            val username: String = usernameEditText.text.toString()
            val password: String = passwordEditText.text.toString()
            val confirmPassword: String = confirmPasswordEditText.text.toString()

            /* if the password is not same to confirm password, alert message will be shown up */
            if (password != confirmPassword) {
                Utils.makeToast(this@RegisterActivity, "Password is not same", Toast.LENGTH_SHORT)
            }
            /* This is Register Process */
            else {
                lifecycleScope.launchWhenResumed {

                    /* If user or password is empty the alert message will be shown up*/
                    if (username == "" || password == "") {
                        Utils.makeToast(
                            this@RegisterActivity,
                            "username or password is empty",
                            Toast.LENGTH_SHORT
                        )
                    }

                    /* Start Register Process */
                    else {
                        val result = try {
                            ApolloClient.Builder()
                                .serverUrl(Config.GRAPHQL_URI)
                                .build()
                                .mutation(LoginUserMutation(username, password))
                                .execute()
                        } catch (err: ApolloException) {
                            Utils.makeToast(
                                this@RegisterActivity,
                                "Error Network Connection",
                                Toast.LENGTH_SHORT
                            )
                            throw err
                        }

                        /* If error on register process, the error message will be displayed */
                        if (result.data?.register == null) {
                            Utils.makeToast(
                                this@RegisterActivity,
                                result.errors?.get(0)?.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                        }

                        /* if register process success */
                        else {
                            /* Saved the token to Config.USER_TOKEN, please ignore this statement */
                            Config.USER_TOKEN = result.data?.register?.token.toString()

                            /* Display message when register process success */
                            Utils.makeToast(
                                this@RegisterActivity,
                                "Register Success",
                                Toast.LENGTH_SHORT
                            )

                            /* Navigate (change) to Login layout */
                            val context = registerButton.context
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }

        /* When back button are pressed navigate to Login layout*/
        backButton.setOnClickListener {
            val context = backButton.context
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}