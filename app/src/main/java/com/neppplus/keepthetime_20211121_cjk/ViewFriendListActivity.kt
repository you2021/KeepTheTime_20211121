package com.neppplus.keepthetime_20211121_cjk

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.keepthetime_20211121_cjk.adatpers.RequestedRecyclerAdapter
import com.neppplus.keepthetime_20211121_cjk.databinding.ActivityViewFriendListBinding
import com.neppplus.keepthetime_20211121_cjk.datas.BasicResponse
import com.neppplus.keepthetime_20211121_cjk.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ViewFriendListActivity : BaseActivity() {

    lateinit var binding: ActivityViewFriendListBinding

    val mMyFriendsList = ArrayList<UserData>()
//    lateinit var  mMyFriendsAdapter: MyFriendRecyclerAdapter
    lateinit var  mRequestedFriendsAdapter: RequestedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_friend_list)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.btnAddFriend.setOnClickListener {

            // 친구 추가 화면 이동
            val myIntent = Intent(mContext, AddFriendActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {
        getMyFriendFromServer()

//        mMyFriendsAdapter = MyFriendRecyclerAdapter(mContext, mMyFriendsList)
        mRequestedFriendsAdapter = RequestedRecyclerAdapter(mContext, mMyFriendsList)
        binding.myFriendsRecyclerView.adapter = mRequestedFriendsAdapter
        // 여러 형태로 목록 배치 가능. -> 어떤형태로 보여줄건지? 리상이클려뷰에 세팅.
        binding.myFriendsRecyclerView.layoutManager = LinearLayoutManager(mContext)


    }

    fun getMyFriendFromServer() {
        apiService.getRequestMyFriends("requested").enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if(response.isSuccessful){

                    val br = response.body()!!
                    mMyFriendsList.addAll(br.data.friends)
                    mRequestedFriendsAdapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
    }
}