package com.hoopsnow.nba.core.testing

/**
 * Core Testing module containing test utilities and helpers.
 *
 * This module provides common testing utilities, fake implementations,
 * and test doubles that can be used across different test modules.
 *
 * ## Available Components
 *
 * ### Fake Repositories
 * - [com.hoopsnow.nba.core.testing.repository.FakeFavoritesRepository]
 * - [com.hoopsnow.nba.core.testing.repository.FakePlayersRepository]
 * - [com.hoopsnow.nba.core.testing.repository.FakeTeamsRepository]
 * - [com.hoopsnow.nba.core.testing.repository.FakeGamesRepository]
 *
 * ### Test Utilities
 * - [com.hoopsnow.nba.core.testing.util.MainDispatcherRule] - JUnit rule for coroutine testing
 * - [com.hoopsnow.nba.core.testing.util.TestData] - Factory for creating test data
 *
 * ## Usage Example
 *
 * ```kotlin
 * class MyViewModelTest {
 *     @get:Rule
 *     val mainDispatcherRule = MainDispatcherRule()
 *
 *     private val fakeRepository = FakePlayersRepository()
 *
 *     @Test
 *     fun `test player loading`() = runTest {
 *         fakeRepository.setPlayers(TestData.samplePlayers)
 *         // ... test logic
 *     }
 * }
 * ```
 */
object CoreTesting
