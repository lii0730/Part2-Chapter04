package com.example.testchapter4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//TODO: History라는 Table을 생성
@Entity //data model class
data class History(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val expression: String?,
    @ColumnInfo val result: String?
)