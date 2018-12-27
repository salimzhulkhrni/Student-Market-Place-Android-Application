package com.salim.salimzhulkhrni.student_marketplace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_fragment.*
import java.lang.RuntimeException

class LoginFragment: Fragment(){

    val TAG ="Login_Fragment"

    lateinit var db: LocalEmailPwdDB

    private var listener: onLoginFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        db = LocalEmailPwdDB(context!!)

        Log.d(TAG,"create db object successfully")

        return inflater.inflate(R.layout.login_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null){

            Log.d(TAG,"saved instance part not null")
            Log.d(TAG,"saved email: "+savedInstanceState.getString("email"))
            Log.d(TAG,"saved password: "+ savedInstanceState.getString("password"))
            email_login.setText(savedInstanceState.getString("email"))
            password_login.setText(savedInstanceState.getString("password"))

        }

        loadSavedCredentials()

        login_button_loginfragment.setOnClickListener {

            Log.d(TAG,"login button from login frag clicked")

            val currentUser = FirebaseAuth.getInstance().currentUser

            if(currentUser != null){

                // signed up user is the current user
                val currentUserEmail = currentUser.email
                val loggedInEmail = email_login.text.toString()

                if(currentUserEmail.equals(loggedInEmail)){

                    Log.d(TAG,"current user email is logged in user's email")
                    verifyUserDetails()
                }
                else{

                    Log.d(TAG,"current user's email is not logged in user's email")
                    tryLogin()
                }
            }
            else{

                Log.d(TAG,"no sign up occured.first time login")
                verifyUserDetails()
            }


        }

        dontHaveAnAccount.setOnClickListener {

            listener!!.reloadSignUpFragment()
        }

        forgot_password_login.setOnClickListener {

            Log.d(TAG,"clicked forgot password")

            resetPassword()

        }

    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "on attach")
        super.onAttach(context)

        if(context is onLoginFragmentListener)
            listener = context
        else
            throw RuntimeException(context.toString()+"must implement")

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
        listener=null
    }

    override fun onStop() {
        Log.d(TAG, "onstop")
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "oncreate")
        super.onCreate(savedInstanceState)
        retainInstance=true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG,"on save instance state")
        //possible error here
        if(email_login != null && password_login != null){

            outState.putString("email",email_login.text.toString())
            outState.putString("password",password_login.text.toString())
        }
    }

    interface onLoginFragmentListener{

        fun reloadSignUpFragment()
    }

    private fun verifyUserDetails(){

        val user = FirebaseAuth.getInstance().currentUser


        if(user == null){
            Log.d(TAG,"current user null")
            //Toast.makeText(context,"No User Available By This Email ID",Toast.LENGTH_SHORT).show()
            tryLogin()
            return
        }
        else{
            Log.d(TAG,"current user not null")
            user.reload().addOnCompleteListener {

                if(it.isSuccessful && user != null){

                    val verifiedUser = FirebaseAuth.getInstance().currentUser
                    val emailVerified = verifiedUser!!.isEmailVerified

                    Log.d(TAG,"current user id: "+verifiedUser.uid)

                    if(emailVerified){

                        Log.d(TAG,"if: email address verification : "+emailVerified)
                        //Toast.makeText(context,"Email Address Verified",Toast.LENGTH_SHORT).show()
                        tryLogin()
                    }
                    else{

                        Log.d(TAG,"else: email address verification failed: "+emailVerified)
                        Toast.makeText(context,"Registration Incomplete - Email Address Not Yet Verified",Toast.LENGTH_SHORT).show()
                    }

                }
                else{

                    Log.d(TAG,"reload failed")
                    Toast.makeText(context,"No user found by this email: ",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    private fun tryLogin(){

        val email = email_login.text.toString()
        val password = password_login.text.toString()

        if(email.isEmpty()){
           Log.d(TAG,"email is empty")
           Toast.makeText(context,"Please provide email address",Toast.LENGTH_SHORT).show()
           return
        }

        if(password.isEmpty()){
            Log.d(TAG,"password is empty")
            Toast.makeText(context,"Please provide the password",Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {

                if(it.isSuccessful){

                    Log.d(TAG,"Login Successful")
                    Toast.makeText(context,"Login SUCCESS !",Toast.LENGTH_LONG).show()
                    val intent = Intent(context,ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
            }

            .addOnFailureListener {

                Log.d(TAG,"Login Failure: "+it.message)
                Toast.makeText(context,"Login Failure: "+it.message,Toast.LENGTH_LONG).show()
            }
    }

    private fun resetPassword(){

        Log.d(TAG,"inside reset password")



        val email = email_login.text.toString()

        if(email.isNotEmpty()){

            Log.d(TAG,"email is: "+ email)
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener {

                    if(it.isSuccessful){

                        Log.d(TAG,"email sent for pwd change")
                        Toast.makeText(context,"Password Reset Email Sent To: "+email,Toast.LENGTH_SHORT).show()
                    }
                    else{

                        Log.d(TAG,"email sent failure for pwd change")
                    }
                }

                .addOnFailureListener {

                    Log.d(TAG,"password reset email sent failed - invalid email: "+it.message)
                    Toast.makeText(context,"Reset password email sending failed - "+it.message,Toast.LENGTH_SHORT ).show()
                }

        }
        else{
            Log.d(TAG,"email is empty for sending password reset")
            Toast.makeText(context,"Email Cannot Be Empty",Toast.LENGTH_SHORT).show()
        }


    }

    private fun loadSavedCredentials(){

        Log.d(TAG,"inside load saved credentials fn")

        val credentialList = db.getFirstUserCredentials()

        if(credentialList.size > 0){

            Log.d(TAG,"first entry from db- email: "+credentialList[0]+ " pwd: "+credentialList[1])
            email_login.setText(credentialList[0])
            password_login.setText(credentialList[1])
        }
        else{

            Log.d(TAG,"no user data from db available")
        }


    }
}