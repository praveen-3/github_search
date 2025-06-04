package com.finder.github_search.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.finder.github_search.databinding.FragmentProfileBinding
import com.finder.github_search.presentation.profile.adapter.RepositoryAdapter
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileFragmentArgs by navArgs()
    private val viewModel: ProfileViewModel by viewModels { ProfileViewModelFactory(args.username) }
    private val repositoryAdapter = RepositoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToolbar()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.repositoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = repositoryAdapter
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItem >= totalItemCount - 5) {
                        viewModel.loadMoreRepositories()
                    }
                }
            })
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect { user ->
                        user?.let { updateUserInfo(it) }
                    }
                }
                launch {
                    viewModel.repositories.collect { repositories ->
                        repositoryAdapter.submitList(repositories)
                    }
                }
                launch {
                    viewModel.loading.collect { isLoading ->
                        binding.progressBar.isVisible = isLoading
                    }
                }
                launch {
                    viewModel.error.collect { error ->
                        error?.let {
                            binding.errorContainer.isVisible = true
                            binding.contentContainer.isVisible = false
                            binding.errorView.text = error
                        } ?: run {
                            binding.errorContainer.isVisible = false
                            binding.contentContainer.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun updateUserInfo(user: com.finder.github_search.data.model.GitHubUser) {
        binding.apply {
            toolbar.title = user.login
            usernameText.text = user.login
            nameText.text = user.name
            bioText.text = user.bio
            reposCountText.text = user.publicRepos.toString()
            followersCountText.text = user.followers.toString()
            followingCountText.text = user.following.toString()

            Glide.with(profileImage)
                .load(user.avatarUrl)
                .into(profileImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 