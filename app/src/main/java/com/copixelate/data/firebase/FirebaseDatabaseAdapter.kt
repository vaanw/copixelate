package com.copixelate.data.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

object FirebaseDatabaseAdapter {

    private val root = Firebase.database.reference

    private enum class Paths(val pathString: String) {
        Users("users"),
        ContactCodes("contact_codes"),
        Invitations("invitations"),
        Spaces("spaces"),
        Drawings("drawings"),
        Palettes("palettes"),
        Sizes("sizes")
    }

    internal suspend fun getContactCode(
        uid: String
    ): FirebaseResult<String> =
        root.child(Paths.ContactCodes.pathString)
            .child(uid)
            .requestValue()

    internal suspend fun getContactCodeUid(
        contactCode: String
    ): FirebaseResult<String> =
        root.child(Paths.ContactCodes.pathString)
            .orderByValue()
            .equalTo(contactCode)
            .requestValue()

    internal suspend fun setContactCode(
        uid: String,
        contactCode: String
    ): FirebaseResult<Unit> =
        root.child(Paths.ContactCodes.pathString)
            .child(uid)
            .submitValue(contactCode)

    internal suspend fun pushInvitationJson(
        inviteeUid: String,
        invitation: InvitationJson
    ): FirebaseResult<Unit> =
        root.child(Paths.Invitations.pathString)
            .child(inviteeUid)
            .pushValue(invitation)

//    internal suspend fun getUserJson(uid: String): UserJson =
//        root.child(Paths.Users.pathString)
//            .child(uid)
//            .requestValue()
//
//    internal suspend fun setUserJson(uid: String, data: UserJson) =
//        root.child(Paths.Users.pathString)
//            .child(uid)
//            .submitValue(data)
//
//    internal suspend fun getSpaceJson(spaceKey: String): SpaceJson =
//        root.child(Paths.Spaces.pathString)
//            .child(spaceKey)
//            .requestValue()
//
//    internal suspend fun getDrawingData(drawingKey: String): List<Int> =
//        root.child(Paths.Drawings.pathString)
//            .child(drawingKey)
//            .requestValue()
//
//    internal fun getDrawingDataUpdateFlow(drawingKey: String): Flow<Pair<Int, Int>> =
//        root.child(Paths.Drawings.pathString)
//            .child(drawingKey)
//            .asIndexedChildEventFlow()
//
//    internal suspend fun setDrawingData(drawingKey: String, data: List<Int>) =
//        root.child(Paths.Drawings.pathString)
//            .child(drawingKey)
//            .submitValue(data)
//
//    internal suspend fun getPaletteData(paletteKey: String): List<Int> =
//        root.child(Paths.Palettes.pathString)
//            .child(paletteKey)
//            .requestValue()
//
//    internal suspend fun setPaletteData(paletteKey: String, data: List<Int>) =
//        root.child(Paths.Palettes.pathString)
//            .child(paletteKey)
//            .submitValue(data)
//
//    internal suspend fun getSizeJson(sizeKey: String): SizeJson =
//        root.child(Paths.Sizes.pathString)
//            .child(sizeKey)
//            .requestValue()
//
//    internal suspend fun setSizeJson(sizeKey: String, data: SizeJson) =
//        root.child(Paths.Sizes.pathString)
//            .child(sizeKey)
//            .submitValue(data)


    private inline fun <reified T> DatabaseReference.asIndexedChildEventFlow()
            : Flow<Pair<Int, T>> =
        asChildEventFlow<T>()
            // Convert keys to Ints for use as indexes
            .filter { pair -> pair.first.toIntOrNull() != null }
            .map { pair -> Pair(pair.first.toInt(), pair.second) }

    private inline fun <reified T> DatabaseReference.asChildEventFlow()
            : Flow<Pair<String, T>> =
        callbackFlow {

            val listener = object : ChildEventListener {

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // If both key and value are non-null,
                    // send update as Pair<String, T>
                    snapshot.apply {
                        key?.let { key ->
                            // getValue<T>()?.let { value ->
                            //   trySend(Pair(key, value))
                            // }
                            value?.let { value ->
                                trySend(Pair(key, value as T))
                            }
                        }
                    }
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    cancel(error.message, error.toException())
                }
            }

            addChildEventListener(listener)
            awaitClose { removeEventListener(listener) }
        }

    private suspend inline fun <reified T> Query.requestValue(): FirebaseResult<T> =

        suspendCancellableCoroutine { continuation ->

            val listener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // dataSnapshot.getValue<T>()?.let { value ->
                    //  continuation.resume(value)
                    dataSnapshot.value?.let { value ->
//                        continuation.resume(value as T)
                        continuation.resume(FirebaseResult.Success(value as T))
                    }
//                        ?: continuation.cancel(NullPointerException())
                        ?: continuation.resume(FirebaseResult.Failure(NullPointerException()))
                }

                override fun onCancelled(error: DatabaseError) {
//                    continuation.cancel(error.toException())
                    continuation.resume(FirebaseResult.Failure(error.toException()))
                }
            }

            addListenerForSingleValueEvent(listener)
            continuation.invokeOnCancellation { removeEventListener(listener) }
        }

    private suspend fun DatabaseReference.submitValue(value: Any): FirebaseResult<Unit> =
        try {
            setValue(value).await()
            FirebaseResult.Success(Unit)
        } catch (exception: Exception) {
            FirebaseResult.Failure(exception)
        }

//    private suspend fun DatabaseReference.submitValue(value: Any) {
//        setValue(value).await()
//    }

    private suspend fun DatabaseReference.pushValue(value: Any): FirebaseResult<Unit> =
        try {
            push().setValue(value).await()
            FirebaseResult.Success(Unit)
        } catch (exception: Exception) {
            FirebaseResult.Failure(exception)
        }

//    private suspend fun DatabaseReference.pushValue(value: Any) {
//        val uniqueKeyRef = push()
//        uniqueKeyRef.setValue(value).await()
//    }

}// End FirebaseAdapter
