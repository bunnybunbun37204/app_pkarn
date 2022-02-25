package com.example.dolpjinjunior

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar

class GateInActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.gatein_activity)

        var eqtype : String = "HC"
        var eqsize : Float = 20f
        var damage_level : String = ""
        var endDate : String = ""
        val finishDate : String = ""

        val calendarView : CalendarView = findViewById(R.id.calendarView)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy.M.dd") //or use getDateInstance()
        var formatDate = formatter.format(date)

        val eqtypeoptionSpinner : Spinner = findViewById(R.id.eqtype)
        val eqsizeoptionSpinner : Spinner = findViewById(R.id.eqsize)
        val containerEditText : EditText = findViewById(R.id.container_id)
        val empty_check : CheckBox = findViewById(R.id.emptyCheck)
        val wait1_check : CheckBox = findViewById(R.id.wait1_check)
        val wait2_check : CheckBox = findViewById(R.id.wait2_check)
        val submitBtn : Button = findViewById(R.id.submit_btn1)

        val eqtypeOption = arrayOf("HC", "GP")
        val eqsizeOption = arrayOf(20f, 40f)

        eqtypeoptionSpinner.adapter = ArrayAdapter<String>(this, R.layout.spinner_item, eqtypeOption)
        eqsizeoptionSpinner.adapter = ArrayAdapter(this, R.layout.spinner_item, eqsizeOption)

        eqtypeoptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("LOG-DeBUGGER","Int ps : $p2")
                eqtype = eqtypeOption[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                eqtype = "HC"
            }
        }

        eqsizeoptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                eqsize = eqsizeOption[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                eqsize = 20f
            }
        }


        calendarView.setOnDateChangeListener { _, i, i2, i3 ->
            formatDate = "$i.${i2+1}.$i3"
        }


        submitBtn.setOnClickListener {
            when {
                empty_check.isChecked -> {
                    Log.d("LOG-DEBUGGER", "EMPTY")
                    damage_level = "Empty"
                }
                wait1_check.isChecked -> {
                    Log.d("LOG-DEBUGGER", "WAIT1")
                    damage_level = "Wait1"
                }
                wait2_check.isChecked -> {
                    Log.d("LOG-DEBUGGER", "WAIT2")
                    damage_level = "Wait2"
                }
            }

            when (damage_level) {
                "Empty" -> endDate = calculateDateEnd(formatDate, 3)
                "Wait1" -> endDate = calculateDateEnd(formatDate, 5)
                "Wait2" -> endDate = calculateDateEnd(formatDate, 30)
            }

            val container_id : String = containerEditText.text.toString()
            Log.d("LOG-DEBUGGER", "ID : $container_id SIZE : $eqsize Type : $eqtype" +
                    " Start Date : $formatDate End Date $endDate Damage Level $damage_level")

            lifecycleScope.launchWhenResumed {
                val result = try {
                    ApolloClient.Builder()
                        .serverUrl(Config.GRAPHQL_URI)
                        .build()
                        .mutation(AddContainerMutation(
                            container_id,
                            eqsize.toDouble(),
                            eqtype,
                            damage_level,
                            formatDate,
                            endDate,
                            finishDate,
                            false
                        )).addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
                } catch (err : ApolloException) {
                    throw err
                }
                Utils.makeToast(this@GateInActivity, "Saved Data!!", Toast.LENGTH_SHORT)
                Log.d("LOG-DEBUGGER", "DATA : ${result.data}")
            }
        }

    }

    private fun calculateDateEnd(startDate : String, dayAdd : Int) : String {
        val array_date = startDate.split(".")
        val month = array_date[1].toInt()
        val day = array_date[2].toInt()
        val end_date = day + dayAdd

        return if (end_date > 28 && month == 2) {
            "${array_date[0]}.3.${end_date - 28}"
        } else if (end_date > 31 &&
            (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) {
            "${array_date[0]}.${month + 1}.${end_date - 31}"
        } else if (end_date > 30 ) {
            "${array_date[0]}.${month + 1}.${end_date - 30}"
        } else {
            "${array_date[0]}.${month}.${end_date}"
        }
    }
}