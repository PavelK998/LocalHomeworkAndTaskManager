package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectWithHomework

interface HomeworkRepository {
    suspend fun insertHomework(homework: HomeworkModel): Long

    suspend fun deleteHomework(homework: HomeworkModel)

    suspend fun deleteListHomework(homework: List<HomeworkModel>)

    suspend fun updateHomework(homework: HomeworkModel)

    suspend fun getAllHomeworkWithSubject(): Flow<List<SubjectWithHomework>>

    suspend fun getHomeworkWithSubjectById(subjectId: Long): Flow<SubjectWithHomework>

    suspend fun getHomeworkById(homeworkId: Long): HomeworkModel

    suspend fun getHomeworkFlowById(homeworkId: Long): Flow<HomeworkModel>

    suspend fun changeHomeworkStagesAfterDeleteStage(
        fromStageId: Long,
        targetStageId: Long,
        targetStageName: String
    )
}