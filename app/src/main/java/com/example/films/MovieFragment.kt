package com.example.films

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MovieFragment(
        private val viewModel: FilmsViewModel
    ) : Fragment(R.layout.fragment_movie) {

    private lateinit var moviePictureView: ImageView
    private lateinit var movieDescriptionView: TextView
    private lateinit var movieNotesView: EditText
    private lateinit var recordsDao: DbDao
    private lateinit var movie: Movie

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ret = super.onCreateView(inflater, container, savedInstanceState)
        ret?.let {
            moviePictureView = it.findViewById(R.id.movie_picture)
            movieDescriptionView = it.findViewById(R.id.movie_description)
            movieNotesView = it.findViewById(R.id.notes)
            recordsDao = Db.getInstance(ret.context).dbDao
            val buttonSave: Button = it.findViewById(R.id.save)
            buttonSave.setOnClickListener {
                val movie = this.movie
                val notes = movieNotesView.text.toString()
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    var movieRecord = recordsDao.get(movie.id.toLong())
                    if (movieRecord == null) {
                        val date = SimpleDateFormat("DD/MM/YYYY").format(Date())
                        recordsDao.insert(MovieRecord(movie.id.toLong(), date, notes))
                    } else {
                        movieRecord.notes = notes
                        recordsDao.update(movieRecord)
                    }
                }
                val inputMethodManager =
                    buttonSave.context.getSystemService(Activity.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(buttonSave.windowToken, 0)
                activity?.onBackPressed()
            }
        }
        return ret
    }

    fun setMovie(movie: Movie) {
        this.movie = movie
        viewModel.getMovie(movie.id)?.observe(
            this,
            Observer {
                it?.let {
                    movieDescriptionView.text = it.description
                    Picasso.get()
                        .load(FilmsDataModel.getImageUrl(it.pictureUrl))
                        .into(moviePictureView)
                    viewLifecycleOwner.lifecycleScope.launch {
                        movieNotesView.setText(readDb(movie))
                    }
                }
            }
        )
    }

    suspend fun readDb(movie: Movie): String {
        return withContext(Dispatchers.IO) {
            var movieRecord = recordsDao.get(movie.id.toLong())
            if (movieRecord != null) {
                return@withContext movieRecord.notes
            } else {
                return@withContext ""
            }
        }
    }

}