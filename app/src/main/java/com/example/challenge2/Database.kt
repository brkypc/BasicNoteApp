package com.example.challenge2

import android.content.Context
import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): MutableList<Note>

    @Query("SELECT * FROM note WHERE noteId = :noteID")
    fun getNote(noteID: Int?): Note

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)
}

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): NoteDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                "notes_database"
            ).build()
        }
    }
}

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int?,
    @ColumnInfo(name = "note_title") var title: String?,
    @ColumnInfo(name = "note_text") var text: String?
)





