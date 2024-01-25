package com.agro.dkdlab.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.inflate
import com.agro.dkdlab.network.model.SoilSampleModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.sample_list_item_new.view.*

class SampleListAdapter(val list: List<SoilSampleModel>, userRole: String) :
    RecyclerView.Adapter<SampleListAdapter.MyViewHolder>(){
    private var sampleList = listOf<SoilSampleModel>()
    private var searchSampleList = listOf<SoilSampleModel>()
    private var itemClick: ((SoilSampleModel) -> Unit)? = null
    private var listVisibility: ((List<SoilSampleModel>) -> Unit)? = null
    private var userType:String=""

    init {
        this.sampleList = list
        this.searchSampleList = list
        userType=userRole
    }
    fun filter(s: String) {
        searchSampleList=sampleList.filter { it.farmerName?.lowercase()!!.contains(s.lowercase()) }
        notifyDataSetChanged()
    }
    fun onItemClick(action: (SoilSampleModel) -> Unit) {
        this.itemClick = action
    }
    fun onVisibilityCheck(action: (List<SoilSampleModel>) -> Unit) {
        this.listVisibility = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.sample_list_item_new)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sampleData = searchSampleList[position]
        holder.itemView.textViewKhasra.text ="${holder.itemView.context.getString(R.string.khasra)} : ${sampleData.khasraNumber}"
        holder.itemView.textViewFarmerName.text = "${sampleData.farmerName?.capitalizeWords()}"
        holder.itemView.textViewSize.text = "${holder.itemView.context.getString(R.string.farm_size)} : ${sampleData.farmSize} ${holder.itemView.context.getString(R.string.acre)} "
        holder.itemView.textViewLatLng.text = "Lat, Lng : ${"%.5f".format(sampleData.latitude)}, ${"%.5f".format(sampleData.longitude)}"

        holder.itemView.textViewLatLng.visibility=if(sampleData.latitude==0.0) View.GONE else View.VISIBLE
        holder.itemView.setOnClickListener {
            itemClick?.invoke(searchSampleList[position])
        }
        sampleData.farmerPhoto?.let {
            Glide.with(holder.itemView.context).load(it).into(holder.itemView.imageViewSoilSamplePacket)
        }

    }

    override fun getItemCount(): Int {
        return searchSampleList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}