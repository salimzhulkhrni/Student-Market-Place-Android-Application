package com.salim.salimzhulkhrni.student_marketplace

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class BrowsingRecyclerViewAdapter(var productList: ArrayList<ProductData>,val key: String)
    : RecyclerView.Adapter<BrowsingRecyclerViewAdapter.ViewHolder>(){

    val TAG = "browsing_adap_fragment"

    private var listener: adapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        Log.d(TAG,"on create view")
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.browsing_card_view,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d(TAG,"OnBindHolder")
        Picasso.get().load(productList[position].url).into(holder!!.imageview)
        holder.title.setText(productList[position].name)
        holder.spinner_text.setText("$"+productList[position].price.toString())

        //Log.i("ProductFlag",productFlag.toString())
        //Log.i("ProductKey",productKey[position])

        if(productList[position].favorites){
            holder.favoriteHeart.setImageResource(R.drawable.color_heart)
        }
        else{
            holder.favoriteHeart.setImageResource(R.drawable.uncolor_heart)
        }

        /*if(productFlag.contains(productKey[position])){
            holder!!.favoriteHeart.setImageResource(R.drawable.color_heart)
        }*/

        holder.favoriteHeart.setOnClickListener{
            var signal = "contains"
            if(productList[position].favorites){
                productList[position].favorites = false
                holder.favoriteHeart.setImageResource(R.drawable.uncolor_heart)
            }
            else{
                holder.favoriteHeart.setImageResource(R.drawable.color_heart)
                productList[position].favorites = true
                signal = "notcontains"
            }
            listener!!.loadProductActivity(productList[position],signal)
        }

        holder.imageview.setOnClickListener {

            listener!!.loadProductActivity(productList[position],"imageView")
        }
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val imageview = v.findViewById<ImageView>(R.id.image)
        val title = v.findViewById<TextView>(R.id.title)
        val spinner_text = v.findViewById<TextView>(R.id.spinner_text)
        val favoriteHeart : ImageView = v.findViewById<ImageView>(R.id.favoriteHeart)
    }

    fun adapterClickListener(listener: adapterListener){
        this.listener = listener
    }

    interface adapterListener{
        fun loadProductActivity(productType: ProductData, flag: String)
    }

    fun updateData(pdList: ArrayList<ProductData>){

        Log.d(TAG,"inside update data func")
        productList = pdList
        notifyDataSetChanged()

    }

}