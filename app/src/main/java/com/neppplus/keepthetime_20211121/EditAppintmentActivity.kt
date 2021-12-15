package com.neppplus.keepthetime_20211121

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import com.neppplus.keepthetime_20211121.databinding.ActivityEditAppintmentBinding
import com.neppplus.keepthetime_20211121.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAppintmentActivity : BaseActivity() {

    lateinit var binding : ActivityEditAppintmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appintment,)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.txtDate.setOnClickListener {
            // 날짜 선택 팝업 (DatePickerDialog) 사용 예시

            // 선택 완료시 할일 설정(JAVA - Interface) => 변수에 담아두자

            val dateSetListener = object  : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                    // 실제로 날짜가 선택되면 할일 적는 공간

                    Log.d("선택한년월일","${year}년 ${month}월 ${dayOfMonth}일 선택됨")

                }

            }

            // 실제 팝업창 띄우기기

            // Kotlin : JAVA 기반 언어 => 월 : 0 ~ 11로 만들어져 있음.

            val datePickDialog = DatePickerDialog(
                mContext,
                dateSetListener,
                2021,
                12,
                5
            )

            datePickDialog.show()

        }

        binding.btnOk.setOnClickListener {
            val inputTitle = binding.edtTitle.text.toString()
//            val inputDateTime = binding.edtDateTime.text.toString()
            val inputPlace = binding.edtPlace.text.toString()
            val inputLat = binding.edtLatitude.text.toString().toDouble()
            val inputLng = binding.edtLongitude.text.toString().toDouble()

            apiService.postRequestAppointment(inputTitle,"임시 값",inputPlace,inputLat,inputLng)
                .enqueue(object : Callback<BasicResponse>{
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {

                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }

                })
        }
    }

    override fun setValues() {

    }
}