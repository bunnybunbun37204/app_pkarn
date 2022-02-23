package com.example.dolpjinjunior

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import java.text.SimpleDateFormat
import java.util.Calendar

class GateOutActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gateout_activity)

        val calendarView : CalendarView = findViewById(R.id.calendarView2)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy.M.dd") //or use getDateInstance()
        var formatDate = formatter.format(date)

        val containerIdEditText : EditText = findViewById(R.id.container_id_query)
        val buttonSubmit : Button = findViewById(R.id.submit_btn2)

        calendarView.setOnDateChangeListener { _, i, i2, i3 ->
            formatDate = "$i.${i2+1}.$i3"
        }

        buttonSubmit.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                val GRAPH_URL = "http://192.168.1.31:4000/"
                val result = try {
                    ApolloClient.Builder().serverUrl(GRAPH_URL)
                        .build().mutation(UpdateContainerStatusMutation(containerIdEditText.text.toString(),
                            formatDate, true)).addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
                } catch (err : ApolloException) {
                    throw err
                }
                Log.i("LOG-INFO","DATA ${result.data}")
            }

        }


    }
}