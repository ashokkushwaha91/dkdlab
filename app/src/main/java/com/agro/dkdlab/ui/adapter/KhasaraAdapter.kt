package com.agro.dkdlab.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.findColor
import com.agro.dkdlab.network.model.SampleList
import kotlinx.android.synthetic.main.khasara_item.view.*

class KhasaraAdapter : RecyclerView.Adapter<KhasaraAdapter.ViewHolder>() {
    private var khasaraList = listOf<SampleList>()
    private var searchedKhasraList = listOf<SampleList>()
    private var listVisibility: ((List<SampleList>) -> Unit)? = null
    private var clickItem: ((SampleList) -> Unit)? = null
    fun setList(list: List<SampleList>) {
        khasaraList = list
        searchedKhasraList = list
        notifyDataSetChanged()
    }
    fun filterList(search:String) {
        searchedKhasraList = khasaraList.filter { it.khasraNumber.startsWith(search) }
        notifyDataSetChanged()
    }

    fun onVisibilityCheck(action: (List<SampleList>) -> Unit) {
        this.listVisibility = action
    }

    fun onItemClick(action: (SampleList) -> Unit) {
        this.clickItem = action
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.khasara_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sampleData = searchedKhasraList[position]
        val sampleStatus = checkNpkExist(sampleData)

        holder.itemView.txtKhasaraNumber.text = sampleData.khasraNumber
        holder.itemView.imgView.setImageResource(if(sampleStatus) R.drawable.ic_checked_circle else R.drawable.ic_pending)

        holder.itemView.apply {
            background.setTint(context.findColor(
                when (sampleStatus) {
                    true-> R.color.green_500
                    else -> R.color.yellow
                }
            ))
        }
        holder.itemView.setOnClickListener {
            clickItem?.invoke(searchedKhasraList[position])
        }
    }
    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount()=searchedKhasraList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun checkNpkExist(sampleData: SampleList):Boolean {
        return sampleData.nrangName.isNotEmpty() && sampleData.phRangName.isNotEmpty() && sampleData.krangName.isNotEmpty() && sampleData.ocRangName.isNotEmpty() && sampleData.ocRangName.isNotEmpty()
    }
}