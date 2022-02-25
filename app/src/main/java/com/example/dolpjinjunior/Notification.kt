package com.example.dolpjinjunior

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

class Notification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_activity)

        lifecycleScope.launchWhenResumed {
            initialization()
        }
    }

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
                    2
                ))
            }
        }

        Log.d("LOG-DEBUGGER", "DATA : ${containerList[0].getContainerId()}")

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        val myAdapter : ContainerAdapter = ContainerAdapter(containerList, this)
        recyclerView.adapter = myAdapter

    }
}