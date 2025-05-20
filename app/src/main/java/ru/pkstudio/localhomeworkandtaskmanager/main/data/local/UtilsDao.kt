package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.UtilsEntity

@Dao
interface UtilsDao {
    @Insert
    suspend fun insertUtils(utils: UtilsEntity)

    @Update
    suspend fun updateUtils(utils: UtilsEntity)

    @Query("SELECT * FROM utils")
    suspend fun getAllUtils(): List<UtilsEntity>
}