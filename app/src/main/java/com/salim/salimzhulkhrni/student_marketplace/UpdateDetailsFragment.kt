package com.salim.salimzhulkhrni.student_marketplace

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.update_details_fragment.*
import java.util.concurrent.TimeUnit

class User_PhoneNumber(val user_id: String, val phone_number: String){

    constructor():this("","")
}

class UpdateDetailsFragment: Fragment(){

    val TAG = "update_details_Fragment"

    private var changeName = false
    private var changePassword = false
    private var changePhoneNumber = false

    lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        return inflater.inflate(R.layout.update_details_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG,"on view created")
        populateData()

        updatebtn_update_details_frag.setOnClickListener {

            Log.d(TAG,"clicked on update button")

            if(phone_number_update_details_frag.text.isNotEmpty()){

                val phNum = phone_number_update_details_frag.text.toString()

                Log.d(TAG,"phone number not empty")

                verifyPhoneNumber(phNum)

            }

            if(name_update_details_frag.text.isNotEmpty()){

                Log.d(TAG,"name not empty ")

                val name = name_update_details_frag.text.toString()

                updateName(name)

            }

            if(password_update_details_frag.text.toString().isNotEmpty() && retypepassword_update_details_frag.text.isNotEmpty()){


                val pwd = password_update_details_frag.text.toString()
                val retype_pwd = retypepassword_update_details_frag.text.toString()

                if(pwd.equals(retype_pwd)){

                    Log.d(TAG,"passwords match")

                    changePassword(pwd)
                }
                else{

                    Toast.makeText(context,"Passwords Dont Match!",Toast.LENGTH_SHORT).show()
                    Log.d(TAG,"passwords dont match")
                }
            }

            Log.d(TAG,"changes saved")

            Toast.makeText(context,"Changed Saved Successfully",Toast.LENGTH_SHORT).show()
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

    private fun populateData(){

        Log.d(TAG,"inside populate data function")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null){

            Log.d(TAG,"user is not null")
            populateNameAtStart(firebaseUser)
            populatePhoneNumberAtStart(firebaseUser)

        }

    }

    private fun populateNameAtStart(user: FirebaseUser){

        Log.d(TAG,"inside populate name at start fn")
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(user!!.uid)

        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

                Log.d(TAG," cancelled")

            }

            override fun onDataChange(p0: DataSnapshot) {

                Log.d(TAG,"setting init username: "+p0.child("username").getValue(String::class.java))
                name_update_details_frag.setHint(p0.child("username").getValue(String::class.java))
            }
        })

    }

    private fun populatePhoneNumberAtStart(user: FirebaseUser){

        Log.d(TAG,"inside populate ph num at start fn")

        val ref = FirebaseDatabase.getInstance().reference.child("phone_number").child(user!!.uid)

        if(ref != null){

            Log.d(TAG,"phone number exists")

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                    Log.d(TAG,"on cancelled")
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val phone_number = p0.child("phone_number").getValue(String::class.java)
                    Log.d(TAG,"setting init phone num: " + phone_number.toString())
                    if(phone_number != null)
                        phone_number_update_details_frag.setHint(p0.child("phone_number").getValue(String::class.java))
                }

            })

        }
        else{

            Log.d(TAG,"phone number does not exist")
        }



    }


    private fun verifyPhoneNumber(phoneNumber: String){

        Log.d(TAG,"inside populate phone number")

        val smsCode = "315751"

        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber,smsCode)

        mCallBacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {

                Log.d(TAG,"verification completed")
                savePhoneNumberToDatabase(phoneNumber)
            }

            override fun onVerificationFailed(p0: FirebaseException?) {

                if(p0 is FirebaseAuthInvalidCredentialsException)
                    Log.d(TAG,"invalid phone number")

                if(p0 is FirebaseTooManyRequestsException)
                    Log.d(TAG,"quota exceeded")

            }

            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(p0, p1)

                Log.d(TAG,"code sent: "+p0)

            }

            override fun onCodeAutoRetrievalTimeOut(p0: String?) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Log.d(TAG,"code auto retrieval: "+p0)
            }


        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(

            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this.activity!!,
            mCallBacks

        )


    }

    private fun savePhoneNumberToDatabase(phoneNumber: String){

        Log.d(TAG,"save phone number to database")

        if(FirebaseAuth.getInstance().currentUser != null){

            val user_id = FirebaseAuth.getInstance().currentUser!!.uid

            Log.d(TAG,"user id: "+user_id)

            val ref = FirebaseDatabase.getInstance().getReference("/phone_number/$user_id")

            val details = User_PhoneNumber(user_id,phoneNumber)

            ref.setValue(details)
                .addOnCompleteListener {

                Log.d(TAG,"user_id: "+user_id+ " ph num: "+ phoneNumber + " - saved in db")
            }

                .addOnFailureListener {

                    Log.d(TAG,"error: "+it.message)
                }

        }

    }

    private fun updateName(name: String){

        Log.d(TAG,"inside update name function")

        if(FirebaseAuth.getInstance().currentUser != null){

            val user_id = FirebaseAuth.getInstance().currentUser!!.uid

            val ref = FirebaseDatabase.getInstance().getReference("/users/$user_id").child("username")

            ref.setValue(name)
                .addOnCompleteListener {

                    Log.d(TAG,"name changed to : "+ name)
                }
                .addOnFailureListener {

                    Log.d(TAG,"Error: "+it.message)
                }

        }

    }

    private fun changePassword(password: String){

        Log.d(TAG,"inside change password")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null){

            firebaseUser.updatePassword(password)
                .addOnCompleteListener {

                    Log.d(TAG,"updated password to: "+password)
                }

                .addOnFailureListener {

                    Log.d(TAG,"error: "+it.message)
                }

        }

    }



}