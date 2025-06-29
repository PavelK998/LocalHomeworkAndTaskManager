package ru.pkstudio.localhomeworkandtaskmanager.main.data.local.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class StageObject: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var color: Int = -1
    var stageName: String = ""
    var position: Int = -1
    var isFinishStage: Boolean = false
}