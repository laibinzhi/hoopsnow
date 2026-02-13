package com.hoopsnow.nba.ui.players

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.ui.component.EmptyScreen
import com.hoopsnow.nba.ui.component.ErrorScreen
import com.hoopsnow.nba.ui.component.LoadingScreen
import com.hoopsnow.nba.ui.component.PlayerHeadshot
import com.hoopsnow.nba.ui.theme.*
import kotlinx.coroutines.flow.distinctUntilChanged

class PlayersListScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<PlayersListScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950),
        ) {
            // Header + Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            ) {
                Text(
                    text = "Players",
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                // Search bar (clickable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(Slate800, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navigator.push(PlayerSearchScreen()) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Search, "Search", tint = Slate500, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Search players...", color = Slate500, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            HorizontalDivider(color = Slate800, thickness = 1.dp)

            when (val state = uiState) {
                is PlayersUiState.Loading -> LoadingScreen()
                is PlayersUiState.Empty -> EmptyScreen(message = "No players found")
                is PlayersUiState.Error -> ErrorScreen(message = state.message)
                is PlayersUiState.Success -> {
                    val listState = rememberLazyListState()
                    val pullToRefreshState = rememberPullToRefreshState()

                    val shouldLoadMore = remember(listState, state) {
                        derivedStateOf {
                            val layoutInfo = listState.layoutInfo
                            val totalItems = layoutInfo.totalItemsCount
                            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            lastVisibleItemIndex >= totalItems - 5 &&
                                totalItems > 0 &&
                                state.hasMore &&
                                !state.isLoadingMore &&
                                !state.canRetryLoadMore
                        }
                    }

                    LaunchedEffect(listState, state.hasMore, state.isLoadingMore) {
                        snapshotFlow { shouldLoadMore.value }
                            .distinctUntilChanged()
                            .collect { shouldLoad ->
                                if (shouldLoad) screenModel.loadMore()
                            }
                    }

                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { screenModel.refresh() },
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        ) {
                            items(state.players, key = { it.id }) { player ->
                                PlayerListItem(player) { navigator.push(PlayerDetailScreen(player.id)) }
                            }
                            item {
                                LoadMoreFooter(state.loadMoreState) { screenModel.loadMore() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PlayerListItem(player: Player, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.99f else 1f, label = "scale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                    onTap = { onClick() },
                )
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerHeadshot(firstName = player.firstName, lastName = player.lastName, size = 48.dp)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("${player.firstName} ${player.lastName}", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 2.dp),
            ) {
                if (player.position.isNotBlank()) {
                    Text(
                        text = player.position,
                        color = Slate300,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Slate800, RoundedCornerShape(4.dp))
                            .border(1.dp, Slate700, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                player.team?.let { team ->
                    Text(team.abbreviation, color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Go", tint = Slate600, modifier = Modifier.size(20.dp))
    }
}

@Composable
internal fun LoadMoreFooter(loadMoreState: LoadMoreState, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (loadMoreState) {
            is LoadMoreState.Loading -> {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(color = Blue500, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text("Loading...", color = Slate400, fontSize = 14.sp)
                }
            }
            is LoadMoreState.Error -> {
                Row(
                    modifier = Modifier
                        .background(Slate900, RoundedCornerShape(8.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                        .clickable(onClick = onRetry)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.Refresh, "Retry", tint = Red500, modifier = Modifier.size(18.dp))
                    Text("Load failed, tap to retry", color = Red500, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            is LoadMoreState.RateLimit -> {
                Row(
                    modifier = Modifier
                        .background(Slate900, RoundedCornerShape(8.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                        .clickable(onClick = onRetry)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Default.Refresh, "Retry", tint = Yellow400, modifier = Modifier.size(18.dp))
                    Text("Too many requests, tap to retry", color = Yellow400, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            is LoadMoreState.NoMore -> {
                Text("— You've reached the end —", color = Slate500, fontSize = 13.sp)
            }
            is LoadMoreState.Idle -> {}
        }
    }
}
