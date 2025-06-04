package com.finder.github_search.data.model

import com.google.gson.annotations.SerializedName

data class Repository(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    @SerializedName("forks_count")
    val forksCount: Int,
    @SerializedName("language")
    val language: String?,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class RepositoryOwner(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
) 