package com.salim.salimzhulkhrni.student_marketplace

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_view_page.*
import kotlinx.android.synthetic.main.toolbar.*

class ProfileActivityViewPage : AppCompatActivity() {

    val TAG = "profile_Act_viewpg_fragment"


    lateinit var viewPager: ViewPager

    lateinit var productPageAdapter: ProductViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view_page)


        viewPager = findViewById(R.id.product_viewPager)
        productPageAdapter = ProductViewPageAdapter(supportFragmentManager)
        viewPager.adapter = productPageAdapter
        productTabs.setupWithViewPager(viewPager)
        //viewPager.setCurrentItem(0)

        setSupportActionBar(mainToolBar)

        val toolbar_ref = supportActionBar
        toolbar_ref!!.title = ""

        toolbar_title.text = "My Profile"

        toolbar_ref.setDisplayHomeAsUpEnabled(true)

        toolbar_ref.setDisplayShowTitleEnabled(false)

        mainToolBar.setNavigationOnClickListener {
            finish()
        }

        // populate image & greeting message
        val currentUser = FirebaseAuth.getInstance().currentUser

        if(currentUser != null){

            val profileRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUser!!.uid)

            profileRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if(p0 != null){

                        Log.d(TAG,"data not null")
                        val url = p0.child("profileImageUrl").value.toString()
                        val greetingMsg = greeting_text_profile_view_page.text.toString()
                        greeting_text_profile_view_page.text = greetingMsg + " " + p0.child("username").value.toString()+ "!"
                        Picasso.get().load(url).into(prof_image_profile_view_page)

                    }
                }


            })

        }


    }
}
