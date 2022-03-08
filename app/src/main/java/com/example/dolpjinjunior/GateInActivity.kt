package com.example.dolpjinjunior

/* Import necessary library */
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Utils
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.Calendar

/* This File controls Login Page In gatein_activity.xml */
class GateInActivity : AppCompatActivity() {

    /* This function use for when The layout start*/
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Call for making layout display */
        super.setContentView(R.layout.gatein_activity)

        /* Defined necessary variables */
        var eqtype: String = "HC"
        var eqsize: Float = 20f
        var damage_level: String = ""
        var endDate: String = ""
        val finishDate: String = ""
        val eqtypeOption = arrayOf("HC", "GP")
        val eqsizeOption = arrayOf(20f, 40f)
        var countCheckBoxIsCheck: Int = 0

        /* Get Data from calendar on format : dd/MM/yyyy, for example 12/06/2021 */
        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
        var formatDate = formatter.format(date)

        /* Get Data from many widgets */
        val eqtypeoptionSpinner: Spinner = findViewById(R.id.eqtype)
        val eqsizeoptionSpinner: Spinner = findViewById(R.id.eqsize)
        val containerEditText: EditText = findViewById(R.id.container_id)
        val empty_check: CheckBox = findViewById(R.id.emptyCheck)
        val wait1_check: CheckBox = findViewById(R.id.wait1_check)
        val wait2_check: CheckBox = findViewById(R.id.wait2_check)

        val submitBtn: Button = findViewById(R.id.submit_btn1)
        val menuButton: Button = findViewById(R.id.buttonMenu)
        val backButton: Button = findViewById(R.id.back_btn1)
        val nextButton: Button = findViewById(R.id.next_btn1)

        /* Create Adapter to make drop down menu */
        eqtypeoptionSpinner.adapter =
            ArrayAdapter<String>(this, R.layout.spinner_item, eqtypeOption)
        eqsizeoptionSpinner.adapter = ArrayAdapter(this, R.layout.spinner_item, eqsizeOption)

        /* When press on eqtype drop down */
        eqtypeoptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                eqtype = eqtypeOption[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                eqtype = "HC"
            }
        }

        /* When press on eqsize drop down */
        eqsizeoptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                eqsize = eqsizeOption[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                eqsize = 20f
            }
        }

        /* When select date from calendar */
        calendarView.setOnDateChangeListener { _, i1, i2, i3 ->

            /* Make date to format dd/MM/yyyy */
            formatDate = if (i2 < 10) {
                if (i3 < 10) {
                    "0$i3/0${i2 + 1}/$i1"
                } else {
                    "$i3/0${i2 + 1}/$i1"
                }
            } else {
                if (i3 < 10) {
                    "0$i3/${i2 + 1}/$i1"
                } else {
                    "$i3/${i2 + 1}/$i1"
                }
            }
        }

        /* When submit button is pressed */
        submitBtn.setOnClickListener {

            /* Change damage level according to Check box */
            when {
                empty_check.isChecked -> {
                    damage_level = "Empty"
                    countCheckBoxIsCheck += 1
                }
                wait1_check.isChecked -> {
                    damage_level = "Wait1"
                    countCheckBoxIsCheck += 1
                }
                wait2_check.isChecked -> {
                    damage_level = "Wait2"
                    countCheckBoxIsCheck += 1
                }
            }

            /* Calculate end-date according to damage level*/
            when (damage_level) {
                "Empty" -> endDate = calculateDateEnd(formatDate, 3)
                "Wait1" -> endDate = calculateDateEnd(formatDate, 5)
                "Wait2" -> endDate = calculateDateEnd(formatDate, 30)
            }

            /* Get Container Id on Edittext */
            val container_id: String = containerEditText.text.toString()

            /* Sending data to mongo db process */
            lifecycleScope.launchWhenResumed {
                /* If container id is empty or all checkbox is unchecked, alert message will show up */
                if (container_id == "" || countCheckBoxIsCheck < 1) {
                    Utils.makeToast(
                        this@GateInActivity,
                        "Please select 1 Damage Level or type container id",
                        Toast.LENGTH_SHORT
                    )
                }

                /* Start the process */
                else {
                    val result = try {
                        ApolloClient.Builder()
                            .serverUrl(Config.GRAPHQL_URI)
                            .build()
                            .mutation(
                                AddContainerMutation(
                                    container_id,
                                    eqsize.toDouble(),
                                    eqtype,
                                    damage_level,
                                    formatDate,
                                    endDate,
                                    finishDate,
                                    false
                                )
                            ).addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
                    } catch (err: ApolloException) {
                        throw err
                    }

                    /* If the process fail, show up alert message */
                    if (result.data?.addContainer == null) {
                        Utils.makeToast(
                            this@GateInActivity,
                            result.errors?.get(0)?.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                    }
                    /* If the process success, show up message */
                    else {
                        Utils.makeToast(this@GateInActivity, "Saved Data!!", Toast.LENGTH_SHORT)
                    }
                }
            }
        }

        /* When menu button is pressed */
        menuButton.setOnClickListener {
            val context = menuButton.context
            val intent = Intent(context, MainMenu::class.java)
            context.startActivity(intent)
        }

        /* When back button is pressed */
        backButton.setOnClickListener {
            val context = backButton.context
            val intent = Intent(context, MainMenu::class.java)
            context.startActivity(intent)
        }

        /* When next button is pressed */
        nextButton.setOnClickListener {
            val context = nextButton.context
            val intent = Intent(context, GateOutActivity::class.java)
            context.startActivity(intent)
        }

    }

    /* This is calculate end-date function */
    @SuppressLint("SimpleDateFormat")
    private fun calculateDateEnd(startDate: String, dayAdd: Int): String {
        val formatter = SimpleDateFormat(Config.FORMAT_DATE)
        val dateStart = formatter.parse(startDate)
        val dateTime = DateTime(dateStart)
        val endDate = dateTime.plusDays(dayAdd)
        return formatter.format(endDate.toDate())
    }
}