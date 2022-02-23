package com.example.dolpjinjunior

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils

class MainMenu : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        Config.STATUS_BUG = 1
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LOG-DEBUGGER", "CURRENT TOKEN : ${Config.USER_TOKEN}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        lifecycleScope.launchWhenResumed {
            val GRAPH_URL = "http://192.168.1.31:4000/"
            Log.i("LOG-INFO","TOKEN : ${Config.USER_TOKEN}")
            val data = ApolloClient.Builder().serverUrl(GRAPH_URL).build()
                .query(QuerytokenUserQuery())
                .addHttpHeader("authorization",Config.USER_TOKEN.toString()).execute()
            val isTokenize : String = data.data?.me?.username.toString()
            Log.i("LOG-INFO","STATUS : $isTokenize")
            if (isTokenize == "null") {
                basicAlert()
            }
        }

        val logoutButton : Button = findViewById(R.id.button_logout)
        val gateInButton : Button = findViewById(R.id.gatein_btn)
        val gateOutButton : Button = findViewById(R.id.gateout_btn)

        logoutButton.setOnClickListener {
            Config.STATUS_BUG = 1
            Utils.clearData(this@MainMenu)
            val context = logoutButton.context
            val intent = Intent(context, MainActivity::class.java)
            Log.d("LOG-DEBUGGER", "TOKEN : ${Config.USER_TOKEN}")
            context.startActivity(intent)
        }

        gateInButton.setOnClickListener {
            val context = gateInButton.context
            val intent = Intent(context, GateInActivity::class.java)
            context.startActivity(intent)
        }

        gateOutButton.setOnClickListener {
            val context = gateOutButton.context
            val intent = Intent(context, GateOutActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            finishAffinity()
        } else {
            Utils.makeToast(this, "Press back again to leave the app.", Toast.LENGTH_LONG)
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun basicAlert(){

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