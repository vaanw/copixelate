package com.copixelate.data.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
        Spaces("spaces"),
        Drawings("drawings"),
        Palettes("palettes"),
        Sizes("sizes")
    }

    internal suspend fun getSpaceJson(spaceKey: String): SpaceJson =
        root.child(Paths.Spaces.pathString)
            .child(spaceKey)
            .requestValue()

    internal suspend fun getDrawingData(drawingKey: String): List<Int> =
        root.child(Paths.Drawings.pathString)
            .child(drawingKey)
            .requestValue()

    internal fun getDrawingDataUpdateFlow(drawingKey: String): Flow<Pair<Int, Int>> =
        root.child(Paths.Drawings.pathString)
            .child(drawingKey)
            .asIndexedChildEventFlow()

    internal suspend fun setDrawingData(drawingKey: String, data: List<Int>) =
        root.child(Paths.Drawings.pathString)
            .child(drawingKey)
            .submitValue(data)

    internal suspend fun getPaletteData(paletteKey: String): List<Int> =
        root.child(Paths.Palettes.pathString)
            .child(paletteKey)
            .requestValue()

    internal suspend fun setPaletteData(paletteKey: String, data: List<Int>) =
        root.child(Paths.Palettes.pathString)
            .child(paletteKey)
            .submitValue(data)

    internal suspend fun getSizeJson(sizeKey: String): SizeJson =
        root.child(Paths.Sizes.pathString)
            .child(sizeKey)
            .requestValue()

    internal suspend fun setSizeJson(sizeKey: String, data: SizeJson) =
        root.child(Paths.Sizes.pathString)
            .child(sizeKey)
            .submitValue(data)


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

    private suspend inline fun <reified T> DatabaseReference.requestValue(): T =

        suspendCancellableCoroutine { continuation ->

            val listener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // dataSnapshot.getValue<T>()?.let { value ->
                    //  continuation.resume(value)
                    dataSnapshot.value?.let { value ->
                        continuation.resume(value as T)
                    } ?: continuation.cancel(NullPointerException())
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.cancel(error.toException())
                }
            }

            addListenerForSingleValueEvent(listener)
            continuation.invokeOnCancellation { removeEventListener(listener) }
        }

    private suspend fun DatabaseReference.submitValue(value: Any) {
        setValue(value).await()
    }

}// End FirebaseAdapter
