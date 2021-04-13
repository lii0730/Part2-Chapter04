package com.example.testchapter4.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.testchapter4.model.History

@Dao // Database Access Object -> 데이터베이스에 접근해서 실질적으로 insert, delete 등을 수행하는 메소드를 포함
interface HistoryDAO {
    //TODO: History 전체 객체 조회

    @Query("SELECT * FROM HISTORY")
    fun getAll() : List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM HISTORY")
    fun deleteAll()
}