package com.finder.github_search.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.finder.github_search.data.model.GitHubUser
import com.finder.github_search.data.repository.GitHubRepository
import com.finder.github_search.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val repository: GitHubRepository
) : BaseViewModel() {

    private val _searchResults = MutableStateFlow<List<GitHubUser>>(emptyList())
    val searchResults: StateFlow<List<GitHubUser>> = _searchResults.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private var currentQuery = ""
    private var currentPage = 1
    private var searchJob: Job? = null
    private var isNewSearch = true

    fun onSearchQueryChanged(query: String) {
        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce search input
            if (query.isNotBlank()) {
                isNewSearch = true
                currentPage = 1
                performSearch()
            } else {
                _searchResults.value = emptyList()
                _totalCount.value = 0
            }
        }
    }

    fun onSearchSubmitted() {
        searchJob?.cancel()
        isNewSearch = true
        currentPage = 1
        performSearch()
    }

    fun loadMoreResults() {
        if (!loading.value && currentQuery.isNotBlank() && _searchResults.value.size < _totalCount.value) {
            isNewSearch = false
            currentPage++
            performSearch()
        }
    }

    private fun performSearch() {
        searchJob = launchWithLoading {
            repository.searchUsers(currentQuery, currentPage).fold(
                onSuccess = { response ->
                    if (isNewSearch) {
                        _searchResults.value = response.items
                    } else {
                        _searchResults.update { currentList ->
                            currentList + response.items
                        }
                    }
                    _totalCount.value = response.totalCount
                },
                onFailure = { throwable ->
                    _error.value = throwable.message
                }
            )
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchResults.value = emptyList()
        _totalCount.value = 0
        currentPage = 1
        currentQuery = ""
    }
} 