package com.agro.dkdlab.ui.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.constant.NPKMOdel
import com.agro.dkdlab.custom.Util.showToast
import kotlinx.android.synthetic.main.custom_dropdown.view.*

class SpinnerAdapter(val context: Context, var list: List<NPKMOdel>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_dropdown, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.itemView.name.text = list[position].name
        vh.itemView.range.text = list[position].value
        return view
    }

    override fun getItem(position: Int)=list[position]
    override fun getCount()=list.size
    override fun getItemId(position: Int)= position.toLong()
    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}