package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.model.Device
import kotlinx.android.synthetic.main.item_device.view.*
import timber.log.Timber

class DeviceAdapter(
    private val devices: MutableList<Device>,
    private val itemClickListener: ItemClickListener<Device>
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    fun addDevice(device: Device) {
        devices.add(device)
        notifyDataSetChanged()
    }

    fun setDevices(devices: List<Device>) {
        this.devices.clear()
        this.devices.addAll(devices)
        notifyDataSetChanged()
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(device: Device) {
            Timber.d("Bind device to view, name: ${device.name}, mac: ${device.macAddress}, icon: ${device.icon}")
            val iconId = view.context.resources.getIdentifier(
                device.icon,
                "drawable",
                view.context.packageName
            )
            view.image_icon.setImageResource(iconId)
            view.name.text = device.name
            view.mac_address.text = device.macAddress
            view.setOnClickListener { itemClickListener.onItemClick(device) }
        }
    }
}