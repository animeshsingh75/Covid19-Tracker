package com.example.covid19_tracker

import java.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.covid19_tracker.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchResults()
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO) { Client.api.execute() }
            if (response.isSuccessful) {
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main){
                    bindCombinedData(data.statewise?.get(0))
                }
            }
        }

    }

    private fun bindCombinedData(data: StatewiseItem) {
        val lastUpdatedTime=data.lastupdatedtime
        val simpleDateFormat=SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        binding.lastUpdatedTv.text="Last Updated\n ${getTimeAgo(simpleDateFormat.parse(lastUpdatedTime))}"
    }
    fun getTimeAgo(past:Date):String{
        val now=Date()
        val seconds=TimeUnit.MILLISECONDS.toSeconds((now.time)-past.time)
        val minutes=TimeUnit.MILLISECONDS.toMinutes((now.time-past.time))
        val hours=TimeUnit.MILLISECONDS.toHours(now.time-past.time)
        return when{
            seconds<60->{
                "Few seconds ago"
            }
            minutes<60->{
                "$minutes minutes ago"
            }
            hours<24->{
                "$hours hour ${minutes%60} min ago"
            }
            else->{
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }
}