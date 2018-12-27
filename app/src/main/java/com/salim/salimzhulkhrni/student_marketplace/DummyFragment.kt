package com.salim.salimzhulkhrni.student_marketplace

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DummyFragment: Fragment(){

    val TAG = "dummy_Fragment"

    companion object {


        fun newInstance(position: Int): Fragment{

            val args = Bundle()
            args.putInt("choice",position)
            val fragment = DummyFragment()
            fragment.arguments  = args
            return fragment

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "createview")

        Log.d(TAG,"choice: "+ arguments?.getInt("choice") )
        return inflater.inflate(R.layout.dummy_fragment, container, false) // inflate the fragment layout. loaded the xml file into the view,
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


}