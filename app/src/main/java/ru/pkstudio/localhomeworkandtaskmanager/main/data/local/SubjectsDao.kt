package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectsDao {
    @Insert
    suspend fun insertSubject(subject: SubjectsEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectsEntity)

    @Update
    suspend fun updateSubject(subject: SubjectsEntity)

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectsEntity>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: Long): SubjectsEntity
}