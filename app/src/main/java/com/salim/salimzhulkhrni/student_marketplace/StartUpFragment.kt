package com.salim.salimzhulkhrni.student_marketplace

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.startup_fragment.view.*
import java.lang.RuntimeException

class StartUpFragment: Fragment(){

    val TAG = "startup_Fragment"

    private var listener: onStartUpFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        return inflater.inflate(R.layout.startup_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.login_button_mainActivity.setOnClickListener {
            Log.d(TAG,"login button clicked")
            listener!!.onLoginClick()
        }

        view.signup_button_mainActivity.setOnClickListener {

            Log.d(TAG,"sign up button clicked")
            listener!!.onSignUpClick()
        }
    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "on attach")
        super.onAttach(context)
        if(context is onStartUpFragmentListener)
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
    }

    interface onStartUpFragmentListener{

        fun onLoginClick()
        fun onSignUpClick()
    }

}