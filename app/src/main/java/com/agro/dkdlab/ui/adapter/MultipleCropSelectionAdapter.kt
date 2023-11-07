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
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.network.model.CropModel
import kotlinx.android.synthetic.main.item_village_multiple_selction.view.*

class MultipleCropSelectionAdapter : RecyclerView.Adapter<MultipleCropSelectionAdapter.MyViewHolder>(), Filterable {
    private var villageList = listOf<CropModel>()
    private var searchedVillageList = listOf<CropModel>()
    private var itemClick: ((CropModel) -> Unit)? = null
    var checkedItems = listOf<CropModel>().toMutableList()
    private var otherCrop:String?=null

    private var listVisibility: ((List<CropModel>) -> Unit)? = null
    fun setCropData(list: List<CropModel>, selected: List<CropModel>?, otherCrop: String) {
        selected?.let { checkedItems.addAll(it) }
        var otherObject=checkedItems.find { it.cropName=="Other Crop" }
        if(otherObject==null) {
            if(!otherCrop.isNullOrEmpty()){
                list.find { it.cropName == "Other Crop" }
                    ?.let {
                            it1 -> checkedItems.add(it1)
                    }
            }
        }
        this.villageList = list
        this.searchedVillageList = list
        this.otherCrop=otherCrop
        notifyDataSetChanged()
    }
    fun getSelectedItems() = checkedItems
    fun setItemClick(action: (CropModel) -> Unit) {
        this.itemClick = action
    }
    fun onVisibilityCheck(action: (List<CropModel>) -> Unit) {
        this.listVisibility = action
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.item_village_multiple_selction)
        return MyViewHolder(view)
    }

    private fun CropModel.isExist(): Boolean{
        return checkedItems.find { it.cropName == cropName } != null
    }
    private fun CropModel.otherCropisExist(): Boolean{
        return this.cropName=="Other Crop" && !otherCrop.isNullOrEmpty()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cropData = searchedVillageList[position]
        holder.itemView.txt_village.text = "${cropData.cropName?.capitalizeWords()}"
        holder.itemView.appCompatCheckBox.isChecked= cropData.isExist()
//        if(cropData.cropName=="Other Crop") holder.itemView.appCompatCheckBox.isChecked= cropData.otherCropisExist()
        holder.itemView.constraintLayout.setOnClickListener {
//            holder.itemView.appCompatCheckBox.isChecked= !holder.itemView.appCompatCheckBox.isChecked
            //itemClick?.invoke(searchVillageFiltered[position])
            val item = checkedItems.find { it.cropName ==  searchedVillageList[position].cropName}
           when{
               !checkedItems.contains(item) && searchedVillageList[position].cropName=="No Crop"->{
                   checkedItems.clear()
                   notifyDataSetChanged()
                   checkedItems.add(searchedVillageList[position])
                   holder.itemView.appCompatCheckBox.isChecked= true
               }
               !checkedItems.contains(item) && checkedItems.size<3->{
                   val noCropExist = checkedItems.filter { it.cropName ==  "No Crop"}
                   if(!noCropExist.isNullOrEmpty()){
                       checkedItems.clear()
                       notifyDataSetChanged()
                   }
                   checkedItems.add(searchedVillageList[position])
                   holder.itemView.appCompatCheckBox.isChecked= true
               }
               !checkedItems.contains(item) && checkedItems.size>=3->holder.itemView.context.showToast("You can select maximum 3 crops")
               else ->{
                   checkedItems.remove(item)
                   holder.itemView.appCompatCheckBox.isChecked=false
                   Log.e("checkedItems:",checkedItems.size.toString() )
               }
           }
        }
    }

    override fun getItemCount(): Int {
        return searchedVillageList.size
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
                searchedVillageList = if (charString.isEmpty()) {
                    villageList
                } else {
                    Log.e("searchVillage", charString)
                    val filteredList: MutableList<CropModel> = ArrayList()
                    for (villageList in villageList) {
                        if (villageList.cropName?.lowercase()!!.startsWith(charString.lowercase())) {
                            filteredList.add(villageList)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = searchedVillageList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                searchedVillageList = filterResults.values as List<CropModel>
                listVisibility?.invoke(searchedVillageList)
                notifyDataSetChanged()
            }
        }
    }

}