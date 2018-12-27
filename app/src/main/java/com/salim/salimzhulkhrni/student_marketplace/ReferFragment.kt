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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.refer_friend_fragment.*

class ReferFragment: Fragment(){

    val TAG = "Refer_Fragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        return inflater.inflate(R.layout.refer_friend_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        referbtn.setOnClickListener {

            Log.d(TAG,"clicked refer button here")

            addReferredEmailToDatabase()
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

    private fun addReferredEmailToDatabase(){

        Log.d(TAG,"inside referred email to database func")

        val key = FirebaseDatabase.getInstance().getReference().child("referred_email_ids").push().key

        Log.d(TAG,"key is: "+key)

        val email = email_refer_fragment.text.toString()

        if(email.isNotEmpty() && isValidEmailAddress(email)){

            Log.d(TAG,"email is not empty & a valid one")


            FirebaseDatabase.getInstance().getReference().child("referred_email_ids").child(key!!).setValue(email)
                .addOnCompleteListener {

                    if(it.isSuccessful){

                        Log.d(TAG,"added the referred email id")
                        Toast.makeText(context,"Successfully Referred !",Toast.LENGTH_SHORT).show()
                        val intent = Intent(context,ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    }
                    else{

                        Log.d(TAG,"failed to addded referred email id")
                    }
                }
                .addOnFailureListener {

                    Log.d(TAG,"Failure - failed to add referred email ID: "+it.message)
                }

        }
        else{

            Log.d(TAG,"email is empty")
            Toast.makeText(context,"Please provide a valid email address",Toast.LENGTH_SHORT).show()
        }




    }

    private fun isValidEmailAddress(email: String): Boolean{

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}