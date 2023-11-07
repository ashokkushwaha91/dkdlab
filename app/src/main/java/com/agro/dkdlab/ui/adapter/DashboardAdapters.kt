package com.agro.dkdlab.ui.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.inflate
import com.agro.dkdlab.network.model.DashboardModel
import com.agro.dkdlab.network.model.Village
import kotlinx.android.synthetic.main.item_dashboard_new.view.*

class DashboardAdapters : RecyclerView.Adapter<DashboardAdapters.MyViewHolder>() {
    private var villageList = listOf<Village>()
    private var filteredVillageList = listOf<Village>()
    private var itemClick: ((Village,String) -> Unit)? = null
    private var listVisibility: ((List<Village>) -> Unit)? = null
    fun setAdapterData(list: List<Village>) {
        this.villageList = list
        this.filteredVillageList = list
        notifyDataSetChanged()
    }
    fun filterData(villageName: String) {
        filteredVillageList = villageList.filter { it.value1.lowercase().startsWith(villageName.lowercase()) }
        listVisibility?.invoke(filteredVillageList)
        notifyDataSetChanged()
    }
    fun setItemClick(action: (Village,String) -> Unit) {
        this.itemClick = action
    }
   fun onVisibilityCheck(action: (List<Village>) -> Unit) {
        this.listVisibility = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.item_dashboard_new)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var village=filteredVillageList[position]
        holder.itemView.textViewVillageName.text = "${village.value1.capitalizeWords()}"
        holder.itemView.txtSampleCount.text = "${village.value2}"
        holder.itemView.parentLayout.setOnClickListener {
            itemClick?.invoke(village,"view")
        }
    }

    override fun getItemCount(): Int {
        return filteredVillageList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    }
}