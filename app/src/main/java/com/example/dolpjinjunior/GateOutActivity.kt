package com.example.dolpjinjunior

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

class GateOutActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gateout_activity)

        val calendarView : CalendarView = findViewById(R.id.calendarView2)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
        var formatDate = formatter.format(date)

        val containerIdEditText : EditText = findViewById(R.id.container_id_query)
        val buttonSubmit : Button = findViewById(R.id.submit_btn2)

        calendarView.setOnDateChangeListener { _, _, i2, i3 ->
            formatDate = if (i2 < 10) {
                "$i3/0${i2+1}"
            } else {
                "$i3/${i2+1}"
            }
        }

        buttonSubmit.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                if (containerIdEditText.text.toString() == "") {
                    Utils.makeToast(this@GateOutActivity, "Please type container id", Toast.LENGTH_SHORT)
                }
                else {
                    val result = try {
                        ApolloClient.Builder().serverUrl(Config.GRAPHQL_URI)
                            .build().mutation(UpdateContainerStatusMutation(containerIdEditText.text.toString(),
                                formatDate, true)).addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
                    } catch (err : ApolloException) {
                        throw err
                    }
                    if (result.data?.updateContainerStatus == null) {
                        Utils.makeToast(this@GateOutActivity, result.errors.toString(), Toast.LENGTH_SHORT)
                    }
                    else {
                        Utils.makeToast(this@GateOutActivity, "Submit Data !!!", Toast.LENGTH_SHORT)
                    }
                    Log.i("LOG-INFO","DATA ${result.data}")
                }
            }

        }


    }
}