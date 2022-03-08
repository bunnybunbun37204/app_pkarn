package com.example.dolpjinjunior

/* Import necessary library */
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

/* This File controls Login Page In gateout_activity.xml */
class GateOutActivity : AppCompatActivity() {

    /* This function use for when The layout start*/
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Call for making layout display */
        setContentView(R.layout.gateout_activity)

        /* Get Data from calendar on format : dd/MM/yyyy, for example 12/06/2021 */
        val calendarView: CalendarView = findViewById(R.id.calendarView2)
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
        var formatDate = formatter.format(date)

        /* Get Edittext variable*/
        val containerIdEditText: EditText = findViewById(R.id.container_id_query)

        /* Get Button variable */
        val buttonSubmit: Button = findViewById(R.id.submit_btn2)
        val buttonMenu: Button = findViewById(R.id.buttonMenu)
        val backButton: Button = findViewById(R.id.back_btn1)
        val nextButton: Button = findViewById(R.id.next_btn1)

        /* When select date from calendar */
        calendarView.setOnDateChangeListener { _, i1, i2, i3 ->
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

        /* When button submit is pressed*/
        buttonSubmit.setOnClickListener {

            /* Send data to Mongo DB process */
            lifecycleScope.launchWhenResumed {
                /* If container id is empty */
                if (containerIdEditText.text.toString() == "") {
                    Utils.makeToast(
                        this@GateOutActivity,
                        "Please type container id",
                        Toast.LENGTH_SHORT
                    )
                }

                /* Start the process */
                else {
                    val result = try {
                        ApolloClient.Builder().serverUrl(Config.GRAPHQL_URI)
                            .build().mutation(
                                UpdateContainerStatusMutation(
                                    containerIdEditText.text.toString(),
                                    formatDate, true
                                )
                            ).addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
                    } catch (err: ApolloException) {
                        throw err
                    }

                    /* If the process failed, error message will show up */
                    if (result.data?.updateContainerStatus == null) {
                        Utils.makeToast(
                            this@GateOutActivity,
                            result.errors?.get(0)?.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                    }

                    /* if the process success, message will show up */
                    else {
                        Utils.makeToast(this@GateOutActivity, "Submit Data !!!", Toast.LENGTH_SHORT)
                    }
                }
            }

        }

        /* When menu button is pressed */
        buttonMenu.setOnClickListener {
            val context = buttonMenu.context
            val intent = Intent(context, MainMenu::class.java)
            context.startActivity(intent)
        }

        /* When back button is pressed */
        backButton.setOnClickListener {
            val context = backButton.context
            val intent = Intent(context, GateInActivity::class.java)
            context.startActivity(intent)
        }

        /* When next button is pressed */
        nextButton.setOnClickListener {
            val context = nextButton.context
            val intent = Intent(context, Notification::class.java)
            context.startActivity(intent)
        }

    }
}