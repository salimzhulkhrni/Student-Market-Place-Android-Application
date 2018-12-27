package com.salim.salimzhulkhrni.student_marketplace

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.toolbar.*

class ListProductActivity : AppCompatActivity(),BrowsingRecyclerViewAdapter.adapterListener {

    val TAG = "list_product_act_Fragment"
    val key = FirebaseAuth.getInstance().currentUser!!.uid

    var productList : ArrayList<ProductData> = ArrayList()

    var productList_copy: ArrayList<ProductData> = ArrayList()

    lateinit var recyclerView : RecyclerView

    lateinit var adapter: BrowsingRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_product)

        setSupportActionBar(mainToolBar)

        val toolbar_ref = supportActionBar
        toolbar_ref!!.title = ""

        toolbar_title.text = "Products"

        toolbar_ref.setDisplayHomeAsUpEnabled(true)

        toolbar_ref.setDisplayShowTitleEnabled(false)

        mainToolBar.setNavigationOnClickListener {
            finish()
        }

        val query = intent.getStringExtra("category")

        recyclerView = findViewById<RecyclerView>(R.id.allProductRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter  = BrowsingRecyclerViewAdapter(productList, key)
        adapter.adapterClickListener(this)
        recyclerView.adapter = adapter

        val ref = FirebaseDatabase.getInstance().getReference()
        ref.child("Products").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@ListProductActivity,"Can't conect to firebase", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(temp in p0.children){

                    var productL : ProductData = ProductData()

                    if(temp.child("active").getValue(String::class.java).equals("yes")){

                        if(query.equals("All") || temp.child("category").getValue(String::class.java).equals(query)){
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
                            productList_copy.add(productL)
                        }
                    }
                }
                Log.d(TAG,"product list size inside: "+ productList.size.toString())
                adapter.notifyDataSetChanged()
            }
        })

    }

    override fun loadProductActivity(productType: ProductData, flag: String) {

        if(flag.equals("imageView")){

            val intent = Intent(this, ViewProductActivity::class.java)
            //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
            intent.putExtra("Key",productType.key)
            startActivity(intent)

        }
        else if(flag.equals("contains")){

            val ref = FirebaseDatabase.getInstance().getReference()
            //ref.child("Products").child(productType).child("Favorites").push().setValue(key)
            ref.child("Products").child(productType.key).child("Favorites").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    for(temp in p0.children){
                        //Log.i("Favorite Product", temp.getValue(String::class.java))

                        if(temp.getValue(String::class.java) == key){
                            ref.child("Products").child(productType.key).child("Favorites").child(temp.key!!).removeValue()
                        }
                    }
                    Log.d(TAG,"data change before calling adapter")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.toolbar_menu_search,menu)

        val search_item = menu!!.findItem(R.id.search_toolbar)

        if(search_item != null){

            Log.d(TAG,"search item not null")

            val search_view = search_item.actionView as SearchView

            search_view.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

                override fun onQueryTextSubmit(query: String?): Boolean {

                    Log.d(TAG,"submit - query: "+ query)


                    if(query != null){

                        Log.d(TAG,"not null query: "+query)

                        val filtered_product_list: ArrayList<ProductData> = ArrayList()

                        for(index in productList_copy.indices){

                            if(productList_copy[index].name.toLowerCase().contains(query)){

                                Log.d(TAG,"matched product name: "+productList_copy[index].name)

                                filtered_product_list.add(productList_copy[index])

                            }
                        }

                        Log.d(TAG,"filtered list count: "+ filtered_product_list.size)
                        //productList.clear()
                        productList = filtered_product_list
                        Log.d(TAG,"product list count: "+ productList.size)
                        Log.d(TAG,"adapter data set changed")
                        adapter.updateData(productList)
                        //adapter.notifyDataSetChanged()


                    }
                    Log.d(TAG,"returning here")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    Log.d(TAG,"text change")

                    if(newText == ""){

                        Log.d(TAG,"empty string - load the movies from the backup product list")
                        //Log.d(TAG,"product list copy size for reload: "+ productList_copy.size)
                        //productList.clear()
                        productList = productList_copy

                        adapter.updateData(productList)
                        //adapter.notifyDataSetChanged()
                    }

                    return true

                }

            })

        }

        return super.onCreateOptionsMenu(menu)

    }

}
