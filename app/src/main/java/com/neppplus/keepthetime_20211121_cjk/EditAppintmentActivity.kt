package com.neppplus.keepthetime_20211121_cjk

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.neppplus.keepthetime_20211121_cjk.databinding.ActivityEditAppintmentBinding
import com.neppplus.keepthetime_20211121_cjk.datas.BasicResponse
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditAppintmentActivity : BaseActivity() {

    // 약속을 잡을 일시를 저장할 변수 (Calendar)

    val mSelectedDateTime = Calendar.getInstance() // 기본값 : 현재 일시경

    // 약속을 잡을 위치를 저장할 변수 (네이버 - LatLng)
    // 그 위치를 보여줄 마커(네이버 - Marker)
    // 처음 화면이 나타날때는, 아직 선책 안한 상태 => 위치도/ 마커도 아직 없다 (초기 값 - null)
    var mSelectedLatLng : LatLng? = null
    var mSelectedMarker : Marker? = null

    var mPath: PathOverlay? = null

    lateinit var binding: ActivityEditAppintmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appintment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.txtTime.setOnClickListener {

            // 시간 선택 팝업(TimePickerDialog) 사용 예시

            // 1. 선택 완료시 할일 (OnTimeSetListener) 설정 -=> 변수에 담아두자.
            val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
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

            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
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


                    // txtDate의 문구를 -> 2021-08-05 와 같은 양식으로 가공해서 텍스트 세팅

                }

            }

            // 실제 팝업창 띄우기기

            // Kotlin : JAVA 기반 언어 => 월 : 0 ~ 11로 만들어져 있음.

            // 오늘 날짜를 기본으로 띄우도록. => mSelectedDateTime에 저장된 값 활용
            // 선택한 이후로는 => 이전에 선택한 날짜가 뜨도록

            val datePickDialog = DatePickerDialog(
                mContext,
                dateSetListener,
                mSelectedDateTime.get(Calendar.YEAR),
                mSelectedDateTime.get(Calendar.MONTH),
                mSelectedDateTime.get(Calendar.DAY_OF_MONTH)
            )



            datePickDialog.show()

        }

        binding.btnOk.setOnClickListener {

            // 입력갑 검증 (vaildation)

            // 1. 일자/ 시간을 모두 선택했는지?
            if (binding.txtDate.text == "날짜 선택" || binding.txtTime.text == "시간 선택") {
                // 둘중 하나를 아직 입력하지 않는 상황.
                Toast.makeText(mContext, "약속 일시를 모두 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // 2. 선택된 일시(mSelectedDateTime)가, 현재 시간보다 더 나중 시간인가?
            // => 과거의 약속이라면 액에서 등록 거부

            // 현재시간을 변수에 저장
            val now = Calendar.getInstance() // 현재 시간 (클릭된 시간)을 기록

            // 두개의 시간을 양으로 변환해서 대소비교
            if (mSelectedDateTime.timeInMillis < now.timeInMillis) {
                // 약속시간이, 현재시간보다 덜 시간이 흐른 상태. (더 이전 시간)

                // 약속시간은 지금보다 미래여야 의미가 있다.
                Toast.makeText(mContext, "약속시간은 더 미래의 시간으로 설정해 주세요", Toast.LENGTH_SHORT).show()

                // 이 함수를 강제 종료 (서버에 데이터 보내는걸 취소 )
                return@setOnClickListener
            }

            // 약속 장소 추가 검증
            if (mSelectedLatLng == null){
                Toast.makeText(mContext, "약속 장소를 지도에서 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inputTitle = binding.edtTitle.text.toString()
//            val inputDateTime = binding.edtDateTime.text.toString()
            // mSelectedDateTime에 저장된 약속일시를 -> String으로 가공(SimpleDateFormat) => 서버에 첨부

            val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val finalDateTimeStr = serverFormat.format(mSelectedDateTime.time)

            val inputPlace = binding.edtPlace.text.toString()
//            val inputLat = binding.edtLatitude.text.toString().toDouble()
//            val inputLng = binding.edtLongitude.text.toString().toDouble()

            apiService.postRequestAppointment(
                inputTitle,
                finalDateTimeStr,
                inputPlace,
                mSelectedLatLng!!.latitude,
                mSelectedLatLng!!.longitude
            )
                .enqueue(object : Callback<BasicResponse> {
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
        
        binding.naverMap.getMapAsync {

            // 로딩이 끝난 네이버멥 객체(인스턴스가) => it 변수에 담겨 있다.
            val naverMap = it

            // 기능 : 지도를 클릭하면 -> 클릭된 지점에 마커 찍기(커스텀 마커 예시)
            naverMap.setOnMapClickListener { pointF, latLng ->



                // 클릭된 좌표 latLng -> 카메라 이동(정가운데) / 마커 찍기

                val cameraUpdate = CameraUpdate.scrollTo(latLng)
                naverMap.moveCamera(cameraUpdate)

                // 선택 한 위치를 멤버변수에 담아두자.
                mSelectedLatLng = latLng

                // 선택한 위치를 보여줄 마커도 (만들어진게 없다면 새로)생성
                if (mSelectedMarker == null){
                    mSelectedMarker = Marker()
                }

                mSelectedMarker!!.position = latLng
                mSelectedMarker!!.map = naverMap

                // 하나의 지점(본인집-startingPaint)에서 -> 클릭한 지점(latlng)까지 선 긋기

                val startingPoint = LatLng(37.49475257520079, 126.8448165273176)

                // 추랄지 ~ 도착지까지의 대중교통 정거장 목록을 위경도 추출
                // ODsay 라이브러리 설치 => API 활용.
                val myODsayService = ODsayService.init(mContext, resources.getString(R.string.odsay_key))
                myODsayService.requestSearchPubTransPath(
                    startingPoint.longitude.toString(),
                    startingPoint.latitude.toString(),
                    latLng.longitude.toString(),
                    latLng.latitude.toString(),
                    null,
                    null,
                    null,
                    object  : OnResultCallbackListener{
                        override fun onSuccess(p0: ODsayData?, p1: API?) {
                            val jsonObj = p0!!.json
                            Log.d("길찾기응답", jsonObj.toString())
                        }

                        override fun onError(p0: Int, p1: String?, p2: API?) {

                        }

                    }
                )


                // 선이 그어질 경로 (여러 지점의 연결로 표현)

                // PathOverlay() 선 긋는 객체 생성 => 지도에 클릭될때 마다 새로 생성됨 => 선도 하나씩 새로 그어짐.
//                val path = PathOverlay()

                // mPath 변수가 null 상태라면? 새 객체 만들어서 채워줌.
                if (mPath == null){
                    mPath = PathOverlay()
                }

                mPath!!.coords = arrayListOf(
                    startingPoint,
                    LatLng(37.494,126.844),

                    latLng
                )

                mPath!!.map = naverMap



//                val marker = Marker()  // 클릭될때 마다, 새로운 마커가 생성됨.
//                // 멤버변수로 하나의 마커만 만들고  => 클릭되면 그 마커의 위치만 변
//                marker.position = latLng
//                marker.map = naverMap
            }




//            // 예시. 카메라를 본인 집근처로 이동
//
//            // 위경도 정보 => 카메라 이동 명령을 변수에 저장만.
//
//            val latLog =LatLng(37.4944825843259, 126.84419596180862)
//            val cameraUpdate = CameraUpdate.scrollTo(latLog)
//            naverMap.moveCamera(cameraUpdate)
//
//            // 마커를 본인 집근처 띄어보기
//            val marker = Marker()
//            marker.position = latLog
//            marker.map = naverMap
//
//            marker.icon = OverlayImage.fromResource(R.drawable.ic_pin)
//
//            // 네이버 지도 클릭 이벤트 달아보기
//            naverMap.setOnMapClickListener{point,  latLog ->
//                Toast.makeText(mContext, "위도 : ${latLog.latitude}, 경도 : ${latLog.longitude}", Toast.LENGTH_SHORT).show()
//            }

        }

    }

    override fun onStart() {
        super.onStart()
        binding.naverMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.naverMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.naverMap.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.naverMap.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding.naverMap.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.naverMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.naverMap.onLowMemory()
    }


}