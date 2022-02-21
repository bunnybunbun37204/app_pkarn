package com.example.dolpjinjunior.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Utils {

    companion object {
        fun makeToast(context : AppCompatActivity, text : String, length : Int) {
            Toast.makeText(context, text, length).show()
        }
    }
}