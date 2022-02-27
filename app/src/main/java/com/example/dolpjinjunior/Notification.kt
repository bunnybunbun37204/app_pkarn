package com.example.dolpjinjunior

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.dolpjinjunior.utils.Config
import com.example.dolpjinjunior.utils.Container
import java.text.SimpleDateFormat
import java.util.*

class Notification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_activity)

        lifecycleScope.launchWhenResumed {
            initialization()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun initialization() {


        val result = try {
            ApolloClient.Builder().serverUrl(Config.GRAPHQL_URI).build()
                .query(AllContainerQuery())
                .addHttpHeader("authorization", Config.USER_TOKEN.toString()).execute()
        } catch (err : ApolloException) {
            throw err
        }
        val allData = result.data?.all_container?.all_id
        val containerList : MutableList<Container> = mutableListOf()

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(Config.FORMAT_DATE) //or use getDateInstance()
        val formatDate = formatter.format(date)

        if (allData != null) {
            for (data in allData){
                containerList.add(Container(
                    data.container_id,
                    data.container_size,
                    data.container_type,
                    data.container_damage_level,
                    data.container_date_start,
                    data.container_date_end,
                    data.container_date_finish,
                    data.container_fixed_status,
                    calculateLateDate(data.container_date_end, data.container_date_finish)
                ))
            }
        }

        Log.d("LOG-DEBUGGER", "DATA : ${containerList[0].getContainerId()}")


        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        val myAdapter = ContainerAdapter(containerList, this)
        recyclerView.adapter = myAdapter

    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateLateDate(endDate: String, current: String): Int? {
        if (current == "") return null
        val formatter = SimpleDateFormat(Config.FORMAT_DATE)
        val dateStart = formatter.parse(endDate)
        val dateFinish = formatter.parse(current)
        val diff: Long = dateFinish?.time?.minus(dateStart?.time!!) ?: 0
        val second: Long = diff / 1000
        val mn: Long = second / 60
        val hrs: Long = mn / 60
        val days : Int = hrs.toInt() / 24
        return if (days >= 0) {
            return days
        } else {
            null
        }

    }

}