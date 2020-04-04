package com.example.digigit.ui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
//import com.example.digigit.R
import com.jjoe64.graphview.series.LineGraphSeries

import com.jjoe64.graphview.GraphView

import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.fragment_send.*


class SendFragment : Fragment() {

    private lateinit var sendViewModel: SendViewModel
    val weeklyseries = LineGraphSeries<DataPoint>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sendViewModel =
            ViewModelProviders.of(this).get(SendViewModel::class.java)
        val root = inflater.inflate(com.example.digigit.R.layout.fragment_send, container, false)
        val textView: TextView = root.findViewById(com.example.digigit.R.id.text_send)
        sendViewModel.text.observe(this, Observer {
            textView.text = it

            btn_plot.setOnClickListener {
                val weight = root.findViewById<View>(com.example.digigit.R.id.weightText).toString()
                val week = root.findViewById<View>(com.example.digigit.R.id.weekText).toString()

               if(!weight.equals("")&&!week.equals("")){
                   val weightNum: Double = weight.toDouble()
                   val weekNum: Double = week.toDouble()
                   weeklyseries.appendData(DataPoint(weightNum,weekNum),true, 200)


               }
            }

            val graph = root.findViewById(com.example.digigit.R.id.graph) as GraphView

            val gridLabel = graph.getGridLabelRenderer()
            gridLabel.setHorizontalAxisTitle("Weeks")
            gridLabel.verticalAxisTitle = "Weight(Kg)"
            graph.setTitle("Progress")
            graph.addSeries(weeklyseries)





        })
        return root
    }
}