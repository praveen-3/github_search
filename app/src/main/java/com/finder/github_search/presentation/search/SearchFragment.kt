package com.finder.github_search.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.finder.github_search.R
import com.finder.github_search.databinding.FragmentSearchBinding
import com.finder.github_search.presentation.search.adapter.UserAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels { SearchViewModelFactory() }
    private val userAdapter = UserAdapter { username ->
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToProfileFragment(username)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchInput()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItem >= totalItemCount - 5) {
                        viewModel.loadMoreResults()
                    }
                }
            })
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText.apply {
            doAfterTextChanged { text ->
                viewModel.onSearchQueryChanged(text?.toString() ?: "")
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.onSearchSubmitted()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onSearchSubmitted()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchResults.collectLatest { users ->
                        userAdapter.submitList(users)
                        binding.emptyView.isVisible = users.isEmpty() && !viewModel.loading.value!!
                    }
                }
                launch {
                    viewModel.loading.collectLatest { isLoading ->
                        binding.progressBar.isVisible = isLoading
                        binding.swipeRefresh.isRefreshing = isLoading
                    }
                }
                launch {
                    viewModel.error.collectLatest { error ->
                        error?.let {
                            binding.errorView.apply {
                                text = error
                                isVisible = error != null
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 