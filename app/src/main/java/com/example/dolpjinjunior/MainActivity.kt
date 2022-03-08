package com.example.dolpjinjunior

/* Import necessary library */
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils

/* This File controls Login Page In activity_main.xml */
class MainActivity : AppCompatActivity() {

    /* This function use for when The layout start*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Call this function for setting value of Config.USER token variable
        * to be equal to the saved local storage variable via Config.SECRET_KEY */
        Utils.initialize(this@MainActivity, Config.SECRET_KEY)

        /* Checking if user has lodged in, if the user logged in by using Config.USER_TOKEN variable,
        *  This layout will be navigate(change) to Main Menu layout  */
        if (Config.USER_TOKEN != "null" && Config.STATUS_BUG == 0) {
            val intent = Intent(this, MainMenu::class.java)
            Utils.makeToast(this@MainActivity, "You have logged in already", Toast.LENGTH_LONG)
            this.startActivity(intent)
        } else {
            /* If User hasn't logged in, clear the application storage for reducing some bugs*/
            Utils.clearData(this@MainActivity)
        }

        /* Call for making layout display */
        setContentView(R.layout.activity_main)

        /* Get the Edittext variable*/
        val usernameEditText: EditText = findViewById(R.id.username_id)
        val passwordEditText: EditText = findViewById(R.id.password_id)

        /* Get the Button variable*/
        val loginButton: Button = findViewById(R.id.button_loginid)
        val registerButton: Button = findViewById(R.id.button_register)

        /* When the login button is pressed */
        loginButton.setOnClickListener {

            /* Get the value from Edittext widget*/
            val username: String = usernameEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            lifecycleScope.launchWhenResumed {
                /* If user or password is empty the alert message will be shown up*/
                if (username == "" || password == "") {
                    Utils.makeToast(
                        this@MainActivity,
                        "username or password is empty",
                        Toast.LENGTH_SHORT
                    )
                }
                /* This is Login Process */
                else {
                    /* Send User and Password to API */
                    val result = try {
                        ApolloClient.Builder()
                            .serverUrl(Config.GRAPHQL_URI)
                            .build()
                            .mutation(UserAuthMutation(username, password)).execute()
                    } catch (exeption: ApolloException) {
                        Utils.makeToast(this@MainActivity, "Error Network", Toast.LENGTH_LONG)
                        throw exeption
                    }

                    /* If error on log in process, the error message will be displayed */
                    if (result.data?.login == null) {
                        Utils.makeToast(
                            this@MainActivity,
                            result.errors?.get(0)?.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                    }

                    /* If log in process success */
                    else {
                        /* Save Data to local storage*/
                        Utils.saveData(
                            this@MainActivity,
                            Config.SECRET_KEY,
                            result.data?.login?.token.toString()
                        )

                        /* setting value of Config.USER token variable to be equal to the saved local storage */
                        Utils.initialize(this@MainActivity, Config.SECRET_KEY)

                        /* Show up text "Login Success" */
                        Utils.makeToast(this@MainActivity, "Login Success", Toast.LENGTH_SHORT)

                        /* Navigate (change) layout to Main Menu*/
                        val context = loginButton.context
                        val intent = Intent(context, MainMenu::class.java)
                        context.startActivity(intent)
                    }
                }

            }
        }

        /* When register button is pressed */
        registerButton.setOnClickListener {
            val context = registerButton.context
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }

    }
}