package com.finder.github_search.data.repository

import com.finder.github_search.data.api.GitHubApiService
import com.finder.github_search.data.api.SearchResponse
import com.finder.github_search.data.model.GitHubUser
import com.finder.github_search.data.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GitHubRepository(private val apiService: GitHubApiService) {
    
    suspend fun searchUsers(query: String, page: Int = 1): Result<SearchResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchUsers(query, page)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(username: String): Result<GitHubUser> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUser(username)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRepositories(
        username: String,
        page: Int = 1
    ): Result<List<Repository>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserRepositories(username, page)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 