package com.hoopsnow.nba.feature.players.impl

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoopsnow.nba.core.designsystem.component.EmptyScreen
import com.hoopsnow.nba.core.designsystem.component.LoadingScreen
import com.hoopsnow.nba.core.designsystem.component.PlayerHeadshot
import com.hoopsnow.nba.core.designsystem.theme.*
import com.hoopsnow.nba.core.model.Player
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PlayerSearchScreen(
    onBack: () -> Unit,
    onPlayerClick: (Int) -> Unit,
    viewModel: PlayerSearchViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Auto focus on search field when screen opens
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
                // Back button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Slate300,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Search input field
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(Slate800, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Slate500,
                        modifier = Modifier.size(20.dp),
                    )

                    Spacer(Modifier.width(8.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearch(it) },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        cursorBrush = SolidColor(Blue500),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                viewModel.performSearch()
                            },
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search players...",
                                        color = Slate500,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )

                    // Clear button
                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable { viewModel.clearSearch() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Slate400,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Slate800, thickness = 1.dp)

        // Content
        when (val state = uiState) {
            is PlayerSearchUiState.Idle -> {
                SearchIdleContent()
            }
            is PlayerSearchUiState.Loading -> {
                LoadingScreen()
            }
            is PlayerSearchUiState.Empty -> {
                EmptyScreen(message = "No players found for \"$searchQuery\"")
            }
            is PlayerSearchUiState.Error -> {
                SearchErrorContent(message = state.message, onRetry = { viewModel.performSearch() })
            }
            is PlayerSearchUiState.Success -> {
                SearchResultList(
                    state = state,
                    onPlayerClick = onPlayerClick,
                    onLoadMore = { viewModel.loadMore() },
                )
            }
        }
    }
}

@Composable
private fun SearchIdleContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Slate600,
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Search for players",
                color = Slate500,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Enter a name to find players",
                color = Slate600,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun SearchErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                color = Red500,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate800)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Retry",
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
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
            .collect { shouldLoad ->
                if (shouldLoad) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    ) {
        items(state.players, key = { it.id }) { player ->
            SearchPlayerListItem(player = player, onClick = { onPlayerClick(player.id) })
        }

        item {
            SearchLoadMoreFooter(
                loadMoreState = state.loadMoreState,
                onRetry = onLoadMore,
            )
        }
    }
}

@Composable
private fun SearchPlayerListItem(player: Player, onClick: () -> Unit) {
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
        PlayerHeadshot(
            firstName = player.firstName,
            lastName = player.lastName,
            size = 48.dp,
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${player.firstName} ${player.lastName}",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
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
                    Text(
                        text = team.abbreviation,
                        color = Slate400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Go", tint = Slate600, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SearchLoadMoreFooter(
    loadMoreState: LoadMoreState,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (loadMoreState) {
            is LoadMoreState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(
                        color = Blue500,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                    Text(
                        text = "Loading...",
                        color = Slate400,
                        fontSize = 14.sp,
                    )
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
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Red500,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Load failed, tap to retry",
                        color = Red500,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
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
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Yellow400,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Too many requests, tap to retry",
                        color = Yellow400,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            is LoadMoreState.NoMore -> {
                Text(
                    text = "— You've reached the end —",
                    color = Slate500,
                    fontSize = 13.sp,
                )
            }
            is LoadMoreState.Idle -> {
                // Show nothing when idle
            }
        }
    }
}
