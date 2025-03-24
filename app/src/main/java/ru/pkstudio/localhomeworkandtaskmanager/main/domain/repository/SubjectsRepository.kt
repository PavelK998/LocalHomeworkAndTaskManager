package ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel

interface SubjectsRepository {

    suspend fun insertSubject(subject: SubjectModel)

    suspend fun deleteSubject(subject: SubjectModel)

    suspend fun updateSubject(subject: SubjectModel)

    suspend fun getAllSubjects(): Flow<List<SubjectModel>>

    suspend fun getSubjectById(subjectId: Long): SubjectModel
}