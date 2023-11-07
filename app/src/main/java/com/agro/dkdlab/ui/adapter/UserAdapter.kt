package com.agro.dkdlab.ui.adapter

import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.findColor
import com.agro.dkdlab.custom.Util.inflate
import com.agro.dkdlab.network.model.UserListModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_list_item.view.*

class UserAdapter : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    private var originalUserList = listOf<UserListModel>()
    private var userList = listOf<UserListModel>()
    private var searchedUserList = listOf<UserListModel>()
    private var itemClick: ((UserListModel,String) -> Unit)? = null
    private var listVisibility: ((List<UserListModel>) -> Unit)? = null

    fun setAdapterData(list: List<UserListModel>) {
        this.originalUserList = list
        this.userList = list
        this.searchedUserList = list
        notifyDataSetChanged()
    }
    fun filterData(searchValue: String) {
        searchedUserList = originalUserList.filter { it.userName.lowercase().startsWith(searchValue.lowercase()) || it.primaryPhone.startsWith(searchValue) }
        listVisibility?.invoke(searchedUserList)
        notifyDataSetChanged()
    }

    fun onVisibilityCheck(action: (List<UserListModel>) -> Unit) {
        this.listVisibility = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = parent.inflate(R.layout.user_list_item)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sampleData = searchedUserList[position]
        holder.itemView.textViewUserName.text = "${sampleData.userName?.capitalizeWords()}"
        holder.itemView.textViewMobileNo.text = "${sampleData.primaryPhone}"
        holder.itemView.textViewUserType.text = "${sampleData.userType}"
        if(sampleData.otpVerified){
            holder.itemView.imgVerified.visibility=View.VISIBLE
            holder.itemView.imgUnverified.visibility=View.GONE
        }else{
            holder.itemView.imgVerified.visibility=View.GONE
            holder.itemView.imgUnverified.visibility=View.VISIBLE
        }

        Glide.with(holder.itemView.context).load(sampleData.userPhoto).placeholder(R.drawable.user_profile).transform().into(holder.itemView.imageViewUser)

       sampleData.active?.let {
            if (!sampleData.active) {
                holder.itemView.layout.backgroundTintList = ColorStateList.valueOf(holder.itemView.context.findColor(R.color.lightGray))
            }
            else {
                if(sampleData.otpVerified){
                    holder.itemView.layout.backgroundTintList = ColorStateList.valueOf(holder.itemView.context.findColor(R.color.white))
                }else{
                    holder.itemView.layout.backgroundTintList = ColorStateList.valueOf(holder.itemView.context.findColor(R.color.unverified))
                }
            }
        }
        holder.itemView.setOnClickListener {
            itemClick?.invoke(searchedUserList[position],"view")
        }
    }

    override fun getItemCount()=searchedUserList.size

    override fun getItemId(position: Int)=position.toLong()

    override fun getItemViewType(position: Int)=position

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}