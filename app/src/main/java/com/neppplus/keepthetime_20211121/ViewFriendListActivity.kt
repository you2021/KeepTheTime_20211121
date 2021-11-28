package com.neppplus.keepthetime_20211121

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.keepthetime_20211121.adatpers.MyFriendAdapter
import com.neppplus.keepthetime_20211121.adatpers.MyFriendRecyclerAdapter
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
    lateinit var  mMyFriendsAdapter: MyFriendRecyclerAdapter

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

        mMyFriendsAdapter = MyFriendRecyclerAdapter(mContext, mMyFriendsList)
        binding.myFriendsRecyclerView.adapter = mMyFriendsAdapter
        // 여러 형태로 목록 배치 가능. -> 어떤형태로 보여줄건지? 리상이클려뷰에 세팅.
        binding.myFriendsRecyclerView.layoutManager = LinearLayoutManager(mContext)


    }

    fun getMyFriendFromServer() {
        apiService.getRequestMyFriends("my").enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if(response.isSuccessful){

                    val br = response.body()!!
                    mMyFriendsList.addAll(br.data.friends)
                    mMyFriendsAdapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
    }
}