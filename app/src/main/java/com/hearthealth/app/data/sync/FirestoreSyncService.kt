package com.hearthealth.app.data.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hearthealth.app.data.entity.JournalEntry
import com.hearthealth.app.data.entity.LifestyleData
import com.hearthealth.app.data.entity.Medication
import com.hearthealth.app.data.entity.Reminder
import kotlinx.coroutines.tasks.await

class FirestoreSyncService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val uid: String?
        get() = auth.currentUser?.uid

    private fun userDoc() = uid?.let { firestore.collection("users").document(it) }

    // ---- Medications ----

    suspend fun syncMedications(medications: List<Medication>) {
        val doc = userDoc() ?: return
        val batch = firestore.batch()
        val col = doc.collection("medications")

        medications.forEach { med ->
            val ref = col.document(med.id.toString())
            batch.set(ref, med.toMap(), SetOptions.merge())
        }
        batch.commit().await()
    }

    suspend fun fetchMedications(): List<Medication> {
        val doc = userDoc() ?: return emptyList()
        val snapshot = doc.collection("medications").get().await()
        return snapshot.documents.mapNotNull { it.toMedication() }
    }

    suspend fun deleteMedicationRemote(medicationId: Long) {
        val doc = userDoc() ?: return
        doc.collection("medications").document(medicationId.toString()).delete().await()
        // Also delete associated reminders
        val reminders = doc.collection("reminders")
            .whereEqualTo("medicationId", medicationId).get().await()
        val batch = firestore.batch()
        reminders.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    // ---- Reminders ----

    suspend fun syncReminders(reminders: List<Reminder>) {
        val doc = userDoc() ?: return
        val batch = firestore.batch()
        val col = doc.collection("reminders")

        reminders.forEach { rem ->
            val ref = col.document(rem.id.toString())
            batch.set(ref, rem.toMap(), SetOptions.merge())
        }
        batch.commit().await()
    }

    suspend fun fetchReminders(): List<Reminder> {
        val doc = userDoc() ?: return emptyList()
        val snapshot = doc.collection("reminders").get().await()
        return snapshot.documents.mapNotNull { it.toReminder() }
    }

    // ---- Journal Entries ----

    suspend fun syncJournalEntries(entries: List<JournalEntry>) {
        val doc = userDoc() ?: return
        val batch = firestore.batch()
        val col = doc.collection("journal_entries")

        entries.forEach { entry ->
            val ref = col.document(entry.id.toString())
            batch.set(ref, entry.toMap(), SetOptions.merge())
        }
        batch.commit().await()
    }

    suspend fun fetchJournalEntries(): List<JournalEntry> {
        val doc = userDoc() ?: return emptyList()
        val snapshot = doc.collection("journal_entries").get().await()
        return snapshot.documents.mapNotNull { it.toJournalEntry() }
    }

    // ---- Lifestyle Data ----

    suspend fun syncLifestyleData(dataList: List<LifestyleData>) {
        val doc = userDoc() ?: return
        val batch = firestore.batch()
        val col = doc.collection("lifestyle_data")

        dataList.forEach { data ->
            val ref = col.document(data.id.toString())
            batch.set(ref, data.toMap(), SetOptions.merge())
        }
        batch.commit().await()
    }

    suspend fun fetchLifestyleData(): List<LifestyleData> {
        val doc = userDoc() ?: return emptyList()
        val snapshot = doc.collection("lifestyle_data").get().await()
        return snapshot.documents.mapNotNull { it.toLifestyleData() }
    }

    // ---- Full Sync ----

    suspend fun pushAllData(
        medications: List<Medication>,
        reminders: List<Reminder>,
        journalEntries: List<JournalEntry>,
        lifestyleData: List<LifestyleData>
    ) {
        syncMedications(medications)
        syncReminders(reminders)
        syncJournalEntries(journalEntries)
        syncLifestyleData(lifestyleData)
    }
}

// ---- Extension mappers ----

private fun Medication.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "dosage" to dosage,
    "isActive" to isActive,
    "createdAt" to createdAt
)

private fun com.google.firebase.firestore.DocumentSnapshot.toMedication(): Medication? {
    return try {
        Medication(
            id = getLong("id") ?: return null,
            name = getString("name") ?: return null,
            dosage = getString("dosage") ?: return null,
            isActive = getBoolean("isActive") ?: true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    } catch (_: Exception) { null }
}

private fun Reminder.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "medicationId" to medicationId,
    "hour" to hour,
    "minute" to minute
)

private fun com.google.firebase.firestore.DocumentSnapshot.toReminder(): Reminder? {
    return try {
        Reminder(
            id = getLong("id") ?: return null,
            medicationId = getLong("medicationId") ?: return null,
            hour = getLong("hour")?.toInt() ?: return null,
            minute = getLong("minute")?.toInt() ?: return null
        )
    } catch (_: Exception) { null }
}

private fun JournalEntry.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "feelings" to feelings,
    "symptoms" to symptoms,
    "notes" to notes,
    "createdAt" to createdAt
)

private fun com.google.firebase.firestore.DocumentSnapshot.toJournalEntry(): JournalEntry? {
    return try {
        JournalEntry(
            id = getLong("id") ?: return null,
            feelings = getString("feelings") ?: return null,
            symptoms = getString("symptoms") ?: return null,
            notes = getString("notes") ?: "",
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    } catch (_: Exception) { null }
}

private fun LifestyleData.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "exerciseMinutes" to exerciseMinutes,
    "heartRate" to heartRate,
    "stepCount" to stepCount,
    "date" to date
)

private fun com.google.firebase.firestore.DocumentSnapshot.toLifestyleData(): LifestyleData? {
    return try {
        LifestyleData(
            id = getLong("id") ?: return null,
            exerciseMinutes = getLong("exerciseMinutes")?.toInt() ?: return null,
            heartRate = getLong("heartRate")?.toInt() ?: return null,
            stepCount = getLong("stepCount")?.toInt() ?: 0,
            date = getLong("date") ?: System.currentTimeMillis()
        )
    } catch (_: Exception) { null }
}
