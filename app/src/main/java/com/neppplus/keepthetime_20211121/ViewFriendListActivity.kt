package com.neppplus.keepthetime_20211121

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.neppplus.keepthetime_20211121.adatpers.MyFriendAdapter
import com.neppplus.keepthetime_20211121.databinding.ActivityViewFriendListBinding
import com.neppplus.keepthetime_20211121.datas.BasicResponse
import com.neppplus.keepthetime_20211121.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ViewFriendListActivity : BaseActivity() {

    lateinit var binding: ActivityViewFriendListBinding

    val mMyFriendsList = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_friend_list)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {
        getMyFriendFromServer()

//        mFriendAdapter = MyFriendAdapter(mContext, R.layout.myfriendlistitem, mMyFriendList)
//        binding.myFriendListView.adapter = mFriendAdapter


    }

    fun getMyFriendFromServer() {
        apiService.getRequestMyFriends("my").enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if(response.isSuccessful){

                    val br = response.body()!!
                    mMyFriendsList.addAll(br.data.friends)

                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
    }
}