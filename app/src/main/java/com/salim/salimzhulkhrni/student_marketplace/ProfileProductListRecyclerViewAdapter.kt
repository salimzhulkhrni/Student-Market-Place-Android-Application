package com.salim.salimzhulkhrni.student_marketplace

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ProfileProductListRecyclerViewAdapter(val productList: MutableList<ProductInfoForAdapter>,var choice: Int)
    : RecyclerView.Adapter<ProfileProductListRecyclerViewAdapter.ProductListHolder>(){

    val TAG = "profile_product_rec_adap_fragment"

    private var listener: adapterListener? = null

    private val pRef = FirebaseDatabase.getInstance().getReference().child("Products")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListHolder {

        Log.d(TAG,"Adap - create view")

        val view = when(choice){
            0,1 -> LayoutInflater.from(parent.context).inflate(R.layout.profile_product_list_items_selling_sold,parent,false)
            else ->   LayoutInflater.from(parent.context).inflate(R.layout.profile_product_list_items,parent,false)
        }
        return ProductListHolder(view)
    }

    override fun getItemCount(): Int {

        return productList.size
    }

    override fun onBindViewHolder(holder: ProductListHolder, position: Int) {

        Log.d(TAG,"Adap - Bind view")
        holder.populateProductListDetails(productList[position])
    }

    fun setItemClickListener(listener: adapterListener){

        Log.d(TAG,"initalized recycler adap's listener")
        this.listener = listener
    }

    fun deleteSellingSoldProducts(position: Int){

        Log.d(TAG,"inside delete selling sold products func. Position: "+ position)
        val productKey = productList[position].productKey
        Log.d(TAG,"delete - product name:"+productList[position].productName+" key: "+ productKey)
        // remove from adapter
        productList.removeAt(position)
        notifyDataSetChanged()
        // remove from firebase
        pRef.child(productKey).removeValue()

    }

    interface adapterListener{

        fun onDeleteProductClick(position: Int)
    }

    inner class ProductListHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        init {

            val delete_product_view = itemView.findViewById<ImageView>(R.id.profile_product_delete_product)

            if(delete_product_view != null){

                // delete product view is present

                delete_product_view.setOnClickListener {

                    if(listener != null){

                        if(adapterPosition != RecyclerView.NO_POSITION){

                            Log.d(TAG,"product adapter position: "+adapterPosition)
                            listener!!.onDeleteProductClick(adapterPosition)
                        }
                    }

                }
            }
        }

        fun populateProductListDetails(productInfo: ProductInfoForAdapter){

            val product_name = itemView.findViewById<TextView>(R.id.profile_product_name)
            val product_category = itemView.findViewById<TextView>(R.id.profile_product_category_content)
            val product_image = itemView.findViewById<ImageView>(R.id.profile_product_image)
            val product_price = itemView.findViewById<TextView>(R.id.profile_product_price)

            product_name.text = productInfo.productName
            product_category.text = productInfo.productCategory
            product_price.text = productInfo.productPrice + "$"

            //itemView.profile_product_name.text = productInfo.productName
            //itemView.profile_product_category_content.text = productInfo.productCategory
            val url: String = productInfo.productImagePath
            Picasso.get().load(url).into(product_image)

        }
    }
}