package com.example.mobilecoding.ui

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
import com.example.mobilecoding.R
import java.text.SimpleDateFormat
import java.util.Locale

class MoviesAdapter(
    private val onItemClick: ((Movie) -> Unit)? = null
) : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    // Mapeo de géneros (id -> nombre)
    private var genreMap: Map<Int, String> = emptyMap()

    // Método para actualizar el mapeo de géneros
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
        holder.tvReleaseDate.text = formatDate(movie.releaseDate)
        val genreNames = movie.genreIds.mapNotNull { genreMap[it] }
        holder.tvGenres.text = if (genreNames.isNotEmpty()) genreNames.joinToString(", ") else "N/A"
        holder.ratingBar.rating = (movie.voteAverage / 2).toFloat()
        holder.tvOverview.text = movie.overview
        holder.tvExtraInfo.text = context.getString(R.string.original_language_format, movie.originalLanguage)
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(holder.ivPoster)
        holder.itemView.setOnClickListener { onItemClick?.invoke(movie) }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem == newItem
}
