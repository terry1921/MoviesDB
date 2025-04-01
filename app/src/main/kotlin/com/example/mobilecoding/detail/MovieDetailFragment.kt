package com.example.mobilecoding.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.core.model.genre.Genre
import com.example.core.network.sources.Endpoints
import com.example.mobilecoding.R
import com.example.mobilecoding.databinding.FragmentMovieDetailBinding
import com.example.mobilecoding.formatBudget
import com.example.mobilecoding.formatDate
import com.example.mobilecoding.formatRuntime
import com.example.mobilecoding.roundRating
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"
        fun newInstance(movieId: Int): MovieDetailFragment {
            return MovieDetailFragment().apply {
                arguments = Bundle().apply { putInt(ARG_MOVIE_ID, movieId) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = arguments?.getInt(ARG_MOVIE_ID) ?: run {
            Toast.makeText(requireContext(), "Movie ID not found", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.getMovieDetail(movieId)
        setObserve()
    }

    private fun setObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.detailState.collectLatest { state ->
                when (state) {
                    is MovieDetailState.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is MovieDetailState.Success -> {
                        binding.progressBar.isVisible = false
                        val detail = state.detail

                        Glide.with(requireContext())
                            .load("${Endpoints.IMAGE_URL}w500${detail.backdropPath}")
                            .into(binding.ivBackdrop)

                        binding.tvReleaseDate.text = detail.releaseDate.formatDate()
                        binding.tvTitle.text = detail.title
                        binding.tvRatingImdb.text =
                            context?.getString(R.string.rating, detail.voteAverage.roundRating())
                        setGenres(detail.genres)
                        binding.tvLanguage.text = detail.originalLanguage.uppercase()
                        binding.tvLength.text = detail.runtime.formatRuntime()
                        binding.tvBudget.text =
                            context?.getString(R.string.budget, detail.budget.formatBudget())
                        binding.tvRevenue.text =
                            context?.getString(R.string.revenue, detail.revenue.formatBudget())
                        binding.tvOverview.text = detail.overview ?: ""
                    }

                    is MovieDetailState.Error -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setGenres(genres: List<Genre>) {
        binding.chipGroupGenres.removeAllViews()
        genres.forEach { genre ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = genre.name
                isClickable = false
                isCheckable = false
                setTextColor(context.getColor(android.R.color.white))
                setChipBackgroundColorResource(R.color.chip_background)
            }
            binding.chipGroupGenres.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
