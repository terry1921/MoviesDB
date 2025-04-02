package com.example.mobilecoding.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.model.movie.Movie
import com.example.core.network.sources.Endpoints
import com.example.mobilecoding.R
import com.example.mobilecoding.formatDate

class MoviesAdapter(
    private val onItemClick: ((Movie) -> Unit)? = null
) : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    private var genreMap: Map<Int, String> = emptyMap()
    fun setGenreMap(map: Map<Int, String>) {
        genreMap = map
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val tvVoteCount: TextView = itemView.findViewById(R.id.tvVoteCount)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvReleaseDate: TextView = itemView.findViewById(R.id.tvReleaseDate)
        val tvGenres: TextView = itemView.findViewById(R.id.tvGenres)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val tvOverview: TextView = itemView.findViewById(R.id.tvOverview)
        val tvExtraInfo: TextView = itemView.findViewById(R.id.tvExtraInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        val context = holder.itemView.context

        holder.tvTitle.text = movie.title
        holder.tvVoteCount.text = context.getString(R.string.vote_count_format, movie.voteCount)
        holder.tvReleaseDate.text = movie.releaseDate?.formatDate()
        val genreNames = movie.genreIds?.mapNotNull { genreMap[it] }
        holder.tvGenres.text = if (genreNames?.isNotEmpty() == true) genreNames.joinToString(", ") else "N/A"
        holder.ratingBar.rating = (movie.voteAverage?.div(2))?.toFloat() ?: 0f
        holder.tvOverview.text = movie.overview
        holder.tvExtraInfo.text = context.getString(R.string.original_language_format, movie.originalLanguage)
        Glide.with(context)
            .load("${Endpoints.IMAGE_URL}w500${movie.posterPath}")
            .into(holder.ivPoster)
        holder.itemView.setOnClickListener { onItemClick?.invoke(movie) }
    }

}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem == newItem
}
