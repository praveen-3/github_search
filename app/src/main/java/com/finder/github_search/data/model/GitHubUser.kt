package com.finder.github_search.data.model

import com.google.gson.annotations.SerializedName

data class GitHubUser(
    val id: Int,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val bio: String?,
    @SerializedName("public_repos")
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    @SerializedName("html_url")
    val profileUrl: String,
    val name: String?,
    val company: String?,
    val location: String?,
    @SerializedName("created_at")
    val createdAt: String
) 