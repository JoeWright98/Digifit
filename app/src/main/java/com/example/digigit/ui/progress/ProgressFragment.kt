package com.example.digigit.ui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.digigit.R
import com.jjoe64.graphview.series.LineGraphSeries

import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport

import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_progress.*




class ProgressFragment : Fragment() {

    private lateinit var sendViewModel: SendViewModel
    internal lateinit var tvWeight:TextView
    internal lateinit var tvWeek:TextView
    internal lateinit var btnPlot:Button
    internal var myWeight:Double = 0.0
    internal var myWeek: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sendViewModel =
            ViewModelProviders.of(this).get(SendViewModel::class.java)
        val root = inflater.inflate(com.example.digigit.R.layout.fragment_progress, container, false)
        val textView: TextView = root.findViewById(com.example.digigit.R.id.text_send)
        sendViewModel.text.observe(this, Observer {
            textView.text = it

            tvWeek = root.findViewById(R.id.weekText)
            tvWeight = root.findViewById(R.id.weightText)
            btnPlot = root.findViewById(R.id.btn_plot
            )
            btn_plot.setOnClickListener (object: View.OnClickListener {
                override fun onClick(v: View?) {
                    val weeklyseries = LineGraphSeries<DataPoint>()

                    myWeight = tvWeight.getText().toString().toDouble()
                    myWeek = tvWeek.getText().toString().toDouble()

                    weeklyseries.appendData(DataPoint(myWeek, myWeight), true, 200)
                    //weeklyseries.
                    print(tvWeek.text)
                    print(tvWeight.text)
                    //graph.addSeries(weeklyseries)


                }
            })



            var graph = root.findViewById(com.example.digigit.R.id.graph) as GraphView
            val gridLabel = graph.getGridLabelRenderer()
            val viewport = graph.viewport
            viewport.setMinX(1.0)
            viewport.setMinY(1.0)
            viewport.setMaxX(200.0)
            viewport.setMaxY(200.0)

            gridLabel.setHorizontalAxisTitle("Weeks")
            gridLabel.verticalAxisTitle = "Weight(Kg)"
            graph.setTitle("Progress")






        })
        return root
    }
}