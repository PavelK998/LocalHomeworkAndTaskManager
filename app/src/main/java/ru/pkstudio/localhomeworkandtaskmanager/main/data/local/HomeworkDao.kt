package ru.pkstudio.localhomeworkandtaskmanager.main.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.HomeworkEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.model.SubjectWithHomeworkData

@Dao
interface HomeworkDao {
    @Insert
    suspend fun insertHomework(homework: HomeworkEntity)

    @Delete
    suspend fun deleteHomework(homework: HomeworkEntity)

    @Update
    suspend fun updateHomework(homework: HomeworkEntity)

    @Transaction
    @Query("SELECT * FROM subjects")
    fun getAllHomeworkWithSubject(): Flow<List<SubjectWithHomeworkData>>

    @Transaction
    @Query("SELECT * FROM subjects WHERE subjectId = :subjectId")
    suspend fun getHomeworkWithSubjectById(subjectId: Long): SubjectWithHomeworkData
}