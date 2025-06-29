package ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm


import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class HomeworkObject: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var color: Int = -1
    var importance: Int = -1
    var subjectId: Long = -1
    var addDate: String = ""
    var name: String = ""
    var stage: String = ""
    var stageId: String = ""
    var description: String = ""
    var startDate: String? = null
    var endDate: String? = null
    var isFinished: Boolean = false
    var imageNameList: RealmList<String> = realmListOf()
}