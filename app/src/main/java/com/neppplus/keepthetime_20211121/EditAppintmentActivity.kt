package com.neppplus.keepthetime_20211121

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.keepthetime_20211121.databinding.ActivityEditAppintmentBinding
import com.neppplus.keepthetime_20211121.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditAppintmentActivity : BaseActivity() {

    // 약속을 잡을 일시를 저장할 변수 (Calendar)

    val mSelectedDateTime = Calendar.getInstance() // 기본값 : 현재 일시

    lateinit var binding : ActivityEditAppintmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appintment,)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.txtTime.setOnClickListener {

            // 시간 선택 팝업(TimePickerDialog) 사용 예시

            // 1. 선택 완료시 할일 (OnTimeSetListener) 설정 -=> 변수에 담아두자.
            val timeSetListener = object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {

//                    Log.d("선택된시간", "${hourOfDay}시, ${minute}분")

                    // 선택한 시간도 실제로 저장
                    mSelectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    mSelectedDateTime.set(Calendar.MINUTE, hourOfDay)

//                  선택한 시간을 -> 오후 4:05 형태로 textTime에 표시
                    // SimpleDateFormat 활용

                    val timeFormat = SimpleDateFormat("a h:mm")
                    val timeStr = timeFormat.format(mSelectedDateTime.time)

                    binding.txtTime.text = timeStr
                }

            }

            // 2. 시간 선택 팝업 출현

            val timePicker = TimePickerDialog(
                mContext,
                timeSetListener,
                15,
                30,
                false
            )

            timePicker.show()

        }

        binding.txtDate.setOnClickListener {
            // 날짜 선택 팝업 (DatePickerDialog) 사용 예시

            // 선택 완료시 할일 설정(JAVA - Interface) => 변수에 담아두자

            val dateSetListener = object  : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                    // 실제로 날짜가 선택되면 할일 적는 공간

//                    Log.d("선택한년월일","${year}년 ${month}월 ${dayOfMonth}일 선택됨")

                    // 선택된 일시를 저장할 변수에, 연/월/일을 저장
//                    mSelectedDateTime.set(Calendar.YEAR, year)
//                    mSelectedDateTime.set(Calendar.MONTH, month)
//                    mSelectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // 년/ 월/일을 한번에 저장하는 set 함수 활용
                    mSelectedDateTime.set(year, month, dayOfMonth)


                    // txtDate 의 문구를 -> 21년 8월 5일 와 같은 양식으로 가공해서 텍스트 세팅

                    val dateFormat = SimpleDateFormat("yy년 M월 d일")
                    val dateStr = dateFormat.format(mSelectedDateTime.time)

                    binding.txtDate.text = dateStr


                    // txtDate 의 문구를 -> 2021-08-05 와 같은 양식으로 가공해서 텍스트 세팅

//                    // Calendar 를 다룰 양식만 미리 지정
//                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//
//                    // Calendar => String 변환.
//                    val dateStr = dateFormat.format(mSelectedDateTime.time)
//
//                    binding.txtDate.text = dateStr



                }

            }

            // 실제 팝업창 띄우기기

            // Kotlin : JAVA 기반 언어 => 월 : 0 ~ 11로 만들어져 있음.

            // 오늘 날짜를 기본으로 띄우도록. => mSelectedDateTime에 저장된 값 활용
            // 선택한 이후로는 => 이전에 선택한 날짜가 뜨도록

            val datePickDialog = DatePickerDialog(
                mContext,
                dateSetListener,
                mSelectedDateTime.get( Calendar.YEAR ),
                mSelectedDateTime.get( Calendar.MONTH ),
                mSelectedDateTime.get( Calendar.DAY_OF_MONTH )
            )



            datePickDialog.show()

        }

        binding.btnOk.setOnClickListener {

            // 입력갑 검증 (vaildation)

            // 1. 선택된 일시(mSelectedDateTime)가, 현재 시간보다 더 나중 시간인가?
            // => 과거의 약속이라면 액에서 등록 거부

            // 현재시간을 변수에 저장
            val now = Calendar.getInstance() // 현재 시간 (클릭된 시간)을 기록

            // 두개의 시간을 양으로 변환해서 대소비교
            if (mSelectedDateTime.timeInMillis < now.timeInMillis){
                // 약속시간이, 현재시간보다 덜 시간이 흐른 상태. (더 이전 시간)

                // 약속시간은 지금보다 미래여야 의미가 있다.
                Toast.makeText(mContext, "약속시간은 더 미래의 시간으로 설정해 주세요", Toast.LENGTH_SHORT).show()

                // 이 함수를 강제 종료 (서버에 데이터 보내는걸 취소 )
                return@setOnClickListener
            }

            val inputTitle = binding.edtTitle.text.toString()
//            val inputDateTime = binding.edtDateTime.text.toString()
            // mSelectedDateTime에 저장된 약속일시를 -> String으로 가공(SimpleDateFormat) => 서버에 첨부

            val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val finalDateTimeStr = serverFormat.format(mSelectedDateTime.time)

            val inputPlace = binding.edtPlace.text.toString()
            val inputLat = binding.edtLatitude.text.toString().toDouble()
            val inputLng = binding.edtLongitude.text.toString().toDouble()

            apiService.postRequestAppointment(inputTitle,finalDateTimeStr,inputPlace,inputLat,inputLng)
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