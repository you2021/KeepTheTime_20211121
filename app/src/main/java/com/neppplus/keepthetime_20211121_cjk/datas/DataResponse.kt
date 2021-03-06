package com.neppplus.keepthetime_20211121_cjk.datas

class DataResponse(
    var user: UserData,
    var token : String,

    // 친구 목록 API의 파싱에만 사용용
    var friends : List<UserData>,

    val users : List<UserData>,

    // 약속 목록 API 파싱에서 활용.
    var appointments : List<ScheduleDate>
){
}