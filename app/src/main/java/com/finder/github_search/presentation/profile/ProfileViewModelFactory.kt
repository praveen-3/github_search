package com.finder.github_search.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.finder.github_search.data.repository.GitHubRepository
import com.finder.github_search.di.NetworkModule

class ProfileViewModelFactory(private val username: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                GitHubRepository(NetworkModule.gitHubApiService),
                username
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
} 