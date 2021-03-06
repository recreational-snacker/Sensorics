package com.aconno.sensorics.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.livegraph.DaggerLiveGraphComponent
import com.aconno.sensorics.dagger.livegraph.LiveGraphComponent
import com.aconno.sensorics.dagger.livegraph.LiveGraphModule
import com.aconno.sensorics.ui.graph.BleGraph
import com.aconno.sensorics.viewmodel.LiveGraphViewModel
import kotlinx.android.synthetic.main.activity_graph.*
import javax.inject.Inject

class LiveGraphActivity : AppCompatActivity() {

    @Inject
    lateinit var liveGraphViewModel: LiveGraphViewModel

    private val liveGraphComponent: LiveGraphComponent by lazy {
        val sensoricsApplication: SensoricsApplication? = application as? SensoricsApplication
        DaggerLiveGraphComponent.builder()
            .appComponent(sensoricsApplication?.appComponent)
            .liveGraphModule(LiveGraphModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        liveGraphComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        val macAddress = intent.getStringExtra(MAC_ADDRESS_EXTRA)
        val graphName = intent.getStringExtra(GRAPH_NAME_EXTRA)
        liveGraphViewModel.setMacAddressAndGraphName(macAddress, graphName)
        liveGraphViewModel.getUpdates().observe(this, Observer { updateGraph() })
        loadGraph(graphName)
    }


    private fun loadGraph(type: String) {
        val graph: BleGraph = liveGraphViewModel.getGraph(type)
        line_chart.description = graph.getDescription()
        line_chart.data = graph.lineData
        line_chart.invalidate()
    }

    private fun updateGraph() {
        line_chart.data?.notifyDataChanged()
        line_chart.notifyDataSetChanged()
        line_chart.invalidate()
    }

    companion object {

        private const val MAC_ADDRESS_EXTRA = "mac_address"
        private const val GRAPH_NAME_EXTRA = "graph_type"

        fun start(context: Context, macAddress: String, graphName: String) {
            val intent = Intent(context, LiveGraphActivity::class.java)
            intent.putExtra(MAC_ADDRESS_EXTRA, macAddress)
            intent.putExtra(GRAPH_NAME_EXTRA, graphName)
            context.startActivity(intent)
        }
    }
}


