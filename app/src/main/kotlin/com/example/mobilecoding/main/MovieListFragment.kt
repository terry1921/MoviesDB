package com.example.mobilecoding.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.data.repository.movies.SortOption
import com.example.mobilecoding.R
import com.example.mobilecoding.databinding.FragmentMovieListBinding
import com.example.mobilecoding.detail.MovieDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MoviesViewModel by viewModels()
    private val moviesAdapter: MoviesAdapter by lazy {
        MoviesAdapter { movie ->
            Timber.tag("MovieListFragment").d("Movie clicked: ${movie.id}")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MovieDetailFragment.newInstance(movie.id))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moviesAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        configureSort()
        viewModel.loadGenresMapping()
        setupInfiniteScroll()
        viewModel.refresh()
        observeUIState()
        binding.btnRetry.setOnClickListener { viewModel.refresh() }
    }

    private fun configureSort() {
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val sortOption = when (position) {
                    0 -> SortOption.POPULARITY_DESC
                    1 -> SortOption.POPULARITY_ASC
                    2 -> SortOption.VOTE_AVG_DESC
                    3 -> SortOption.VOTE_AVG_ASC
                    4 -> SortOption.VOTE_COUNT_DESC
                    5 -> SortOption.VOTE_COUNT_ASC
                    else -> SortOption.POPULARITY_DESC
                }
                viewModel.setSortOption(sortOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupInfiniteScroll() {
        binding.rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition >= totalItemCount - 5) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun observeUIState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.spinnerSort.visibility = View.GONE
                            binding.rvMovies.visibility = View.GONE
                            binding.errorView.visibility = View.GONE
                        }

                        is MainState.MoviesSuccess -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvMovies.visibility = View.VISIBLE
                            binding.spinnerSort.visibility = View.VISIBLE
                            binding.errorView.visibility = View.GONE
                            moviesAdapter.submitList(state.result.movies)
                        }

                        is MainState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvMovies.visibility = View.GONE
                            binding.spinnerSort.visibility = View.GONE
                            binding.errorView.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = state.message
                        }

                        MainState.GenresSuccess -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.genreMap.collect { map ->
                    moviesAdapter.setGenreMap(map)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
