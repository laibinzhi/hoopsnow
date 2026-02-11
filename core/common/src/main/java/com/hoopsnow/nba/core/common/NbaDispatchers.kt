package com.hoopsnow.nba.core.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val nbaDispatcher: NbaDispatchers)

enum class NbaDispatchers {
    Default,
    IO,
}
