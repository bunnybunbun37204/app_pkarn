package com.example.dolpjinjunior

/* Import necessary library */
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils

/* This File controls Login Page In main_menu.xml */
class MainMenu : AppCompatActivity() {

    /* Declare some variable */
    private var backPressedTime: Long = 0
    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        Config.STATUS_BUG = 1
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    /* This function use for when The layout start*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        /* Start check the user who authenticate are exist*/
        lifecycleScope.launchWhenResumed {
            val data = ApolloClient.Builder().serverUrl(Config.GRAPHQL_URI).build()
                .query(QuerytokenUserQuery())
                .addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
            val isTokenize: String = data.data?.me?.username.toString()

            /* If user auth failed show alert dialogue message */
            if (isTokenize == "null") {
                basicAlert()
            }
        }

        /* Get the button variable */
        val logoutButton: Button = findViewById(R.id.button_logout)
        val gateInButton: Button = findViewById(R.id.gatein_btn)
        val gateOutButton: Button = findViewById(R.id.gateout_btn)
        val dataNotiButton: Button = findViewById(R.id.data_noti_btn)

        /* When log out button pressed */
        logoutButton.setOnClickListener {
            Config.STATUS_BUG = 1
            Utils.clearData(this@MainMenu)
            val context = logoutButton.context
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        /* When gate in button pressed */
        gateInButton.setOnClickListener {
            val context = gateInButton.context
            val intent = Intent(context, GateInActivity::class.java)
            context.startActivity(intent)
        }

        /* When gate out button pressed */
        gateOutButton.setOnClickListener {
            val context = gateOutButton.context
            val intent = Intent(context, GateOutActivity::class.java)
            context.startActivity(intent)
        }

        /* When Notification button pressed */
        dataNotiButton.setOnClickListener {
            val context = dataNotiButton.context
            val intent = Intent(context, Notification::class.java)
            context.startActivity(intent)
        }
    }

    /* This is function for exiting application */
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            finishAffinity()
        } else {
            Utils.makeToast(this, "Press back again to leave the app.", Toast.LENGTH_LONG)
        }
        backPressedTime = System.currentTimeMillis()
    }

    /* This is basic alert function */
    private fun basicAlert() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Alert")
            setMessage("The Session has expired please login again")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }

    }

}