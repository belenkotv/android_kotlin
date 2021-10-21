package com.example.films

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class CategoriesAdapter(private val viewModel: FilmsViewModel, private val owner: LifecycleOwner):
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var data: List<Category> = ArrayList()

    init {
        viewModel.getCategories().observe(owner, Observer {
            it?.let {
                this.refresh(it)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryNameView: TextView? = null
        var moviesView: RecyclerView? = null
        init {
            categoryNameView = itemView.findViewById(R.id.category_name)
            moviesView = itemView.findViewById(R.id.movies)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        var viewHolder = ViewHolder(itemView)
        viewHolder.moviesView?.layoutManager = LinearLayoutManager(
             parent.context, LinearLayoutManager.HORIZONTAL, false
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryNameView?.text = data[position].name
        holder.moviesView?.adapter = MoviesAdapter(data[position], viewModel, owner)
    }

    override fun getItemCount(): Int = data.size

    private fun refresh(categories: List<Category>) {
        this.data = categories
        notifyDataSetChanged()
    }

}

class MoviesAdapter(
        private val category: Category,
        private val viewModel: FilmsViewModel,
        private val owner: LifecycleOwner
    ) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private var data: List<MovieBrief> = ArrayList()

    init {
        viewModel.getMovies(category)?.observe(owner, Observer {
            it?.let {
                this.refresh(it)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var moviePictureView: ImageView? = null
        var movieNameView: TextView? = null
        var movie: MovieBrief? = null
        init {
            moviePictureView = itemView.findViewById(R.id.movie_picture)
            movieNameView = itemView.findViewById(R.id.movie_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        val ret = ViewHolder(itemView)
        itemView.setOnClickListener {
            val intent = Intent(it.context, MovieActivity::class.java)
            intent.putExtra(MovieActivity.MOVIE_ID, ret.movie?.id)
            it.context.startActivity(intent)
        }
        itemView.setOnLongClickListener {
            ret.movie?.id?.let { it ->
                viewModel.getMovieDetail(it)?.value?.description?.let { it ->
                    itemView.showDescription(it)
                }
            }
            return@setOnLongClickListener true
        }
        return ret
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.movieNameView?.text = data[position].name
        holder.movie = data[position]
    }

    override fun getItemCount(): Int = data.size

    private fun refresh(movies: List<MovieBrief>) {
        this.data = movies
        notifyDataSetChanged()
    }

    private fun View.showDescription (text: String, length: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, text, length).show()
    }

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)
        val button: Button = findViewById(R.id.search)
        button.setOnClickListener {
        }
        val categoriesView: RecyclerView = findViewById(R.id.categories)
        categoriesView.layoutManager = LinearLayoutManager(this)
        categoriesView.adapter = CategoriesAdapter(viewModel, this)
    }

}