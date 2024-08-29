package com.agbellerive.passwordmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccountsAdapter (private val allAccounts: ArrayList<Account>): RecyclerView.Adapter<CustomViewHolder>() {

    private lateinit var mlistner: onClickListner //https://www.youtube.com/watch?v=dB9JOsVx-yY

    interface onClickListner{
        fun onItemClick( position: Int)
    }

    fun setOnItemClickListiner (listiner : onClickListner){
        mlistner = listiner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.account_list_cell,parent,false)
        return CustomViewHolder(cell,mlistner)
    }

    override fun getItemCount() = allAccounts.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val account = allAccounts[position]
        holder.itemView.findViewById<TextView>(R.id.cellAccount).text = account.Site
    }
}

class CustomViewHolder(view: View, listiner: AccountsAdapter.onClickListner) : RecyclerView.ViewHolder(view){
    init{
        view.setOnClickListener {
            listiner.onItemClick(adapterPosition)
        }
    }
}