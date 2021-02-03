package com.example.covid19_tracker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.DecimalFormat

class StateAdapter(val list: MutableList<StatewiseItem>) : BaseAdapter() {
    override fun getCount() = list.size


    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        val item = list[position]
        val confirmedTv = view.findViewById<TextView>(R.id.confirmedTv)
        val activeTv = view.findViewById<TextView>(R.id.activeTv)
        val deceasedTv = view.findViewById<TextView>(R.id.deceasedTv)
        val recoveredTv = view.findViewById<TextView>(R.id.recoveredTv)
        val stateTv = view.findViewById<TextView>(R.id.stateTv)
        val confirmed = getFormatedAmount(item.confirmed!!.toInt())
        val active = getFormatedAmount(item.active!!.toInt())
        val recovered = getFormatedAmount(item.recovered!!.toInt())
        val deaths = getFormatedAmount(item.deaths!!.toInt())
        confirmedTv.text = SpannableDelta(
            "${confirmed}\n ↑${item.deltaconfirmed ?: "0"}",
            "#D32F2F",
            confirmed?.length ?: 0
        )
        activeTv.text = SpannableDelta(
            "${active}\n ↑${item.deltaactive ?: "0"}",
            "#1976D2",
            active?.length ?: 0
        )
        recoveredTv.text = SpannableDelta(
            "${recovered}\n ↑ ${item.deltarecovered ?: "0"}",
            "#388E3C",
            recovered?.length ?: 0
        )
        deceasedTv.text = SpannableDelta(
            "${deaths} \n ↑ ${item.deltadeaths ?: "0"}",
            "#FBC02D",
            deaths?.length ?: 0
        )
        stateTv.text = item.state
        return view
    }

    fun getFormatedAmount(amount: Int): String? {
        if (amount > 10000000) {
            val cr = amount / 10000000
            val remainder = amount - cr * 10000000
            val lakh = remainder / 100000
            if (lakh < 10) {
                return "$cr.0$lakh Cr"
            } else {
                return "$cr.$lakh Cr"
            }
        } else if (amount > 100000) {
            val lakh = amount / 100000
            val remainder = amount - lakh * 100000
            val thousand = remainder / 1000
            if (thousand < 10) {
                return "$lakh.0$thousand L"
            } else {
                return "$lakh.$thousand L"
            }
        } else {
            val formatter = DecimalFormat("##,###")
            return formatter.format(amount)
        }
    }
}