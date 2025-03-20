package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.SubjectsDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListSubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import javax.inject.Inject

class SubjectsRepositoryImpl @Inject constructor(
    private val subjectsDao: SubjectsDao
): SubjectsRepository {
    override suspend fun insertSubject(subject: SubjectModel) = withContext(Dispatchers.IO) {
        subjectsDao.insertSubject(subject.toSubjectEntity())
    }

    override suspend fun deleteSubject(subject: SubjectModel) = withContext(Dispatchers.IO) {
        subjectsDao.deleteSubject(subject.toSubjectEntity())
    }

    override suspend fun updateSubject(subject: SubjectModel) = withContext(Dispatchers.IO) {
        subjectsDao.updateSubject(subject.toSubjectEntity())
    }

    override suspend fun getAllSubjects() = withContext(Dispatchers.IO) {
        subjectsDao.getAllSubjects().map { subjects ->
            subjects.toListSubjectModel()
        }
    }

    override suspend fun getSubjectById(subjectId: Long): SubjectModel = withContext(Dispatchers.IO) {
        subjectsDao.getSubjectById(subjectId).toSubjectModel()
    }


}