package com.example.films

import android.content.Context
import androidx.room.*

@Entity(tableName = "movie_records_table")
data class MovieRecord(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "notes")
    var notes: String
)

@Dao
interface DbDao {
    @Insert
    suspend fun insert(record: MovieRecord)
    @Update
    suspend fun update(record: MovieRecord)
    @Query("SELECT * from movie_records_table WHERE id = :id")
    suspend fun get(id: Long): MovieRecord?
}

@Database(entities = [MovieRecord::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {

    abstract val dbDao: DbDao

    companion object {
        @Volatile
        private var INSTANCE: Db? = null
        fun getInstance(context: Context): Db {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        Db::class.java,
                        "movie_record_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}