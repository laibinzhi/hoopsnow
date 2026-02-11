package com.hoopsnow.nba.core.testing.util

import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

/**
 * Test data factory for creating test objects.
 */
object TestData {

    fun createTeam(
        id: Int = 1,
        abbreviation: String = "LAL",
        city: String = "Los Angeles",
        conference: String = "West",
        division: String = "Pacific",
        fullName: String = "Los Angeles Lakers",
        name: String = "Lakers",
    ) = Team(
        id = id,
        abbreviation = abbreviation,
        city = city,
        conference = conference,
        division = division,
        fullName = fullName,
        name = name,
    )

    fun createPlayer(
        id: Int = 1,
        firstName: String = "LeBron",
        lastName: String = "James",
        position: String = "F",
        team: Team? = createTeam(),
    ) = Player(
        id = id,
        firstName = firstName,
        lastName = lastName,
        position = position,
        team = team,
    )

    fun createGame(
        id: Int = 1,
        date: String = "2024-01-15",
        season: Int = 2024,
        homeTeamScore: Int = 110,
        visitorTeamScore: Int = 105,
        homeTeam: Team = createTeam(id = 1, abbreviation = "LAL", fullName = "Los Angeles Lakers"),
        visitorTeam: Team = createTeam(id = 2, abbreviation = "GSW", fullName = "Golden State Warriors"),
        status: String = "Final",
    ) = Game(
        id = id,
        date = date,
        season = season,
        homeTeamScore = homeTeamScore,
        visitorTeamScore = visitorTeamScore,
        homeTeam = homeTeam,
        visitorTeam = visitorTeam,
        status = status,
    )

    val sampleTeams = listOf(
        createTeam(id = 1, abbreviation = "LAL", fullName = "Los Angeles Lakers", conference = "West"),
        createTeam(id = 2, abbreviation = "GSW", fullName = "Golden State Warriors", conference = "West"),
        createTeam(id = 3, abbreviation = "BOS", fullName = "Boston Celtics", conference = "East"),
        createTeam(id = 4, abbreviation = "MIA", fullName = "Miami Heat", conference = "East"),
    )

    val samplePlayers = listOf(
        createPlayer(id = 1, firstName = "LeBron", lastName = "James", team = sampleTeams[0]),
        createPlayer(id = 2, firstName = "Stephen", lastName = "Curry", team = sampleTeams[1]),
        createPlayer(id = 3, firstName = "Jayson", lastName = "Tatum", team = sampleTeams[2]),
        createPlayer(id = 4, firstName = "Jimmy", lastName = "Butler", team = sampleTeams[3]),
    )

    val sampleGames = listOf(
        createGame(id = 1, homeTeam = sampleTeams[0], visitorTeam = sampleTeams[1]),
        createGame(id = 2, homeTeam = sampleTeams[2], visitorTeam = sampleTeams[3]),
    )
}
