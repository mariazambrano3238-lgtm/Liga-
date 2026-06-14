package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Entities
@Entity(tableName = "athletes")
data class Athlete(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val name: String,
    val surname: String,
    val category: String, // "Masculino", "Femenino", "Mixto"
    val age: Int,
    val paymentProofUrl: String,
    val paymentRef: String,
    val club: String,
    val phone: String
)

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val groupName: String, // "Grupo A", "Grupo B", etc.
    val category: String, // Specific category (e.g., "Femenino - Novatas")
    val pg: Int = 0, // Partidos Ganados
    val pp: Int = 0, // Partidos Perdidos
    val pg2: Int = 0, // Ganados en 2 sets (3 pts)
    val pg3: Int = 0, // Ganados en 3 sets (2 pts)
    val pp3: Int = 0, // Perdidos en 3 sets (1 pto)
    val pp2: Int = 0  // Perdidos en 2 sets (0 pts)
) {
    val points: Int get() = (pg2 * 3) + (pg3 * 2) + (pp3 * 1)
    val jj: Int get() = pg + pp
    val jg: Int get() = pg
    val jp: Int get() = pp
    val sg: Int get() = (pg2 * 2) + (pg3 * 2) + (pp3 * 1)
    val sp: Int get() = (pg3 * 1) + (pp3 * 2) + (pp2 * 2)
    val ptos: Int get() = points
}

@Entity(tableName = "categories")
data class TournamentCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gender: String, // "Masculino", "Femenino", "Mixto"
    val name: String
)

@Entity(tableName = "scheduled_matches")
data class ScheduledMatch(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val journey: Int, // 1, 2, 3... (Jornada)
    val category: String, // "Masculino", "Femenino", "Mixto"
    val teamAName: String,
    val teamBName: String,
    val referee: String = "",
    val isPaid: Boolean = false,
    val paymentMethod: String = "Efectivo", // "Pago Móvil", "Efectivo", "Transferencia"
    val totalPaid: Double = 15.0 // referee fee per match
)

@Entity(tableName = "tournament_settings")
data class TournamentSettings(
    @PrimaryKey val id: Int = 1,
    val totalBudget: Double = 1000.0,
    val mascPerc: Float = 40f,
    val femPerc: Float = 35f,
    val mixPerc: Float = 25f,
    val premiacionPerc: Float = 60f,
    val logisticaPerc: Float = 40f,
    val refereeFeePerMatch: Double = 15.0,
    val googleSheetsCsvUrl: String = ""
)

// 2. DAOs
@Dao
interface TournamentDao {
    // Athletes
    @Query("SELECT * FROM athletes ORDER BY timestamp DESC")
    fun getAllAthletes(): Flow<List<Athlete>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: Athlete)

    @Update
    suspend fun updateAthlete(athlete: Athlete)

    @Delete
    suspend fun deleteAthlete(athlete: Athlete)

    @Query("DELETE FROM athletes")
    suspend fun deleteAllAthletes()

    // Teams
    @Query("SELECT * FROM teams ORDER BY category ASC, groupName ASC, pg DESC, pp ASC")
    fun getAllTeamsFlow(): Flow<List<Team>>

    @Query("SELECT * FROM teams")
    suspend fun getAllTeams(): List<Team>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team)

    @Update
    suspend fun updateTeam(team: Team)

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("DELETE FROM teams")
    suspend fun deleteAllTeams()

    // Scheduled Matches
    @Query("SELECT * FROM scheduled_matches ORDER BY journey ASC, category ASC")
    fun getAllMatchesFlow(): Flow<List<ScheduledMatch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: ScheduledMatch)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<ScheduledMatch>)

    @Update
    suspend fun updateMatch(match: ScheduledMatch)

    @Delete
    suspend fun deleteMatch(match: ScheduledMatch)

    @Query("DELETE FROM scheduled_matches WHERE journey = :journey AND category = :category")
    suspend fun deleteMatchesForJourneyAndCategory(journey: Int, category: String)

    @Query("DELETE FROM scheduled_matches")
    suspend fun clearFixture()

    // Settings
    @Query("SELECT * FROM tournament_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<TournamentSettings?>

    @Query("SELECT * FROM tournament_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): TournamentSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: TournamentSettings)

    // Categories
    @Query("SELECT * FROM categories ORDER BY gender ASC, name ASC")
    fun getAllCategoriesFlow(): Flow<List<TournamentCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TournamentCategory)

    @Update
    suspend fun updateCategory(category: TournamentCategory)

    @Delete
    suspend fun deleteCategory(category: TournamentCategory)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}

// 3. Database Abstract Class
@Database(
    entities = [Athlete::class, Team::class, ScheduledMatch::class, TournamentSettings::class, TournamentCategory::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: TournamentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "superliga_volei.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
