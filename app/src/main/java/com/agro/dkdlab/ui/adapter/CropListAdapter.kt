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
import com.agro.dkdlab.network.model.FertCropModel
import kotlinx.android.synthetic.main.crop_list_item.view.txt_name

class CropListAdapter : RecyclerView.Adapter<CropListAdapter.MyViewHolder>(), Filterable {
    private var cropList = listOf<FertCropModel>()
    private var cropVillageList = listOf<FertCropModel>()
    private var itemClick: ((FertCropModel) -> Unit)? = null
    private var listVisibility: ((List<FertCropModel>) -> Unit)? = null

    fun setVillageData(list: List<FertCropModel>) {
        this.cropList = list
        this.cropVillageList = list
        notifyDataSetChanged()
    }

    fun setItemClick(action: (FertCropModel) -> Unit) {
        this.itemClick = action
    }
    fun onVisibilityCheck(action: (List<FertCropModel>) -> Unit) {
        this.listVisibility = action
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.crop_list_item)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val villageVillageCode = cropVillageList[position]
        holder.itemView.txt_name.text = "${villageVillageCode.cropName?.capitalizeWords()}"
        holder.itemView.alpha=if(villageVillageCode.seed==true) 1.0f else 0.5f
        /*villageVillageCode.seed?.let {
            if(it)
                holder.itemView.setBackgroundResource(R.drawable.videos_text_bg)
            else
                holder.itemView.setBackgroundResource(R.drawable.videos_text_bg_disable)
        }*/
        holder.itemView.txt_name.setOnClickListener {
            itemClick?.invoke(cropVillageList[position])
        }
    }

    override fun getItemCount(): Int {
        return cropVillageList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                cropVillageList = if (charString.isEmpty()) {
                    cropList
                } else {
                    Log.e("searchVillage", charString)
                    val filteredList: MutableList<FertCropModel> = ArrayList()
                    cropList.forEach { villageList ->
                        if (villageList.cropName!!.lowercase().startsWith(charString.lowercase())) {
                            filteredList.add(villageList)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = cropVillageList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                cropVillageList = filterResults.values as List<FertCropModel>
                listVisibility?.invoke(cropVillageList)
                notifyDataSetChanged()
            }
        }
    }

}