package com.example.films

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MovieFragment(
        private val viewModel: FilmsViewModel
    ) : Fragment(R.layout.fragment_movie) {

    private lateinit var moviePictureView: ImageView
    private lateinit var movieDescriptionView: TextView
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
            recordsDao = Db.getInstance(ret.context).dbDao
            val buttonSave: Button = it.findViewById(R.id.save)
            val notes: EditText = it.findViewById(R.id.notes)
            buttonSave.setOnClickListener {
                var movieRecord = recordsDao.get(this.movie.id.toLong())
                val notes = notes.text.toString()
                if (movieRecord == null) {
                    val date = SimpleDateFormat("DD/MM/YYYY").format(Date())
                    recordsDao.insert(MovieRecord(this.movie.id.toLong(), date, notes))
                } else {
                    movieRecord.notes = notes
                    recordsDao.update(movieRecord)
                }
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
                }
            }
        )
    }

}