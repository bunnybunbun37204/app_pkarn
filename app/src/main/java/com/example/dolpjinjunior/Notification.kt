package com.example.dolpjinjunior

/* Import necessary library */
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Container
import com.example.dolpjinjunior.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

/* This File controls Login Page In notification_activity.xml */
class Notification : AppCompatActivity() {

    /* This function use for when The layout start*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Call for making layout display */
        setContentView(R.layout.notification_activity)

        /* Down load data from Mongo DB to display in table*/
        lifecycleScope.launchWhenResumed {
            initialization()
        }

        /* Get Button Variable */
        val buttonMenu: Button = findViewById(R.id.buttonMenu)

        buttonMenu.setOnClickListener {
            val context = buttonMenu.context
            val intent = Intent(context, MainMenu::class.java)
            context.startActivity(intent)
        }

    }

    /* This is function for download data from Mongo DB */
    @SuppressLint("SimpleDateFormat")
    private suspend fun initialization() {

        /* declare important variables */
        var count = 0 //for check the container that mus be fixed today
        val idEndDate: MutableList<String> =
            mutableListOf() //for store list of container that must be fixed today

        /* Start download Process */
        val result = try {
            ApolloClient.Builder().serverUrl(Config.GRAPHQL_URI).build()
                .query(AllContainerQuery())
                .addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
        } catch (err: ApolloException) {
            throw err
        }

        /* If list of all container is not empty */
        if (result.data?.all_container?.all_id?.isNotEmpty() == true) {
            val allData = result.data?.all_container?.all_id //store all container data
            val containerList: MutableList<Container> =
                mutableListOf() //create list of all container

            /* if all container data are not null */
            if (allData != null) {
                for (data in allData) {

                    /* store Container list on containerList variable */
                    containerList.add(
                        Container(
                            data.container_id,
                            data.container_size,
                            data.container_type,
                            data.container_damage_level,
                            data.container_date_start,
                            data.container_date_end,
                            data.container_date_finish,
                            data.container_fixed_status,
                            calculateLateDate(data.container_date_end, data.container_date_finish)
                        )
                    )

                    /* for getting current date */
                    val date = Date()
                    val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
                    val current = formatter.format(date)

                    /* to find how many containers must be fixed today */
                    if (current == data.container_date_end && !data.container_fixed_status) {
                        idEndDate.add(data.container_id)
                        count++
                    }
                }

                /*  If 1 container must be fixed, show up alert dialogue message */
                if (count == 1) {
                    basicAlert(idEndDate[0], "must be fixed in today")
                }
                /* if more than 1 container must be fixed, show up alert dialogue message */
                else if (count > 1) {
                    basicAlert(
                        idEndDate[0],
                        "and ${idEndDate.size - 1} more container must be fixed today"
                    )
                }
            }

            /* Declare recycle view variable to display all data as a table */
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView!!.layoutManager = LinearLayoutManager(this)

            /* Create Adapter variable to control the recycle view */
            val myAdapter = ContainerAdapter(containerList, this)
            recyclerView.adapter = myAdapter
        }
        /* If all container data from Mongo DB is empty */
        else {
            Utils.makeToast(this@Notification, "Do not have any Data yet", Toast.LENGTH_LONG)
        }


    }

    /* This is function calculate how long the container has not fixed */
    @SuppressLint("SimpleDateFormat")
    private fun calculateLateDate(endDate: String, _current: String): Int? {
        var current: String = _current
        if (current == "") {
            val date = Date()
            val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
            current = formatter.format(date)
        }
        val formatter = SimpleDateFormat(Config.FORMAT_DATE)
        val dateStart = formatter.parse(endDate)
        val dateFinish = formatter.parse(current)
        val diff: Long = dateFinish?.time?.minus(dateStart?.time!!) ?: 0
        val second: Long = diff / 1000
        val mn: Long = second / 60
        val hrs: Long = mn / 60
        val days: Int = hrs.toInt() / 24
        return when {
            days >= 0 -> {
                return days
            }
            days < 0 -> {
                return days
            }
            else -> {
                null
            }
        }

    }

    /* Nothing please ignore this statement it's call for remove bugs */
    private val positiveButtonClick = { _: DialogInterface, _: Int ->

    }

    /* Alert message for container must be fixed today */
    private fun basicAlert(container_id: String, message: String) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("It's Complete date day")
            setMessage("$container_id $message")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }


    }

}