package ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class SubjectObject: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var subjectName: String = ""
    var comment: String = ""
    var homeworkList: RealmList<HomeworkObject> = realmListOf()
}