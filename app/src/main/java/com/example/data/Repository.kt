package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class TournamentRepository(private val db: AppDatabase) {
    val dao = db.dao

    val allAthletes: Flow<List<Athlete>> = dao.getAllAthletes()
    val allTeams: Flow<List<Team>> = dao.getAllTeamsFlow()
    val allMatches: Flow<List<ScheduledMatch>> = dao.getAllMatchesFlow()
    val allCategories: Flow<List<TournamentCategory>> = dao.getAllCategoriesFlow()
    val settingsFlow: Flow<TournamentSettings?> = dao.getSettingsFlow()

    // Default pre-population check
    suspend fun checkAndPrepopulate() {
        val existingTeams = dao.getAllTeams()
        if (existingTeams.isEmpty()) {
            populateDefaults()
        }
    }

    private suspend fun populateDefaults() {
        // Mock Settings
        dao.saveSettings(
            TournamentSettings(
                id = 1,
                totalBudget = 1200.0,
                mascPerc = 40f,
                femPerc = 35f,
                mixPerc = 25f,
                premiacionPerc = 60f,
                logisticaPerc = 40f,
                refereeFeePerMatch = 15.0
            )
        )

        // Mock Categories according to request
        val defaultCategories = listOf(
            // Femenino
            TournamentCategory(gender = "Femenino", name = "Desarrollo 3x3"),
            TournamentCategory(gender = "Femenino", name = "Novatas"),
            TournamentCategory(gender = "Femenino", name = "Ascenso"),
            TournamentCategory(gender = "Femenino", name = "Relibre"),
            TournamentCategory(gender = "Femenino", name = "Amateur (Master) 3x3"),
            TournamentCategory(gender = "Femenino", name = "Federativa U11"),
            TournamentCategory(gender = "Femenino", name = "Federativa U13"),
            TournamentCategory(gender = "Femenino", name = "Federativa U15"),

            // Masculino
            TournamentCategory(gender = "Masculino", name = "Desarrollo 3x3 (Edad máxima 14 años)"),
            TournamentCategory(gender = "Masculino", name = "Novatos"),
            TournamentCategory(gender = "Masculino", name = "Ascenso"),
            TournamentCategory(gender = "Masculino", name = "Regular"),
            TournamentCategory(gender = "Masculino", name = "Relibre"),
            TournamentCategory(gender = "Masculino", name = "Master 3x3"),
            TournamentCategory(gender = "Masculino", name = "Federativa U11"),
            TournamentCategory(gender = "Masculino", name = "Federativa U13"),
            TournamentCategory(gender = "Masculino", name = "Federativa U15"),

            // Mixto
            TournamentCategory(gender = "Mixto", name = "Mixto Única")
        )
        for (cat in defaultCategories) {
            dao.insertCategory(cat)
        }

        // Mock Registrations
        val now = System.currentTimeMillis()
        val mockAthletes = listOf(
            Athlete(name = "Carlos", surname = "Ríos", category = "Masculino - Novatos", age = 24, paymentProofUrl = "https://drive.google.com/file/d/proof_carlos", paymentRef = "REF10023", club = "Arena Club", phone = "+584125550101"),
            Athlete(name = "Andrés", surname = "Mendoza", category = "Masculino - Novatos", age = 27, paymentProofUrl = "https://drive.google.com/file/d/proof_andres", paymentRef = "REF10024", club = "Arena Club", phone = "+584125550102"),
            Athlete(name = "Sofía", surname = "Silva", category = "Femenino - Novatas", age = 22, paymentProofUrl = "https://drive.google.com/file/d/proof_sofia", paymentRef = "REF10025", club = "Voley Pro", phone = "+584125550103"),
            Athlete(name = "María", surname = "Camis", category = "Femenino - Novatas", age = 26, paymentProofUrl = "https://drive.google.com/file/d/proof_maria", paymentRef = "REF10026", club = "Voley Pro", phone = "+584125550104"),
            Athlete(name = "Gabriel", surname = "Ochoa", category = "Mixto - Mixto Única", age = 29, paymentProofUrl = "https://drive.google.com/file/d/proof_gabriel", paymentRef = "REF10027", club = "Playa Libre", phone = "+584125550105"),
            Athlete(name = "Daniela", surname = "Rojas", category = "Mixto - Mixto Única", age = 25, paymentProofUrl = "https://drive.google.com/file/d/proof_daniela", paymentRef = "REF10028", club = "Playa Libre", phone = "+584125550106"),
            Athlete(name = "Julio", surname = "Castillo", category = "Masculino - Novatos", age = 31, paymentProofUrl = "https://drive.google.com/file/d/proof_julio", paymentRef = "REF10029", club = "Titanes", phone = "+584125550107"),
            Athlete(name = "Paula", surname = "Guzmán", category = "Femenino - Novatas", age = 21, paymentProofUrl = "https://drive.google.com/file/d/proof_paula", paymentRef = "REF10030", club = "Titanes", phone = "+584125550108")
        )

        for (ath in mockAthletes) {
            dao.insertAthlete(ath)
        }

        // Mock Duplas (Definitive Teams)
        val mockTeams = listOf(
            // Masculino - Grupo A
            Team(name = "Ortega / Pérez", groupName = "Grupo A", category = "Masculino - Novatos", pg = 3, pp = 0, pg2 = 2, pg3 = 1, pp3 = 0, pp2 = 0),
            Team(name = "Gómez / Ramos", groupName = "Grupo A", category = "Masculino - Novatos", pg = 2, pp = 1, pg2 = 1, pg3 = 1, pp3 = 1, pp2 = 0),
            Team(name = "Rodríguez / Díaz", groupName = "Grupo A", category = "Masculino - Novatos", pg = 1, pp = 2, pg2 = 1, pg3 = 0, pp3 = 1, pp2 = 1),
            Team(name = "Torres / Castro", groupName = "Grupo A", category = "Masculino - Novatos", pg = 0, pp = 3, pg2 = 0, pg3 = 0, pp3 = 0, pp2 = 3),

            // Masculino - Grupo B
            Team(name = "Sánchez / Flores", groupName = "Grupo B", category = "Masculino - Novatos", pg = 2, pp = 1, pg2 = 1, pg3 = 1, pp3 = 1, pp2 = 0),
            Team(name = "Mendoza / Ruiz", groupName = "Grupo B", category = "Masculino - Novatos", pg = 3, pp = 0, pg2 = 3, pg3 = 0, pp3 = 0, pp2 = 0),
            Team(name = "Álvarez / Gutiérrez", groupName = "Grupo B", category = "Masculino - Novatos", pg = 1, pp = 2, pg2 = 0, pg3 = 1, pp3 = 0, pp2 = 2),
            Team(name = "Vargas / Medina", groupName = "Grupo B", category = "Masculino - Novatos", pg = 0, pp = 3, pg2 = 0, pg3 = 0, pp3 = 1, pp2 = 2),

            // Femenino - Grupo A
            Team(name = "Silva / Santos", groupName = "Grupo A", category = "Femenino - Novatas", pg = 2, pp = 0, pg2 = 2, pg3 = 0, pp3 = 0, pp2 = 0),
            Team(name = "Rojas / Camis", groupName = "Grupo A", category = "Femenino - Novatas", pg = 1, pp = 1, pg2 = 0, pg3 = 1, pp3 = 0, pp2 = 1),
            Team(name = "Guerra / Herrera", groupName = "Grupo A", category = "Femenino - Novatas", pg = 0, pp = 2, pg2 = 0, pg3 = 0, pp3 = 1, pp2 = 1),

            // Femenino - Grupo B
            Team(name = "Martínez / Blanco", groupName = "Grupo B", category = "Femenino - Novatas", pg = 2, pp = 0, pg2 = 1, pg3 = 1, pp3 = 0, pp2 = 0),
            Team(name = "Noguera / Bello", groupName = "Grupo B", category = "Femenino - Novatas", pg = 1, pp = 1, pg2 = 1, pg3 = 0, pp3 = 1, pp2 = 0),
            Team(name = "Soto / Prieto", groupName = "Grupo B", category = "Femenino - Novatas", pg = 0, pp = 2, pg2 = 0, pg3 = 0, pp3 = 0, pp2 = 2),

            // Mixto - Grupo Único
            Team(name = "López / Castillo", groupName = "Grupo Único", category = "Mixto - Mixto Única", pg = 1, pp = 1, pg2 = 1, pg3 = 0, pp3 = 1, pp2 = 0),
            Team(name = "Marín / Acosta", groupName = "Grupo Único", category = "Mixto - Mixto Única", pg = 2, pp = 0, pg2 = 1, pg3 = 1, pp3 = 0, pp2 = 0),
            Team(name = "Uzcátegui / Reyes", groupName = "Grupo Único", category = "Mixto - Mixto Única", pg = 0, pp = 2, pg2 = 0, pg3 = 0, pp3 = 0, pp2 = 2)
        )

        for (team in mockTeams) {
            dao.insertTeam(team)
        }

        // Generate default matches (Journey 1 & 2 mock) so fixture starts prepopulated
        generateFixtureInternal(mockTeams, 15.0)
    }

    // Fixture Generator Logic
    suspend fun generateFixture() {
        val teams = dao.getAllTeams()
        val settings = dao.getSettingsDirect() ?: TournamentSettings()
        generateFixtureInternal(teams, settings.refereeFeePerMatch)
    }

    private suspend fun generateFixtureInternal(teams: List<Team>, fee: Double) {
        dao.clearFixture()
        val categories = listOf("Masculino", "Femenino", "Mixto")
        val allGeneratedMatches = mutableListOf<ScheduledMatch>()

        // For each category and group, generate Round-Robin
        for (cat in categories) {
            val catTeams = teams.filter { it.category == cat }
            // Group by groupName
            val groups = catTeams.map { it.groupName }.distinct()
            for (group in groups) {
                val groupTeams = catTeams.filter { it.groupName == group }
                if (groupTeams.size < 2) continue

                val roundRobinMatches = makeRoundRobin(groupTeams, fee)
                allGeneratedMatches.addAll(roundRobinMatches)
            }
        }

        // Insert mock or assigned referee randomly from a pool to make it look realistic!
        val referees = listOf("Prof. Luis Alarcón", "Ing. Carmen Suárez", "Lcdo. José Torres", "Dra. Brenda Gil")
        val populatedMatches = allGeneratedMatches.map { match ->
            match.copy(referee = referees[Random.nextInt(referees.size)])
        }

        dao.insertMatches(populatedMatches)
    }

    // Classic Round-Robin algorithm
    private fun makeRoundRobin(list: List<Team>, fee: Double): List<ScheduledMatch> {
        val result = mutableListOf<ScheduledMatch>()
        val listMutable = list.toMutableList()

        if (listMutable.size % 2 != 0) {
            // Null or bye stand-in team
            // Let's call it BYE. We won't include match-ups against BYE
            listMutable.add(Team(name = "BYE", groupName = "", category = ""))
        }

        val n = listMutable.size
        val rounds = n - 1

        for (round in 0 until rounds) {
            for (i in 0 until n / 2) {
                val home = listMutable[i]
                val away = listMutable[n - 1 - i]

                if (home.name != "BYE" && away.name != "BYE") {
                    result.add(
                        ScheduledMatch(
                            journey = round + 1,
                            category = home.category,
                            teamAName = home.name,
                            teamBName = away.name,
                            totalPaid = fee
                        )
                    )
                }
            }

            // Rotate list elements (except the first one)
            val first = listMutable[0]
            val last = listMutable.removeAt(listMutable.size - 1)
            listMutable.add(1, last)
        }

        return result
    }
}
