package com.aconno.sensorics.ui.settings.publishers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.LongItemClickListener
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RESTPublishModel


import com.aconno.sensorics.ui.settings.publishers.PublishListFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.item_publish.view.*

/**
 * [RecyclerView.Adapter] that can display a [BasePublishModel] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class PublishRecyclerViewAdapter(
    private val mValues: List<BasePublishModel>,
    private val mListener: OnListFragmentInteractionListener?,
    private val mLongItemClickListener: LongItemClickListener<BasePublishModel>?
) : RecyclerView.Adapter<PublishRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val mOnLongClickListener: View.OnLongClickListener
    private var mCheckedChangeListener: OnCheckedChangeListener? = null

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BasePublishModel
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }

        mOnLongClickListener = View.OnLongClickListener {
            val item = it.tag as BasePublishModel
            mLongItemClickListener?.onLongClick(item)
            true
        }
    }

    fun setOnCheckedChangeListener(checkedChangeListener: OnCheckedChangeListener?) {
        this.mCheckedChangeListener = checkedChangeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publish, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mNameView.text = item.name
        holder.mEnableView.isChecked = item.enabled

        when (item) {
            is GooglePublishModel -> holder.mImageView.setImageResource(R.drawable.google_logo)
            is RESTPublishModel -> holder.mImageView.setImageResource(R.drawable.uplaod_cloud)
            is MqttPublishModel -> holder.mImageView.setImageResource(R.drawable.mqtt_logo)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }

        holder.mEnableView.setOnCheckedChangeListener { _, isChecked ->
            mCheckedChangeListener?.onCheckedChange(isChecked, position)
        }

        holder.mView.setOnLongClickListener(mOnLongClickListener)
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView = mView.publish_name
        val mEnableView: Switch = mView.publish_switch
        val mImageView: ImageView = mView.publish_image

        override fun toString(): String {
            return super.toString() + " '" + mEnableView.text + "'"
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, position: Int)
    }

}
