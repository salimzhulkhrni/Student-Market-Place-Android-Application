package com.salim.salimzhulkhrni.student_marketplace

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.signup_fragment.*
import java.lang.RuntimeException
import java.util.*

class User(val user_id: String, val username: String,val  user_email: String, val profileImageUrl: String){

    constructor():this("","","","")
}

class SignUpFragment: Fragment(){

    val TAG = "signup_fragment"

    private var listener: onSignUpFragmentListener? = null

    var selectedPhotoUri: Uri? = null

    var email_ref: String? = null
    var username_ref: String? = null
    var password_ref: String?=null

    var isReferred: Boolean= false

    lateinit var db: LocalEmailPwdDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        //
        db = LocalEmailPwdDB(context!!)

        Log.d(TAG,"create db object successfully")
        ///
        return inflater.inflate(R.layout.signup_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null){

            Log.d(TAG,"saved instance not null - prev data available")
            Log.d(TAG,"saved email: "+savedInstanceState.getString("email"))
            Log.d(TAG,"saved password: "+savedInstanceState.getString("password"))
            Log.d(TAG,"saved retyped-password: "+savedInstanceState.getString("retype_password"))
            Log.d(TAG,"saved username: "+savedInstanceState.getString("username"))

            email_signup.setText(savedInstanceState.getString("email"))
            password_signup.setText(savedInstanceState.getString("password"))
            retypepassword_signup.setText(savedInstanceState.getString("retype_password"))
            username_signup.setText(savedInstanceState.getString("username"))
        }

        register_signupfragment.setOnClickListener {
            Log.d(TAG,"Register button clicked")

            val email = email_signup.text.toString()
            if(email.isNotEmpty())
                checkForReferredEmail(email) // check if this email is already referred by someone
            //verifyEmail()

        }

        alreadHaveAnAccount.setOnClickListener {

            listener!!.reloadLoginFragment()
        }

        selectphoto_imageview.setOnClickListener {

            Log.d(TAG,"clicked on image")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "on attach")
        super.onAttach(context)
        if(context is onSignUpFragmentListener)
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
        listener = null
    }

    override fun onStop() {
        Log.d(TAG, "onstop")
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "oncreate")
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG,"on save instance state")
        //possible error here
        if(email_signup!= null && password_signup != null
            && retypepassword_signup != null && username_signup != null
            && selectphoto_imageview != null ){

            outState.putString("email",email_signup.text.toString())
            outState.putString("password",password_signup.text.toString())
            outState.putString("retype_password",retypepassword_signup.text.toString())
            outState.putString("username",username_signup.text.toString())
            // need to add for photo
        }
    }

    private fun verifyEmail(){

        Log.d(TAG,"inside verify email fn")

        val syr_email = email_signup.text.toString()
        val password = password_signup.text.toString()
        val retype_password = retypepassword_signup.text.toString()
        val name = username_signup.text.toString()

        ///
        email_ref = syr_email
        username_ref = name
        password_ref=password
        //


        if( syr_email.isNotEmpty() && password.isNotEmpty() && retype_password.isNotEmpty() && name.isNotEmpty()  ){


            if(!password.equals(retype_password)){

                Log.d(TAG,"passwords dont match")
                Toast.makeText(context,"Pssswords Dont Match!",Toast.LENGTH_SHORT).show()
                return

            }

            Log.d(TAG,"is referred: "+ isReferred)

            /*

            if(!syr_email.contains("@syr.edu") || !isReferred ){

                Log.d(TAG,"invalid SU email address")
                Toast.makeText(context,"Please provide a valid SU email address!",Toast.LENGTH_SHORT).show()
                return
            }
            */

            if(syr_email.contains("@syr.edu") || isReferred){

                Log.d(TAG,"valid email address - can proceed")
            }
            else{

                Log.d(TAG,"invalid SU email address")
                Toast.makeText(context,"Please provide a valid SU email address!",Toast.LENGTH_SHORT).show()
                return

            }

            if(selectedPhotoUri == null){

                Log.d(TAG,"no image selected")
                Toast.makeText(context,"Please select an image",Toast.LENGTH_SHORT).show()
                return

            }

            Log.d(TAG,"all req validation successful")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(syr_email,password)
                .addOnCompleteListener {

                    if(it.isSuccessful){

                        Log.d(TAG,"Created user")

                        val user = it.result!!.user
                        Log.d(TAG,"user is: "+user)
                        val emailVerified = user.isEmailVerified
                        Log.d(TAG,"email verified: "+emailVerified)

                        storeImagesToFirebaseDatabase()

                        if(!emailVerified){

                            sendVerificationMailToUser(user)
                        }

                    }
                    else{
                        Log.d(TAG,"user creation not successful")
                        return@addOnCompleteListener
                    }


                }

                .addOnFailureListener {
                    Toast.makeText(context,"Registration Failed: "+ it.message,Toast.LENGTH_SHORT).show()
                    Log.d(TAG,"user creation failure "+it.message)
                }

        }

        else{
            Log.d(TAG,"Please fill up the fields!")
            Toast.makeText(context,"Please fill up the fields",Toast.LENGTH_SHORT).show()
        }



    }

    private fun sendVerificationMailToUser(user: FirebaseUser){


        user.sendEmailVerification().addOnCompleteListener {

            if(it.isSuccessful){

                Log.d(TAG,"email sent")
                Toast.makeText(context,"Registration Partially Completed. Email sent to "+user.email + " for verification",Toast.LENGTH_SHORT).show()
                /// save credentials to local email pwd db
                if(email_ref != null && password_ref != null){

                    val email = email_ref
                    val pwd = password_ref
                    alertBoxInteraction(email ?: "",pwd ?: "")
                }


                //
                //listener!!.reloadLoginFragment()
            }
            else{

                Log.d(TAG,"email not sent")
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            Log.d(TAG,"photo selected success")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver,selectedPhotoUri)

            selectphoto_imageview.setImageBitmap(bitmap)

        }
        else{

            Log.d(TAG,"photo selected failure")
        }
    }

    private fun storeImagesToFirebaseDatabase(){

        if(selectedPhotoUri == null){

            Log.d(TAG,"no image to upload")
            return
        }

        val filename = UUID.randomUUID().toString()
        Log.d(TAG,"file name: "+filename)

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                Log.d(TAG,"image upload success")

                ref.downloadUrl.addOnSuccessListener {

                    Log.d(TAG,"file location: "+it.toString())

                    Log.d(TAG,"username val:"+username_ref)
                    Log.d(TAG,"email val:"+email_ref)


                    storeUserInFirebaseDatabase(it.toString())
                }
            }

            .addOnFailureListener {

                Log.d(TAG,"failed to upload image")
            }
    }

    private fun storeUserInFirebaseDatabase(imageURL: String){


        Log.d(TAG,"storing user in db")
        val user_id = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$user_id")

        if(username_ref == null)
            return

        if(email_ref == null)
            return

        Log.d(TAG,"stored username: "+ username_ref + " email: "+ email_ref)


        val user = User(user_id,username_ref!!,email_ref!!,imageURL)

        ref.setValue(user)
            .addOnCompleteListener {

                Log.d(TAG,"saved user details to database")


            }

            .addOnFailureListener {

                Log.d(TAG,"Failed to save user to database: "+it.message)
            }


    }

    private fun checkForReferredEmail(email: String){

        Log.d(TAG,"inside referred emails function")

        val ref = FirebaseDatabase.getInstance().getReference().child("referred_email_ids")

        Log.d(TAG,"ref value: "+ref)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.d(TAG,"p0 : "+p0)

                if(p0.value == null){
                    Log.d(TAG,"p0 snapshot is "+p0)
                    verifyEmail()
                    return
                }

                val children = p0.children

                Log.d(TAG,"children: "+children)

                children.forEach {

                    Log.d(TAG,"key: "+it.key)
                    Log.d(TAG,"value: "+it.value)

                    if(it.value!!.equals(email)){
                        Log.d(TAG,"email: "+email+ " is present from exisitng referred ones")
                        isReferred = true  // this email is already referred
                        verifyEmail()  // verify this referred email
                    }
                    else{

                        Log.d(TAG,"email: "+email+ " is not present from existing referred ones")
                        verifyEmail() // proceed to complete normal sign up process
                    }
                }

            }

        })


    }

    private fun alertBoxInteraction(email: String,password: String){

        Log.d(TAG,"inside alert box fun")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Save Password")
        builder.setMessage("Do you want to remember the credentails? ")

        // listener for 'Yes'
        builder.setPositiveButton("Yes"){ dialog, which ->

            Log.d(TAG,"clicked Yes from alert box")
            Log.d(TAG,"saving email: "+email + " pwd: "+password+" to local db")
            db.saveUserCredentialsIntoDB(email,password)
            listener!!.reloadLoginFragment()


        }

        builder.setNegativeButton("No"){ dialog, which ->

            Log.d(TAG,"clicked no frm alert box")
            listener!!.reloadLoginFragment()

        }

        builder.setNeutralButton("Cancel"){ dialog, which ->

            Log.d(TAG,"clicked on neutral button")
            listener!!.reloadLoginFragment()


        }

        val alertDialog = builder.create()
        alertDialog.show()

    }

    interface onSignUpFragmentListener{

        fun reloadLoginFragment()

    }


}