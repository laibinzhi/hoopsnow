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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.ui.component.EmptyScreen
import com.hoopsnow.nba.ui.component.LoadingScreen
import com.hoopsnow.nba.ui.component.PlayerHeadshot
import com.hoopsnow.nba.ui.theme.*
import kotlinx.coroutines.flow.distinctUntilChanged

class PlayerSearchScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<PlayerSearchScreenModel>()
        val searchQuery by screenModel.searchQuery.collectAsState()
        val uiState by screenModel.uiState.collectAsState()
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950)
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            // Search Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navigator.pop() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Slate300, modifier = Modifier.size(24.dp))
                    }

                    Spacer(Modifier.width(8.dp))

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(Slate800, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Search, "Search", tint = Slate500, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { screenModel.updateSearch(it) },
                            modifier = Modifier.weight(1f).focusRequester(focusRequester),
                            textStyle = TextStyle(color = White, fontSize = 16.sp, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(Blue500),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    screenModel.performSearch()
                                },
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (searchQuery.isEmpty()) {
                                        Text("Search players...", color = Slate500, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    }
                                    innerTextField()
                                }
                            },
                        )

                        if (searchQuery.isNotEmpty()) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).clickable { screenModel.clearSearch() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Close, "Clear", tint = Slate400, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Slate800, thickness = 1.dp)

            when (val state = uiState) {
                is PlayerSearchUiState.Idle -> SearchIdleContent()
                is PlayerSearchUiState.Loading -> LoadingScreen()
                is PlayerSearchUiState.Empty -> EmptyScreen(message = "No players found for \"$searchQuery\"")
                is PlayerSearchUiState.Error -> SearchErrorContent(state.message) { screenModel.performSearch() }
                is PlayerSearchUiState.Success -> {
                    SearchResultList(
                        state = state,
                        onPlayerClick = { playerId -> navigator.push(PlayerDetailScreen(playerId)) },
                        onLoadMore = { screenModel.loadMore() },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchIdleContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, null, tint = Slate600, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Search for players", color = Slate500, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text("Enter a name to find players", color = Slate600, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SearchErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Red500, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate800)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("Retry", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SearchResultList(
    state: PlayerSearchUiState.Success,
    onPlayerClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

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
            .collect { shouldLoad -> if (shouldLoad) onLoadMore() }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    ) {
        items(state.players, key = { it.id }) { player ->
            PlayerListItem(player) { onPlayerClick(player.id) }
        }
        item {
            LoadMoreFooter(state.loadMoreState, onLoadMore)
        }
    }
}
