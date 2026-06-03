package com.hearthealth.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hearthealth.app.data.entity.LifestyleData
import kotlinx.coroutines.flow.Flow

@Dao
interface LifestyleDao {

    @Query("SELECT * FROM lifestyle_data ORDER BY date DESC")
    fun getAllData(): Flow<List<LifestyleData>>

    @Query("SELECT * FROM lifestyle_data ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<LifestyleData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: LifestyleData): Long
}
