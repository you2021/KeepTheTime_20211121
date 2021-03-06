package com.neppplus.keepthetime_20211121_cjk

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.neppplus.keepthetime_20211121_cjk.databinding.ActivityViewPlaceMapBinding
import com.neppplus.keepthetime_20211121_cjk.datas.ScheduleDate

class ViewPlaceMapActivity : BaseActivity() {

    lateinit var binding : ActivityViewPlaceMapBinding

    lateinit var mScheduleDate: ScheduleDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_view_place_map)
        binding.naverMapView.onCreate(savedInstanceState)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mScheduleDate = intent.getSerializableExtra("schedule") as ScheduleDate

        // 0. 프로젝트에 네이버지도 설치 (완료)

        // 1. 화면(xml)에 네이버 맵 띄워주기

        // 2. 네이버 맵 객체를 실제로 얻어내기 -> getMapAsync
        binding.naverMapView.getMapAsync {

            val naverMap = it

            // 3. 카메라 이동 / 마커 추가 (받아온 스케쥴의 위도/경도 이용)

            // 위치(좌표) 데이터 객체

            val coord = LatLng( mScheduleDate.latitude , mScheduleDate.longitude )
            val cameraUpdate = CameraUpdate.scrollTo(coord)
            naverMap.moveCamera(cameraUpdate)

            val marker = Marker()
            marker.position = coord
            marker.map = naverMap

            // 추가기능 체험 - 정보장(말풍선) => 마커에 반영

            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext){
                override fun getText(p0: InfoWindow): CharSequence {
                    return mScheduleDate.place
                }

            }

            infoWindow.open(marker)

            naverMap.setOnMapClickListener { pointF, latLng ->

                // 지도 아무데나 클릭하면, 정보창 닫기
                infoWindow.close()

            }

            marker.setOnClickListener {

                if(marker.infoWindow == null){

                    // 정보창이 안열려 있는 상태
                    infoWindow.open(marker)
                }else{
                    // 이미 정보창이 열림 상태 => 닫아주자
                    infoWindow.close()
                }

                return@setOnClickListener true
            }

        }



    }

    override fun onStart() {
        super.onStart()
        binding.naverMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.naverMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.naverMapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.naverMapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding.naverMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.naverMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.naverMapView.onLowMemory()
    }
}