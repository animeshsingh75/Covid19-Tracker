package com.example.covid19_tracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covid19_tracker.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var stateAdapter: StateAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            list.addHeaderView(LayoutInflater.from(this@MainActivity).inflate(R.layout.item_header,list,false))
        }
        fetchResults()
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO) { Client.api.execute() }
            if (response.isSuccessful) {
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise?.get(0))
                    bindStateWiseData(data.statewise.subList(1,data.statewise.size))
                }
            }
        }

    }

    private fun bindStateWiseData(subList: MutableList<StatewiseItem>) {
        val stateName=subList[35].state
        subList.removeAt(35)
        Log.wtf("State","$stateName")
        stateAdapter= StateAdapter(subList)
        binding.list.adapter=stateAdapter
    }

    private fun bindCombinedData(data: StatewiseItem) {
        val lastUpdatedTime = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        binding.apply {
            lastUpdatedTv.text =
                "Last Updated\n ${getTimeAgo(simpleDateFormat.parse(lastUpdatedTime))}"
            val confirmed = getFormatedAmount(data.confirmed!!.toInt())
            val active = getFormatedAmount(data.active!!.toInt())
            val deaths = getFormatedAmount(data.deaths!!.toInt())
            val recovered = getFormatedAmount(data.recovered!!.toInt())
            confirmedTv.text = confirmed
            activeTv.text = active
            deceasedTv.text = deaths
            recoveredTv.text = recovered
        }

    }

    private fun getFormatedAmount(amount: Int): String? {
        if (amount > 10000000) {
            var cr = amount / 10000000
            val remainder = amount-cr*10000000
            val lakh = remainder / 100000
            if(lakh<10){
                return "$cr.0$lakh Cr"
            }
            else{
                return "$cr.$lakh Cr"
            }
        }
        else if(amount>100000){
            var lakh = amount / 100000
            val remainder = amount-lakh*100000
            val thousand = remainder / 1000
            if(thousand<10){
                return "$lakh.0$thousand L"
            }
            else{
                return "$lakh.$thousand L"
            }
        }
        else{
            val formatter = DecimalFormat("##,###")
            return formatter.format(amount)
        }
    }

    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds((now.time) - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes((now.time - past.time))
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }
}