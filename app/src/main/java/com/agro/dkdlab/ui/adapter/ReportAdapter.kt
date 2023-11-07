package com.agro.dkdlab.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.inflate
import com.agro.dkdlab.ui.database.entities.ReportEntity
import kotlinx.android.synthetic.main.reports_list_item.view.*
import javax.inject.Inject

class ReportAdapter @Inject constructor() : RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {
    private var userList = listOf<ReportEntity>()
    private var searchedUserList = listOf<ReportEntity>()
    private var itemClick: ((ReportEntity) -> Unit)? = null
    private var listVisibility: ((List<ReportEntity>) -> Unit)? = null

    fun setAdapterData(list: List<ReportEntity>) {
        this.userList = list
        this.searchedUserList = list
        notifyDataSetChanged()
    }

    fun onFilter(key: String){
        searchedUserList = if(key.isEmpty()){
            userList
        }else
            userList.filter { it.name.contains(key) || it.sampleBarCode.contains(key) }
        notifyDataSetChanged()
    }
    fun onItemClick(action: (ReportEntity) -> Unit) {
        this.itemClick = action
    }

    fun onVisibilityCheck(action: (List<ReportEntity>) -> Unit) {
        this.listVisibility = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.reports_list_item)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sampleData = searchedUserList[position]
        holder.itemView.textViewName.text = "${sampleData.name.capitalizeWords()}"
        holder.itemView.textViewBarcode.text = "${sampleData.sampleBarCode}"
//        holder.itemView.textViewTest.text = "${sampleData.testParametersModel.map { it.testType }.joinToString { it }}"
        holder.itemView.textViewTest.text = if(sampleData.testParametersModel.size<10)"${sampleData.testParametersModel.size}/10" else "Completed"
        holder.itemView.textViewDate.text = "${sampleData.date}"
        holder.itemView.setOnClickListener {
            itemClick?.invoke(sampleData)
        }
    }

    override fun getItemCount()=searchedUserList.size
    override fun getItemId(position: Int)=position.toLong()
    override fun getItemViewType(position: Int)= position
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}