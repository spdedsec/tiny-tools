package com.velvexlabs.tinytools.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @androidx.room.PrimaryKey val toolId: String,
    val position: Int = 0
)

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY position ASC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE toolId = :toolId")
    suspend fun delete(toolId: String)
}

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class TinyToolsDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile private var instance: TinyToolsDatabase? = null

        fun get(context: Context): TinyToolsDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                TinyToolsDatabase::class.java,
                "tiny_tools.db"
            ).build().also { instance = it }
        }
    }
}
