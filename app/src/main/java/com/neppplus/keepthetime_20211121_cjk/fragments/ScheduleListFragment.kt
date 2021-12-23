package com.neppplus.keepthetime_20211121_cjk.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.keepthetime_20211121_cjk.EditAppintmentActivity
import com.neppplus.keepthetime_20211121_cjk.R
import com.neppplus.keepthetime_20211121_cjk.adatpers.ScheduleRecyclerAdapter
import com.neppplus.keepthetime_20211121_cjk.databinding.FragmentScheduleListBinding
import com.neppplus.keepthetime_20211121_cjk.datas.BasicResponse
import com.neppplus.keepthetime_20211121_cjk.datas.ScheduleDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ScheduleListFragment : BaseFragment() {

    lateinit var  binding: FragmentScheduleListBinding
    val mScheduleList = ArrayList<ScheduleDate>()
    lateinit var mSheduleAdapter:ScheduleRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEvents()
        setValues()


    }

    override fun setupEvents() {
        binding.btnAddAppointment.setOnClickListener {
            val myIntent = Intent(mContext, EditAppintmentActivity::class.java)
            startActivity(myIntent)
        }
    }

    override fun setValues() {
        getScheduleListFromServer()

        mSheduleAdapter = ScheduleRecyclerAdapter(mContext, mScheduleList)
        binding.appointmentRecyclerView.adapter = mSheduleAdapter
        binding.appointmentRecyclerView.layoutManager = LinearLayoutManager(mContext)
    }

    fun getScheduleListFromServer(){
        apiService.getRequestAppintment().enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful){
                    val br = response.body()!!

                    mScheduleList.clear()
                    mScheduleList.addAll(br.data.appointments)

                    mSheduleAdapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
    }


}