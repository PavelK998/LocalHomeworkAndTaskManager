package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.HomeworkDao
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListHomeworkEntity
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListSubjectWithHomework
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectWithHomework
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.HomeworkRepository
import javax.inject.Inject

class HomeworkRepositoryImpl @Inject constructor(
    private val homeworkDao: HomeworkDao
) : HomeworkRepository {
    override suspend fun insertHomework(homework: HomeworkModel) = withContext(Dispatchers.IO) {
        homeworkDao.insertHomework(homework.toHomeworkEntity())
    }

    override suspend fun deleteHomework(homework: HomeworkModel) = withContext(Dispatchers.IO) {
        homeworkDao.deleteHomework(homework.toHomeworkEntity())
    }

    override suspend fun updateHomework(homework: HomeworkModel) = withContext(Dispatchers.IO) {
        homeworkDao.updateHomework(homework.toHomeworkEntity())
    }

    override suspend fun getAllHomeworkWithSubject() = withContext(Dispatchers.IO) {
        homeworkDao.getAllHomeworkWithSubject().map {
            it.toListSubjectWithHomework()
        }
    }

    override suspend fun getHomeworkWithSubjectById(subjectId: Long) = withContext(Dispatchers.IO) {
        homeworkDao.getHomeworkWithSubjectById(subjectId = subjectId).toSubjectWithHomework()
    }

    override suspend fun getHomeworkById(homeworkId: Long) = withContext(Dispatchers.IO) {
        homeworkDao.getHomeworkById(homeworkId = homeworkId).toHomeworkModel()
    }

    override suspend fun deleteListHomework(homework: List<HomeworkModel>) = withContext(Dispatchers.IO) {
        homeworkDao.deleteListHomework(homework.toListHomeworkEntity())
    }

    override suspend fun changeHomeworkStagesAfterDeleteStage(
        fromStageId: Long,
        targetStageId: Long
    ) = withContext(Dispatchers.IO) {
        homeworkDao.changeHomeworkStagesAfterDeleteStage(
            fromStageId = fromStageId,
            targetStageId = targetStageId
        )
    }
}