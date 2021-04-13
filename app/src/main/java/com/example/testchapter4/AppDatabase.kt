package com.example.testchapter4

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testchapter4.DAO.HistoryDAO
import com.example.testchapter4.model.History


// database 홀더를 포함하며, 앱에 영구 저장되는 데이터와 기본 연결을 위한 주 액세스 지점
// RoomDatabase를 extend 하는 추상클래스여야 하며, 테이블과 버전을 정의하는 곳
@Database(entities = arrayOf(History::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao() : HistoryDAO
}