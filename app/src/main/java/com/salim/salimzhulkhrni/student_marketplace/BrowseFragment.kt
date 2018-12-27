package com.salim.salimzhulkhrni.student_marketplace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BrowseFragment: Fragment(), BrowsingRecyclerViewAdapter.adapterListener{

    val TAG = "Browse_fragment"

    lateinit var recyclerView : RecyclerView
    lateinit var mostLikeViewAll : TextView
    lateinit var categoryViewAll : TextView
    lateinit var electronis : LinearLayout
    lateinit var homekitchen : LinearLayout
    lateinit var automobiles : LinearLayout
    lateinit var clothes : LinearLayout
    lateinit var fab_button: FloatingActionButton

    var productList : ArrayList<ProductData> = ArrayList()
    lateinit var adapter : BrowsingRecyclerViewAdapter //Adapter

    val key = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        Log.d(TAG, "createview")
        val view =inflater.inflate(R.layout.browse_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,

        mostLikeViewAll = view.findViewById<TextView>(R.id.mostLikeViewAll)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        electronis = view.findViewById<LinearLayout>(R.id.electronis)
        homekitchen = view.findViewById<LinearLayout>(R.id.homeandkitchen)
        automobiles = view.findViewById<LinearLayout>(R.id.automobiles)
        clothes = view.findViewById<LinearLayout>(R.id.clothes)
        categoryViewAll = view.findViewById<TextView>(R.id.categoryViewAll)
        fab_button = view.findViewById<FloatingActionButton>(R.id.fab)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        adapter  = BrowsingRecyclerViewAdapter(productList, key)
        adapter.adapterClickListener(this)
        recyclerView.adapter = adapter

        loadProductData()

        fab_button.setOnClickListener {
            val intent = Intent(context, AddProductActivity::class.java)
            startActivity(intent)
        }

        mostLikeViewAll.setOnClickListener {

            val intent = Intent(context, ListProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("category","All")
            startActivity(intent)
        }

        categoryViewAll.setOnClickListener {

            val intent = Intent(context, ListProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("category","All")
            startActivity(intent)
        }

        electronis.setOnClickListener {

            val intent = Intent(context, ListProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("category","Electronics")
            startActivity(intent)
        }

        homekitchen.setOnClickListener {

            val intent = Intent(context, ListProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("category","Home and Kitchen")
            startActivity(intent)
        }

        automobiles.setOnClickListener {

            val intent = Intent(context, ListProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("category","Automobiles")
            startActivity(intent)
        }

        clothes.setOnClickListener {

            val intent = Intent(context, ListProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("category","Clothes and Footwear")
            startActivity(intent)
        }


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

    override fun loadProductActivity(productType: ProductData, flag: String) {

        if(flag.equals("imageView")){

            val intent = Intent(context, ViewProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("Key",productType.key)
            startActivity(intent)

        }
        else if(flag.equals("contains")){

            val ref = FirebaseDatabase.getInstance().getReference()
            //ref.child("Products").child(productType).child("Favorites").push().setValue(key)
            ref.child("Products").child(productType.key).child("Favorites").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    for(temp in p0.children){
                        //Log.i("Favorite Product", temp.getValue(String::class.java))
                        Log.i(tag, temp.getValue(String::class.java))
                        Log.i(tag,temp.getValue(String::class.java))

                        if(temp.getValue(String::class.java) == key){
                            ref.child("Products").child(productType.key).child("Favorites").child(temp.key!!).removeValue()
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            })
            //ref.child("Favorites").child(key).child(productType).removeValue()
        }
        else if(flag.equals("notcontains")){
            val ref = FirebaseDatabase.getInstance().getReference()
            ref.child("Products").child(productType.key).child("Favorites").push().setValue(key)
            //ref.child("Favorites").child(key).child(productType).setValue(productType)
        }
    }

    fun loadProductData(){

        val ref = FirebaseDatabase.getInstance().getReference()
        ref.child("Products").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"Can't conect to firebase", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.d(tag,"Inside child listner")

                for(temp in p0.children){

                    var productL : ProductData = ProductData()

                    if(temp.child("active").getValue(String::class.java).equals("yes")){
                        productL.name = temp.child("name").getValue(String::class.java)!!
                        productL.address = temp.child("address").getValue(String::class.java)!!
                        productL.description = temp.child("description").getValue(String::class.java)!!
                        productL.category = temp.child("category").getValue(String::class.java)!!
                        productL.price = temp.child("price").getValue(Long::class.java)!!
                        productL.url = temp.child("url").getValue(String::class.java)!!
                        productL.user_id = temp.child("user_id").getValue(String::class.java)!!
                        productL.key = temp.key.toString()!!

                        for(fav in temp.child("Favorites").children){
                            if(fav.getValue(String::class.java).equals(key)) {
                                productL.favorites = true
                            }
                        }

                        productList.add(productL)
                    }
                }
                Log.i("Product List size inside",productList.size.toString())
                adapter.notifyDataSetChanged()
            }
        })

    }
}