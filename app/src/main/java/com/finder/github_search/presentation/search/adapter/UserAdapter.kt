package com.finder.github_search.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finder.github_search.data.model.GitHubUser
import com.finder.github_search.databinding.ItemUserBinding

class UserAdapter(
    private val onUserClick: (String) -> Unit
) : ListAdapter<GitHubUser, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding, onUserClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        private val binding: ItemUserBinding,
        private val onUserClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: GitHubUser) {
            binding.apply {
                usernameText.text = user.login
                bioText.text = user.bio
                root.setOnClickListener { onUserClick(user.login) }

                Glide.with(avatarImage)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .into(avatarImage)
            }
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<GitHubUser>() {
        override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
            return oldItem == newItem
        }
    }
} 