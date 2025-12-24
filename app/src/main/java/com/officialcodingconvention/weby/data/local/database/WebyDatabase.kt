package com.officialcodingconvention.weby.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.officialcodingconvention.weby.data.local.database.converter.Converters
import com.officialcodingconvention.weby.data.local.database.dao.ProjectDao
import com.officialcodingconvention.weby.data.local.database.dao.TemplateDao
import com.officialcodingconvention.weby.data.local.database.dao.ComponentDao
import com.officialcodingconvention.weby.data.local.database.entity.ProjectEntity
import com.officialcodingconvention.weby.data.local.database.entity.PageEntity
import com.officialcodingconvention.weby.data.local.database.entity.AssetEntity
import com.officialcodingconvention.weby.data.local.database.entity.TemplateEntity
import com.officialcodingconvention.weby.data.local.database.entity.SavedComponentEntity
import com.officialcodingconvention.weby.data.local.database.entity.CssClassEntity
import com.officialcodingconvention.weby.data.local.database.entity.VersionSnapshotEntity

@Database(
    entities = [
        ProjectEntity::class,
        PageEntity::class,
        AssetEntity::class,
        TemplateEntity::class,
        SavedComponentEntity::class,
        CssClassEntity::class,
        VersionSnapshotEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class WebyDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun templateDao(): TemplateDao
    abstract fun componentDao(): ComponentDao

    companion object {
        private const val DATABASE_NAME = "weby_database"

        @Volatile
        private var instance: WebyDatabase? = null

        fun getInstance(context: Context): WebyDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WebyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                WebyDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
