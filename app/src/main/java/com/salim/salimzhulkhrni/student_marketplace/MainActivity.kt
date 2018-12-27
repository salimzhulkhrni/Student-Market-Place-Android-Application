package com.salim.salimzhulkhrni.student_marketplace

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity(),StartUpFragment.onStartUpFragmentListener,SignUpFragment.onSignUpFragmentListener,LoginFragment.onLoginFragmentListener {

    val TAG = "MainAct_Fragment"

    //var currentFragment =""

    val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolBar)

        val toolbar = supportActionBar

        toolbar!!.title=""

        if(savedInstanceState != null){

            Log.d(TAG,"saved instance part not null")
        }
        else{

            Log.d(TAG,"saved instance part is null")
            loadStartUpScreen()
        }


    }

    private fun loadStartUpScreen(){

        Log.d(TAG,"loading startup fragment")
        toolbar_title.text="Welcome!"
        val transaction = manager.beginTransaction()
        val fragment = StartUpFragment()
        transaction.replace(R.id.frame_mainActivity,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onLoginClick() {
        // Loads Login Fragment
        loadLoginFragment()
    }


    override fun onSignUpClick() {
        //Loads Sign Up Fragment
        loadSignUpFragment()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.d(TAG,"on save instance func")
        //outState?.putString("currentPage",currentFragment)
    }

    private fun loadLoginFragment(){

        Log.d(TAG,"loading login fragment")
        //currentFragment = "Login"
        //Toast.makeText(this,"Loading Login Screen!",Toast.LENGTH_SHORT).show()
        toolbar_title.text="Login"
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_from_left)
        val fragment = LoginFragment()
        transaction.replace(R.id.frame_mainActivity,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun loadSignUpFragment(){

        Log.d(TAG,"loading sign up fragment")
        //currentFragment = "SignUp"
        Toast.makeText(this,"Loading Sign Up Screen!",Toast.LENGTH_SHORT).show()
        toolbar_title.text="Sign Up"
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_from_left)
        val fragment = SignUpFragment()
        transaction.replace(R.id.frame_mainActivity,fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun reloadLoginFragment() {

        Log.d(TAG,"reloading login fragment after registration")
        loadLoginFragment()
    }

    override fun reloadSignUpFragment() {

        loadSignUpFragment()
    }
}
