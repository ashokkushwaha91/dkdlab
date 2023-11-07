package com.agro.dkdlab.ui.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.constant.listIrrigation
import com.agro.dkdlab.custom.Util.inflate
import kotlinx.android.synthetic.main.item_irrigation_multiple_selction.view.*

class IrrigationAdapter(private val selectedIrrigationList: MutableList<String>) : RecyclerView.Adapter<IrrigationAdapter.MyViewHolder>() {
    private var irrigationList = listIrrigation["English"]!!
    fun getSelectedSources() = selectedIrrigationList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.item_irrigation_multiple_selction)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.textViewName.text = irrigationList[position]
        holder.itemView.checkBoxIrrigation.isChecked = selectedIrrigationList.contains(irrigationList[position])
        holder.itemView.checkBoxIrrigation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                selectedIrrigationList.add(irrigationList[position])
            }else{
                selectedIrrigationList.remove(irrigationList[position])
            }

            Log.e("itemSize",selectedIrrigationList.size.toString() )
        }
        holder.itemView.setOnClickListener {
            holder.itemView.checkBoxIrrigation.isChecked= !holder.itemView.checkBoxIrrigation.isChecked
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return irrigationList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}