package com.example.films

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        val movieDescriptionView: TextView = findViewById(R.id.movie_description)
        val movieId = intent.getIntExtra(MOVIE_ID, 0)
        val viewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)
        viewModel.getMovieDetail(movieId)?.observe(this, Observer {
            it?.let {
                movieDescriptionView.text = it.description
            }
        })
    }

    companion object {
        const val MOVIE_ID = "movie_id"
    }

}