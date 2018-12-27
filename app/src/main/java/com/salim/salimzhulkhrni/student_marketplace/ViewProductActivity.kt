package com.salim.salimzhulkhrni.student_marketplace

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_product.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject

class ViewProductActivity : AppCompatActivity() {

    val TAG = "view_product_act_Fragment"

    lateinit var productImage: ImageView
    lateinit var productName: TextView
    lateinit var productPrice: TextView
    lateinit var productDescription : TextView


    lateinit var productAddress: String
    lateinit var product_name: String
    lateinit var seller_user_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        setSupportActionBar(mainToolBar)

        val toolbar_ref = supportActionBar
        toolbar_ref!!.title = ""

        toolbar_title.text = ""

        toolbar_ref.setDisplayHomeAsUpEnabled(true)

        toolbar_ref.setDisplayShowTitleEnabled(false)

        mainToolBar.setNavigationOnClickListener {
            finish()
        }

        val requireKey:String = intent.getStringExtra("Key")
        Log.d("Key",requireKey)

        initializeVariables()

        contact_seller_view_product.setOnClickListener {

            Log.d(TAG,"contact seller button clicked")
            contactSeller()

        }

        val ref = FirebaseDatabase.getInstance().getReference()
        ref.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.i("Address",p0.toString())
                for(temp in p0.children){
                    //Log.i("Name",temp.child("name").getValue(String::class.java))
                    //Log.i("UUID",temp.key)
                    if(temp.key.equals(requireKey)){
                        val prod_name = temp.child("name").getValue(String::class.java)
                        toolbar_title.text=prod_name
                        productName.setText(prod_name)
                        productPrice.setText(temp.child("price").getValue(Long::class.java).toString())
                        productDescription.setText(temp.child("description").getValue(String::class.java))
                        Picasso.get().load(temp.child("url").getValue(String::class.java)).into(productImage)
                        productAddress= temp.child("address").getValue(String::class.java)!!
                        product_name = prod_name!!
                        seller_user_id = temp.child("user_id").getValue(String::class.java)!!
                        break
                    }
                }
            }
        })



    }

    fun initializeVariables(){
        productImage = findViewById<View>(R.id.productImage) as ImageView
        productName = findViewById<View>(R.id.productName) as TextView
        productPrice = findViewById<View>(R.id.productPrice) as TextView
        productDescription = findViewById<View>(R.id.productDescription) as TextView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.location_product_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){

            R.id.location_product_toolbar -> {

                Log.d(TAG,"clicked on location pd")
                getProductLocationData()
            }
        }
        return super.onOptionsItemSelected(item)


    }

    fun getProductLocationData(){

        Log.d(TAG,"inside get product location data")

        //val address = "1600 Amphitheatre Parkway Mountain View CA"

        var address = productAddress

        val queue = Volley.newRequestQueue(this)
        var url = "https://maps.googleapis.com/maps/api/geocode/json?address="

        for(temp in address.split(" ")){
            url+=temp+"+"
        }

        val APIkey = "&key=AIzaSyBJOON8qnCr3wN5-JpEkzJ64Sn2htMnNP8"

        url += APIkey

        Log.i("URL", url)

        val jsonObj = JSONObject()

        val req = JsonObjectRequest(
            Request.Method.GET,url,null,
            Response.Listener {
                    response ->

                if(response.getJSONArray("results").length() != 0){

                    val lat = response.getJSONArray("results").
                        getJSONObject(0).getJSONObject("geometry").
                        getJSONObject("location").getString("lat")

                    val long = response.getJSONArray("results").
                        getJSONObject(0).getJSONObject("geometry").
                        getJSONObject("location").getString("lng")

                    //Toast.makeText(this,"Latitude "+lat +"  Longitude "+ long,Toast.LENGTH_SHORT).show()
                    Toast.makeText(this,"Opening Maps!",Toast.LENGTH_SHORT).show()

                    // load google maps
                    //val uri = Uri.parse("geo:"+lat+","+long+"")
                    val label=product_name
                    val uri = Uri.parse("geo:0,0?q="+lat+","+long+"("+label+")")
                    val intent = Intent(Intent.ACTION_VIEW,uri)
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)

                }
                else{

                    Log.d(TAG,"invalid address - json results count: "+ response.getJSONArray("results").length())
                    Toast.makeText(this,"Invalid Address. Cannot Be Displayed in Google Maps",Toast.LENGTH_SHORT).show()
                }



            },
            Response.ErrorListener {

                Log.d(TAG,"error: "+it.message)

            }
        )

        queue.add(req)

    }

    fun contactSeller(){

        Log.d(TAG,"inside contact seller function")

        val seller_id_ref = FirebaseDatabase.getInstance().getReference("/users/"+seller_user_id)

        if(seller_id_ref == null){

            Log.d(TAG,"no user id: "+ seller_user_id+" found")
            return
        }

        seller_id_ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0 != null){

                    val seller_email = p0.child("user_email").value.toString()
                    val seller_name = p0.child("username").value.toString()
                    Log.d(TAG,"seller name: "+ seller_name)
                    Log.d(TAG,"seller email: "+seller_email)
                    val intent = Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+seller_email))
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Enquiry On Product: " + product_name)
                    intent.putExtra(Intent.EXTRA_TEXT,"Hi "+ seller_name+",")
                    try {

                        Log.d(TAG,"inside try block contact seller")
                        startActivity(Intent.createChooser(intent,"Chooser Title"))

                    }
                    catch (e: ActivityNotFoundException){

                        Log.d(TAG,"Exception: "+e.message)


                    }
                }
            }


        })




    }
}
