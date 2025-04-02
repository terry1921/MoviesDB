package com.example.mobilecoding.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilecoding.R
import com.example.mobilecoding.databinding.FragmentSearchBinding
import com.example.mobilecoding.detail.MovieDetailFragment
import com.example.mobilecoding.main.MoviesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val searchAdapter: MoviesAdapter by lazy {
        MoviesAdapter { movie ->
            Timber.tag("SearchFragment").d("Movie clicked: ${movie.id}")
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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchMovies(query)
                } else {
                    searchAdapter.submitList(emptyList())
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collectLatest { movies ->
                searchAdapter.submitList(movies)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
