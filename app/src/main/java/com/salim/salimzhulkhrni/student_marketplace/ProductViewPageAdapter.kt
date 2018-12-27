package com.salim.salimzhulkhrni.student_marketplace

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log

class ProductViewPageAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager){

    val TAG = "product_view_page_adapter_fragment"

    val titles = listOf<String>("Selling","Sold","Favorites")


    override fun getItem(position: Int): Fragment {

        //Log.d(TAG,"pd size: " + productDetails.size)

        Log.d(TAG,"position before: "+position)
        var fragment: Fragment?= null

        if(position == 0 ){

            Log.d(TAG,"if 0 position: "+position)
            fragment = ProfileProductListFragment.newInstance(position)
        }
        else if (position == 1){

            Log.d(TAG,"else 1 position: "+position)
            fragment = ProfileProductListFragment.newInstance(position)
        }
        else{

            Log.d(TAG,"else 2 position: "+position)
            fragment = ProfileProductListFragment.newInstance(position)
        }


        return fragment
    }

    override fun getCount(): Int {

        //Log.d(TAG,"inside count")
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //return super.getPageTitle(position)
        //Log.d(TAG,"pg title pos: "+position)
        return titles[position]

    }
}