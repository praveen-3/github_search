package com.finder.github_search.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finder.github_search.data.model.GitHubUser
import com.finder.github_search.data.model.Repository
import com.finder.github_search.data.repository.GitHubRepository
import com.finder.github_search.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: GitHubRepository,
    private val username: String
) : BaseViewModel() {

    private val _user = MutableStateFlow<GitHubUser?>(null)
    val user: StateFlow<GitHubUser?> = _user.asStateFlow()

    private val _repositories = MutableStateFlow<List<Repository>>(emptyList())
    val repositories: StateFlow<List<Repository>> = _repositories.asStateFlow()

    private var currentPage = 1
    private var hasMoreRepositories = true

    init {
        loadUserProfile()
        loadRepositories()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                repository.getUser(username).fold(
                    onSuccess = { user ->
                        _user.value = user
                    },
                    onFailure = { throwable ->
                        _error.value = throwable.message ?: "Error loading user profile"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading user profile"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadRepositories() {
        if (!hasMoreRepositories || _loading.value == true) return

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                repository.getUserRepositories(username, currentPage).fold(
                    onSuccess = { newRepositories ->
                        if (newRepositories.isEmpty()) {
                            hasMoreRepositories = false
                        } else {
                            _repositories.update { currentList ->
                                currentList + newRepositories
                            }
                            currentPage++
                        }
                    },
                    onFailure = { throwable ->
                        _error.value = throwable.message ?: "Error loading repositories"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading repositories"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadMoreRepositories() {
        loadRepositories()
    }

    fun retry() {
        if (_user.value == null) {
            loadUserProfile()
        }
        if (_repositories.value.isEmpty()) {
            currentPage = 1
            hasMoreRepositories = true
            loadRepositories()
        }
    }
} 