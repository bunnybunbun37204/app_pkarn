package com.example.dolpjinjunior

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils

class MainMenu : AppCompatActivity() {
    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.initialize(this@MainMenu, Config.SECRET_KEY)
        Log.d("LOG-DEBUGGER", "CURRENT TOKEN : ${Config.USER_TOKEN}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        val logoutButton : Button = findViewById(R.id.button_logout)

        logoutButton.setOnClickListener {
            Config.STATUS_BUG = 1
            Utils.clearData(this@MainMenu)
            val context = logoutButton.context
            val intent = Intent(context, MainActivity::class.java)
            Log.d("LOG-DEBUGGER", "TOKEN : ${Config.USER_TOKEN}")
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
}