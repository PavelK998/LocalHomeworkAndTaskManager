package ru.pkstudio.localhomeworkandtaskmanager.main.data.repository

import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId.Companion.invoke
import org.mongodb.kbson.ObjectId
import ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm.SubjectObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toHomeworkObject
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toListSubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.data.mappers.toSubjectObject
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.HomeworkModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.model.SubjectModel
import ru.pkstudio.localhomeworkandtaskmanager.main.domain.repository.SubjectsRepository
import javax.inject.Inject

class SubjectsRepositoryImpl @Inject constructor(
    private val realmDb: Realm
) : SubjectsRepository {
    override suspend fun insertSubject(subject: SubjectModel) = withContext(Dispatchers.IO) {
        realmDb.write {
            copyToRealm(subject.toSubjectObject())
        }
        Unit
    }

    override suspend fun deleteSubject(subject: SubjectModel) = withContext(Dispatchers.IO) {
        realmDb.write {
            val existingObject =
                query<SubjectObject>("_id == $0", subject.toSubjectObject()._id).first().find()
            if (existingObject != null) {
                delete(existingObject)
            } else throw Error("No such object")
        }
    }

    override suspend fun updateSubject(subject: SubjectModel) = withContext(Dispatchers.IO) {
        realmDb.write {
            val subjectObject = subject.toSubjectObject()
            val existingObject =
                query<SubjectObject>("_id == $0", subjectObject._id).first().find()
            if (existingObject != null) {
                copyToRealm(subjectObject, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }


    override suspend fun getAllSubjects() = withContext(Dispatchers.IO) {
        realmDb.query<SubjectObject>().find().asFlow().map {
            it.list.toList().toListSubjectModel()
        }
    }

    override suspend fun insertHomeworkInSubject(subjectId: String, homeworkModel: HomeworkModel)
    = withContext(Dispatchers.IO) {
        realmDb.write {
            val queryObject = query<SubjectObject>("_id == $0", ObjectId(subjectId)).first().find()
            if (queryObject != null){
                findLatest(queryObject)?.homeworkList?.add(homeworkModel.toHomeworkObject())
            }
        }
    }

    override suspend fun updateHomeworkInSubject(subjectId: String, homeworkModel: HomeworkModel)
    = withContext(Dispatchers.IO) {
        realmDb.write {
            val queryObject = query<SubjectObject>("_id == $0", ObjectId(subjectId)).first().find()
            val homeworkObject = homeworkModel.toHomeworkObject()
            if (queryObject != null){
                val latestObject = findLatest(queryObject)
                val index = latestObject?.homeworkList?.indexOfFirst { it._id == homeworkObject._id }
                if (index != null && index != -1) {
                    latestObject.homeworkList[index] = homeworkObject
                }
            }
        }
    }

    override suspend fun deleteHomeworkInSubject(subjectId: String, homeworkModel: HomeworkModel)
    = withContext(Dispatchers.IO) {
        realmDb.write {
            val queryObject = query<SubjectObject>("_id == $0", ObjectId(subjectId)).first().find()
            if (queryObject != null){
                val latestQueryObject = findLatest(queryObject)?.homeworkList
                latestQueryObject?.removeIf { it._id == homeworkModel.toHomeworkObject()._id }
            }
        }
    }

    override suspend fun deleteHomeworkListInSubject(
        subjectId: String,
        homeworkModelList: List<HomeworkModel>
    ) = withContext(Dispatchers.IO) {
        realmDb.write {
            val queryObject = query<SubjectObject>("_id == $0", ObjectId(subjectId)).first().find()
            if (queryObject != null){
                val latestQueryObject = findLatest(queryObject)?.homeworkList
                val idsToDelete = homeworkModelList.map {
                    ObjectId(it.id)
                }.toHashSet()
                latestQueryObject?.removeAll{ homeworkObject ->  
                    idsToDelete.contains(homeworkObject._id)
                }
            }
        }
    }

    override suspend fun getSubjectById(subjectId: String) = withContext(Dispatchers.IO) {
        val queryObject =
            realmDb.query<SubjectObject>("_id == $0", ObjectId(subjectId)).first().find()
        queryObject?.toSubjectModel() ?: throw Exception("No such object")
    }

    override suspend fun getSubjectByIdFlow(subjectId: String) = withContext(Dispatchers.IO) {
        val queryObject =
            realmDb.query<SubjectObject>("_id == $0", ObjectId(subjectId)).first().find()
        queryObject?.asFlow()?.map {
            it.obj?.toSubjectModel() ?: throw Exception()
        } ?: throw Exception()
    }
}