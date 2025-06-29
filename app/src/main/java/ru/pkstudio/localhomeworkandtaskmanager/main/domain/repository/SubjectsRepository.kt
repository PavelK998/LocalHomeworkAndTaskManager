package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel

interface SubjectsRepository {

    suspend fun insertSubject(subject: SubjectModel)

    suspend fun deleteSubject(subject: SubjectModel)

    suspend fun updateSubject(subject: SubjectModel)

    suspend fun insertHomeworkInSubject(subjectId: String, homeworkModel: HomeworkModel)

    suspend fun updateHomeworkInSubject(subjectId: String, homeworkModel: HomeworkModel)

    suspend fun deleteHomeworkInSubject(subjectId: String, homeworkModel: HomeworkModel)

    suspend fun deleteHomeworkListInSubject(subjectId: String, homeworkModelList: List<HomeworkModel>)

    suspend fun getAllSubjects(): Flow<List<SubjectModel>>

    suspend fun getSubjectById(subjectId: String): SubjectModel

    suspend fun getSubjectByIdFlow(subjectId: String): Flow<SubjectModel>
}