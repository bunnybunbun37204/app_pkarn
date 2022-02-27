package com.example.dolpjinjunior

import android.annotation.SuppressLint
import android.content.Intent
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
        val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
        var formatDate = formatter.format(date)

        val eqtypeoptionSpinner : Spinner = findViewById(R.id.eqtype)
        val eqsizeoptionSpinner : Spinner = findViewById(R.id.eqsize)
        val containerEditText : EditText = findViewById(R.id.container_id)
        val empty_check : CheckBox = findViewById(R.id.emptyCheck)
        val wait1_check : CheckBox = findViewById(R.id.wait1_check)
        val wait2_check : CheckBox = findViewById(R.id.wait2_check)

        val submitBtn : Button = findViewById(R.id.submit_btn1)
        val menuButton : Button = findViewById(R.id.buttonMenu)
        val backButton : Button = findViewById(R.id.back_btn1)
        val nextButton : Button = findViewById(R.id.next_btn1)

        val eqtypeOption = arrayOf("HC", "GP")
        val eqsizeOption = arrayOf(20f, 40f)

        var countCheckBoxIsCheck : Int = 0

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


        calendarView.setOnDateChangeListener { _, _, i2, i3 ->
            formatDate = if (i2 < 10) {
                "$i3/0${i2+1}"
            } else {
                "$i3/${i2+1}"
            }

        }


        submitBtn.setOnClickListener {
            when {
                empty_check.isChecked -> {
                    Log.d("LOG-DEBUGGER", "EMPTY")
                    damage_level = "Empty"
                    countCheckBoxIsCheck += 1
                }
                wait1_check.isChecked -> {
                    Log.d("LOG-DEBUGGER", "WAIT1")
                    damage_level = "Wait1"
                    countCheckBoxIsCheck += 1
                }
                wait2_check.isChecked -> {
                    Log.d("LOG-DEBUGGER", "WAIT2")
                    damage_level = "Wait2"
                    countCheckBoxIsCheck += 1
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
                if (container_id == "" || countCheckBoxIsCheck < 1) {
                    Utils.makeToast(this@GateInActivity, "Please select 1 Damage Level or type container id", Toast.LENGTH_SHORT)
                }
                else {
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

        menuButton.setOnClickListener {
            val context = menuButton.context
            val intent = Intent(context, MainMenu::class.java)
            context.startActivity(intent)
        }

        backButton.setOnClickListener {
            val context = backButton.context
            val intent = Intent(context, MainMenu::class.java)
            context.startActivity(intent)
        }

        nextButton.setOnClickListener {
            val context = nextButton.context
            val intent = Intent(context, GateOutActivity::class.java)
            context.startActivity(intent)
        }

    }

    private fun calculateDateEnd(startDate : String, dayAdd : Int) : String {
        val array_date = startDate.split("/")
        Log.d("LOG-DEBUGGER", "ARRAY DATE : $array_date")
        val month = array_date[1].toInt()
        val day = array_date[0].toInt()
        val end_date = day + dayAdd

        return if (end_date > 28 && month == 2) {
            "${end_date - 28}/3"
        } else if (end_date > 31 &&
            (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)) {
            "${end_date - 31}/${month + 1}"
        } else if (end_date > 30 ) {
            "${end_date - 30}/${month + 1}"
        } else {
            "${end_date}/${month}"
        }
    }
}