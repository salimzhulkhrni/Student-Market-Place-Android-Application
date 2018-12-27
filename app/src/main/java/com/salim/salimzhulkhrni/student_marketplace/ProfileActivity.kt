package com.salim.salimzhulkhrni.student_marketplace

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.app_bar_profile.*

class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val TAG = "profile_Fragment"

    val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        val toolbar_ref = supportActionBar

        toolbar_ref!!.title = "Browse Products"

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val headerView = nav_view.getHeaderView(0)
        val prof_username = headerView.findViewById<TextView>(R.id.profUid)
        val prof_email = headerView.findViewById<TextView>(R.id.profEmail)
        val prof_image = headerView.findViewById<CircularImageView>(R.id.profImg)

        if(FirebaseAuth.getInstance().currentUser == null)
            Log.d(TAG,"current user is null")

        val firebaseuser = FirebaseAuth.getInstance().currentUser

        if(firebaseuser != null){

            val profileRef = FirebaseDatabase.getInstance().reference.child("users").child(firebaseuser!!.uid)

            Log.d(TAG,"current user: "+profileRef)


            profileRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    Log.d(TAG,"profile data changed")
                    if(p0 != null){

                        Log.d(TAG,"snapshot not null")
                        prof_email.text = p0.child("user_email").value.toString()
                        prof_username.text = p0.child("username").value.toString()
                        Picasso.get().load(p0.child("profileImageUrl").value.toString()).into(prof_image)


                    }
                    else{

                        Log.d(TAG,"snapshot is null")
                    }

                }


            })

        }

        //loadViewPageProfileFragment()

        // brose frag for inital load

        loadBrowseFragment()


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_refresh -> {

                Log.d(TAG,"clicked on home")
                loadBrowseFragment()

            }

            R.id.nav_electronics -> {

                Log.d(TAG,"clicked on electronics category")
                val intent = Intent(this, ListProductActivity::class.java)
                //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
                intent.putExtra("category","Electronics")
                startActivity(intent)

            }

            R.id.nav_automobiles -> {

                Log.d(TAG,"clicked on automobiles category")
                val intent = Intent(this, ListProductActivity::class.java)
                //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
                intent.putExtra("category","Automobiles")
                startActivity(intent)
            }

            R.id.nav_homekitchen -> {

                Log.d(TAG,"clicked on home kitchen category")
                val intent = Intent(this, ListProductActivity::class.java)
                //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
                intent.putExtra("category","Home and Kitchen")
                startActivity(intent)
            }

            R.id.nav_clothing -> {

                Log.d(TAG,"clicked on clothing category")
                val intent = Intent(this, ListProductActivity::class.java)
                //Toast.makeText(activityContext,productKey[position],Toast.LENGTH_SHORT).show()
                intent.putExtra("category","Clothes and Footwear")
                startActivity(intent)
            }

            R.id.nav_update_details -> {

                Log.d(TAG,"clicked on update details nav ")

                loadUpdateDetailsFragment()

            }
            R.id.nav_signout -> {

                Log.d(TAG,"signing out")
                Toast.makeText(this,"Signing Out",Toast.LENGTH_SHORT).show()
                signOut()
            }

            R.id.nav_refer -> {

                Log.d(TAG,"clicked on referral")
                loadReferFriendFragment()
            }

            R.id.nav_myprofile -> {

                Log.d(TAG,"clicked on my profile")
                loadMyProfilePage()

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut(){

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        Log.d(TAG,"Navigating back to login fragment")
        startActivity(intent)

    }

    private fun loadReferFriendFragment(){

        Log.d(TAG,"loading refer friend fragment")
        Toast.makeText(this,"Loading Refer Friend Screen!",Toast.LENGTH_SHORT).show()
        toolbar.title = "Refer Friend"
        val transaction = manager.beginTransaction()
        val fragment = ReferFragment()
        transaction.replace(R.id.nav_fragment,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun loadUpdateDetailsFragment(){

        Log.d(TAG,"loading update details fragment")
        Toast.makeText(this,"Loading Update Details Screen!",Toast.LENGTH_SHORT).show()
        toolbar.title = "Update Details"
        val transaction = manager.beginTransaction()
        val fragment = UpdateDetailsFragment()
        transaction.replace(R.id.nav_fragment,fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun loadMyProfilePage(){

        Log.d(TAG,"inside my profile page fun")
        val intent = Intent(this, ProfileActivityViewPage::class.java)
        startActivity(intent)

    }

    private fun loadBrowseFragment(){

        Log.d(TAG,"loading browse fragment")
        val transaction = manager.beginTransaction()
        val fragment = BrowseFragment()
        transaction.replace(R.id.nav_fragment,fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}
