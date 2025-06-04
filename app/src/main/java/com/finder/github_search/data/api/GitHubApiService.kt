package com.finder.github_search.data.api

import com.finder.github_search.data.model.GitHubUser
import com.finder.github_search.data.model.Repository
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<SearchResponse>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<GitHubUser>

    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated"
    ): Response<List<Repository>>
}

data class SearchResponse(
    val totalCount: Int,
    val items: List<GitHubUser>
) 