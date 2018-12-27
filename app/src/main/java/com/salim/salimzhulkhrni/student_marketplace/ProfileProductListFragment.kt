package com.salim.salimzhulkhrni.student_marketplace

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.profile_product_list_fragment.*

data class ProductInfoForAdapter(
    var productName: String,
    var productCategory: String,
    var productImagePath: String,
    var productKey: String,
    var productPrice: String
)

class ProfileProductListFragment: Fragment(), ProfileProductListRecyclerViewAdapter.adapterListener{


    val TAG = "profile_product_fragment"

    lateinit var profile_product_adapter: ProfileProductListRecyclerViewAdapter
    lateinit var productListObjects: MutableList<ProductInfoForAdapter>

    companion object {

        fun newInstance(choice: Int): ProfileProductListFragment{

            val args = Bundle()
            val fragment = ProfileProductListFragment()
            args.putInt("choice",choice)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        return inflater.inflate(R.layout.profile_product_list_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(view.context)
        profile_recyclerView.layoutManager = layoutManager
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        setUpRecyclerViewWithData()

    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "on attach")
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "activitycreated")
        super.onActivityCreated(savedInstanceState)

    }

    override fun onDestroy() {
        Log.d(TAG, "destroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.d(TAG, "destroyview")
        super.onDestroyView()
    }

    override fun onStart() {
        Log.d(TAG, "start")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "pause")
        super.onPause()
    }

    override fun onDetach() {
        Log.d(TAG, "detach")
        super.onDetach()

    }

    override fun onStop() {
        Log.d(TAG, "onstop")
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "oncreate")
        super.onCreate(savedInstanceState)
    }



    override fun onDeleteProductClick(position: Int) {

        Log.d(TAG,"delete product function from prof prod list frag. Receiver position: "+ position)
        profile_product_adapter.deleteSellingSoldProducts(position)
    }

    private fun setUpRecyclerViewWithData(){

        Log.d(TAG,"inside set up recycler view with data function")

        val productDetailList = mutableListOf<ProductInfoForAdapter>()

        val ref = FirebaseDatabase.getInstance().getReference().child("Products")

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID: String

        if(currentUser == null)
            return
        else
            currentUserID = currentUser.uid

        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.d(TAG,"data change")

                if(p0 == null){

                    Log.d(TAG,"no data available")
                    return
                }

                val children = p0.children // gives keys under "product"

                var name = ""
                var category=""
                var imageURL =""
                var key=""
                var price=""
                var productObj: ProductInfoForAdapter

                for(data in children){

                    //iterates through each values under product/keys/

                    if(arguments?.getInt("choice") == 0){

                        // selling tab
                        Log.d(TAG,"selling items")
                        //Log.d(TAG,"product key: "+data.key.toString())

                        if(currentUserID.equals(data.child("user_id").value) && data.child("active").value!!.equals("yes")){

                            Log.d(TAG,"if product name: "+ data.child("name").value.toString())
                            name = data.child("name").value.toString()
                            category = data.child("category").value.toString()
                            imageURL = data.child("url").value.toString()
                            price = data.child("price").value.toString()
                            key = data.key.toString()
                            Log.d(TAG,"pd name: "+ name+ " key: "+key)
                            productObj = ProductInfoForAdapter(name,category,imageURL,key,price)
                            productDetailList.add(productObj)


                        }

                    }

                    if(arguments?.getInt("choice") == 1){

                        // sold tab
                        Log.d(TAG,"sold items")

                        if(currentUserID.equals(data.child("user_id").value) && data.child("active").value!!.equals("no")){

                            Log.d(TAG,"product name: "+ data.child("name").value.toString())
                            name = data.child("name").value.toString()
                            category = data.child("category").value.toString()
                            imageURL = data.child("url").value.toString()
                            key = data.key.toString()
                            price = data.child("price").value.toString()
                            Log.d(TAG,"pd name: "+ name+ " key: "+key)
                            productObj = ProductInfoForAdapter(name,category,imageURL,key,price)
                            productDetailList.add(productObj)


                        }

                    }
                    if(arguments?.getInt("choice") == 2){

                        Log.d(TAG,"favorite items")

                        val favorites_children = data.child("Favorites").children
                        for(fav_data in favorites_children){

                            //Log.d(TAG,"fav key:" + fav_data.key)
                            //Log.d(TAG,"fav value:" + fav_data.value)
                            if(fav_data.value.toString().equals(currentUserID)){

                                Log.d(TAG,"fav product name: "+ data.child("name").value.toString() )
                                name = data.child("name").value.toString()
                                category = data.child("category").value.toString()
                                imageURL = data.child("url").value.toString()
                                key = data.key.toString()
                                price = data.child("price").value.toString()
                                Log.d(TAG,"pd name: "+ name+ " key: "+key)
                                productObj = ProductInfoForAdapter(name,category,imageURL,key,price)
                                productDetailList.add(productObj)

                            }

                        }


                    }
                }
                Log.d(TAG,"list count: "+productDetailList.size)
                // setup the recycler view
                Log.d(TAG,"setting up recycler view")
                profile_product_adapter = ProfileProductListRecyclerViewAdapter(productDetailList,arguments!!.getInt("choice"))
                profile_product_adapter.setItemClickListener(this@ProfileProductListFragment)
                profile_recyclerView.adapter = profile_product_adapter

            }


        })


    }
}