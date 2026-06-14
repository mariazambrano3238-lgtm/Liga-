package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class TournamentViewModel(private val repository: TournamentRepository) : ViewModel() {

    // Registrations
    val allAthletes: StateFlow<List<Athlete>> = repository.allAthletes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Teams
    val allTeams: StateFlow<List<Team>> = repository.allTeams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Matches
    val allMatches: StateFlow<List<ScheduledMatch>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings
    val settings: StateFlow<TournamentSettings> = repository.settingsFlow
        .map { it ?: TournamentSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TournamentSettings())

    // Categories
    val allCategories: StateFlow<List<TournamentCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulate()
        }
    }

    // Custom Category management actions
    fun addCategory(gender: String, name: String) {
        viewModelScope.launch {
            repository.dao.insertCategory(TournamentCategory(gender = gender, name = name))
        }
    }

    fun updateCategory(category: TournamentCategory) {
        viewModelScope.launch {
            repository.dao.updateCategory(category)
        }
    }

    fun deleteCategory(category: TournamentCategory) {
        viewModelScope.launch {
            repository.dao.deleteCategory(category)
        }
    }

    // Interactive Actions:
    fun addAthlete(name: String, surname: String, category: String, age: Int, payProof: String, payRef: String, club: String, phone: String) {
        viewModelScope.launch {
            repository.dao.insertAthlete(
                Athlete(
                    name = name,
                    surname = surname,
                    category = category,
                    age = age,
                    paymentProofUrl = payProof,
                    paymentRef = payRef,
                    club = club,
                    phone = phone
                )
            )
        }
    }

    fun deleteAthlete(athlete: Athlete) {
        viewModelScope.launch {
            repository.dao.deleteAthlete(athlete)
        }
    }

    fun updateAthlete(athlete: Athlete) {
        viewModelScope.launch {
            repository.dao.updateAthlete(athlete)
        }
    }

    fun resetAthletes() {
        viewModelScope.launch {
            repository.dao.deleteAllAthletes()
        }
    }

    // Teams Admin:
    fun addTeam(name: String, group: String, category: String) {
        viewModelScope.launch {
            repository.dao.insertTeam(
                Team(
                    name = name,
                    groupName = group,
                    category = category,
                    pg = 0,
                    pp = 0
                )
            )
        }
    }

    fun updateTeamStats(team: Team, pg: Int, pp: Int) {
        viewModelScope.launch {
            val targetPg = pg.coerceAtLeast(0)
            val targetPp = pp.coerceAtLeast(0)
            
            // Adjust pg2 and pg3 so their sum equals targetPg
            var newPg2 = team.pg2
            var newPg3 = team.pg3
            val currentSumPg = newPg2 + newPg3
            if (targetPg > currentSumPg) {
                newPg2 += (targetPg - currentSumPg)
            } else if (targetPg < currentSumPg) {
                var diff = currentSumPg - targetPg
                val subFromPg2 = minOf(newPg2, diff)
                newPg2 -= subFromPg2
                diff -= subFromPg2
                if (diff > 0) {
                    newPg3 = (newPg3 - diff).coerceAtLeast(0)
                }
            }

            // Adjust pp3 and pp2 so their sum equals targetPp
            var newPp3 = team.pp3
            var newPp2 = team.pp2
            val currentSumPp = newPp3 + newPp2
            if (targetPp > currentSumPp) {
                newPp2 += (targetPp - currentSumPp)
            } else if (targetPp < currentSumPp) {
                var diff = currentSumPp - targetPp
                val subFromPp2 = minOf(newPp2, diff)
                newPp2 -= subFromPp2
                diff -= subFromPp2
                if (diff > 0) {
                    newPp3 = (newPp3 - diff).coerceAtLeast(0)
                }
            }

            repository.dao.updateTeam(
                team.copy(
                    pg = targetPg,
                    pp = targetPp,
                    pg2 = newPg2,
                    pg3 = newPg3,
                    pp3 = newPp3,
                    pp2 = newPp2
                )
            )
        }
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch {
            repository.dao.updateTeam(team)
        }
    }

    fun adjustTeamDetailedStats(team: Team, pg2Delta: Int, pg3Delta: Int, pp3Delta: Int, pp2Delta: Int) {
        viewModelScope.launch {
            val newPg2 = (team.pg2 + pg2Delta).coerceAtLeast(0)
            val newPg3 = (team.pg3 + pg3Delta).coerceAtLeast(0)
            val newPp3 = (team.pp3 + pp3Delta).coerceAtLeast(0)
            val newPp2 = (team.pp2 + pp2Delta).coerceAtLeast(0)
            repository.dao.updateTeam(
                team.copy(
                    pg2 = newPg2,
                    pg3 = newPg3,
                    pp3 = newPp3,
                    pp2 = newPp2,
                    pg = newPg2 + newPg3,
                    pp = newPp3 + newPp2
                )
            )
        }
    }

    fun deleteTeam(team: Team) {
        viewModelScope.launch {
            repository.dao.deleteTeam(team)
        }
    }

    // Fixture actions:
    fun triggerGenerateFixture() {
        viewModelScope.launch {
            repository.generateFixture()
        }
    }

    fun updateMatch(match: ScheduledMatch) {
         viewModelScope.launch {
              repository.dao.updateMatch(match)
         }
    }

    fun deleteMatch(match: ScheduledMatch) {
         viewModelScope.launch {
              repository.dao.deleteMatch(match)
         }
    }

    fun addManualMatch(journey: Int, category: String, teamA: String, teamB: String, referee: String = "", totalPaid: Double = 15.0) {
         viewModelScope.launch {
              repository.dao.insertMatch(
                   ScheduledMatch(
                        journey = journey,
                        category = category,
                        teamAName = teamA,
                        teamBName = teamB,
                        referee = referee,
                        totalPaid = totalPaid
                   )
              )
         }
    }

    fun generateCustomMatchesForCategoryAndJourney(
         journey: Int,
         category: String,
         matchCount: Int,
         activeGroups: List<String>,
         onResult: (Boolean, String) -> Unit
    ) {
         viewModelScope.launch {
              try {
                   val allTeams = repository.dao.getAllTeams()
                   val candidateTeams = allTeams.filter {
                        it.category == category && activeGroups.contains(it.groupName)
                   }

                   if (candidateTeams.size < 2) {
                        onResult(false, "Necesitas al menos 2 equipos registrados en la categoría $category dentro de los grupos seleccionados.")
                        return@launch
                   }

                   // Delete existing matches for this category in this journey
                   repository.dao.deleteMatchesForJourneyAndCategory(journey, category)

                   val possiblePairings = mutableListOf<Pair<Team, Team>>()
                   for (i in candidateTeams.indices) {
                        for (j in i + 1 until candidateTeams.size) {
                             possiblePairings.add(Pair(candidateTeams[i], candidateTeams[j]))
                        }
                   }
                   possiblePairings.shuffle()

                   val generated = mutableListOf<ScheduledMatch>()
                   var attempt = 0
                   val referees = listOf("Prof. Luis Alarcón", "Ing. Carmen Suárez", "Lcdo. José Torres", "Dra. Brenda Gil")
                   val settings = repository.dao.getSettingsDirect() ?: TournamentSettings()

                   while (generated.size < matchCount && attempt < 100) {
                        for (pairing in possiblePairings) {
                             if (generated.size >= matchCount) break
                             generated.add(
                                  ScheduledMatch(
                                       journey = journey,
                                       category = category,
                                       teamAName = pairing.first.name,
                                       teamBName = pairing.second.name,
                                       referee = referees.random(),
                                       totalPaid = settings.refereeFeePerMatch
                                  )
                             )
                        }
                        attempt++
                   }

                   repository.dao.insertMatches(generated)
                   onResult(true, "¡Éxito! Se programaron $matchCount juegos para la categoría $category en la Jornada $journey.")
              } catch (e: Exception) {
                   onResult(false, "Error: ${e.localizedMessage ?: e.message}")
              }
         }
    }

    fun updateMatchArbitration(match: ScheduledMatch, referee: String, isPaid: Boolean, method: String, totalPaid: Double) {
        viewModelScope.launch {
            repository.dao.updateMatch(
                match.copy(
                    referee = referee,
                    isPaid = isPaid,
                    paymentMethod = method,
                    totalPaid = totalPaid
                )
            )
        }
    }

    fun saveSettings(totalBudget: Double, masc: Float, fem: Float, mix: Float, prem: Float, log: Float, refFee: Double) {
        viewModelScope.launch {
            val current = repository.dao.getSettingsDirect() ?: TournamentSettings()
            repository.dao.saveSettings(
                TournamentSettings(
                    id = 1,
                    totalBudget = totalBudget,
                    mascPerc = masc,
                    femPerc = fem,
                    mixPerc = mix,
                    premiacionPerc = prem,
                    logisticaPerc = log,
                    refereeFeePerMatch = refFee,
                    googleSheetsCsvUrl = current.googleSheetsCsvUrl
                )
            )
        }
    }

    fun saveCsvUrl(url: String) {
        viewModelScope.launch {
            val current = repository.dao.getSettingsDirect() ?: TournamentSettings()
            repository.dao.saveSettings(current.copy(googleSheetsCsvUrl = url))
        }
    }

    fun syncFromSheets(csvUrl: String, onResult: (Int, String?) -> Unit) {
        viewModelScope.launch {
            val syncRes = withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(csvUrl).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            return@withContext SyncResult(0, "Error del servidor HTTP ${response.code}")
                        }
                        val bodyString = response.body?.string() ?: ""
                        if (bodyString.isBlank()) {
                            return@withContext SyncResult(0, "La hoja de cálculo publicada está vacía")
                        }
                        
                        val lines = bodyString.lines().map { it.replace("\r", "") }
                        if (lines.size <= 1) {
                            return@withContext SyncResult(0, "Solo se encontró la fila de cabecera. Llena el formulario primero")
                        }
                        
                        var count = 0
                        // Parse each data row
                        for (i in 1 until lines.size) {
                            val line = lines[i]
                            if (line.trim().isEmpty()) continue
                            val cols = splitCsvLine(line)
                            
                            // At least has: Timestamp, Nombre, Apellido, Categoría
                            if (cols.size >= 4) {
                                val name = cols.getOrNull(1) ?: ""
                                val surname = cols.getOrNull(2) ?: ""
                                val categoryRaw = cols.getOrNull(3) ?: ""
                                val ageRaw = cols.getOrNull(4) ?: "20"
                                val proof = cols.getOrNull(5) ?: "Sincronizado"
                                val ref = cols.getOrNull(6) ?: "REF-SHEETS"
                                val club = cols.getOrNull(7) ?: "Independiente"
                                val phone = cols.getOrNull(8) ?: ""
                                
                                val category = when {
                                    categoryRaw.contains("Fem", ignoreCase = true) -> "Femenino"
                                    categoryRaw.contains("Mix", ignoreCase = true) -> "Mixto"
                                    else -> "Masculino"
                                }
                                
                                if (name.trim().isNotEmpty()) {
                                    repository.dao.insertAthlete(
                                        Athlete(
                                            name = name.trim(),
                                            surname = surname.trim(),
                                            category = category,
                                            age = ageRaw.trim().toIntOrNull() ?: 22,
                                            paymentProofUrl = proof.trim().ifEmpty { "https://drive.google.com" },
                                            paymentRef = ref.trim().ifEmpty { "REF-AUTO" },
                                            club = club.trim().ifEmpty { "Independiente" },
                                            phone = phone.trim().ifEmpty { "+584120000000" }
                                        )
                                    )
                                    count++
                                }
                            }
                        }
                        SyncResult(count, null)
                    }
                } catch (e: Exception) {
                    SyncResult(0, "Error de red o formato: ${e.message ?: e.localizedMessage}")
                }
            }
            onResult(syncRes.count, syncRes.error)
        }
    }

    private fun splitCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        for (char in line) {
            if (char == '\"') {
                inQuotes = !inQuotes
            } else if (char == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.setLength(0)
            } else {
                current.append(char)
            }
        }
        result.add(current.toString().trim())
        return result
    }

    private data class SyncResult(val count: Int, val error: String?)
}

class TournamentViewModelFactory(private val repository: TournamentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TournamentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TournamentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
