package com.example.films

import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class Preferences private constructor(private val prefs: SharedPreferences) {
    
    private val PREF_ADULT: String = "adult"

    companion object Factory {
        private val PREF_FILE: String = "prefs"
        fun create(context: Context) : Preferences = Preferences(
            context.getSharedPreferences(Preferences.PREF_FILE, Context.MODE_PRIVATE)
        )
    }

    fun getAdult(): Boolean {
        return this.prefs.getBoolean(PREF_ADULT, false)
    }

    fun setAdult(adult: Boolean) {
        var editor = this.prefs.edit()
        editor.putBoolean(PREF_ADULT, adult)
        editor.commit()
    }

}

interface OnClicks {
    fun onMovie(movie: Movie)
}

class CategoriesAdapter(
        private val viewModel: FilmsViewModel,
        private val owner: LifecycleOwner,
        private val prefs: Preferences,
        private val onClicks: OnClicks
    ) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

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
        holder.moviesView?.adapter =
            MoviesAdapter(data[position], owner, viewModel, prefs, onClicks)
    }

    override fun getItemCount(): Int = data.size

    private fun refresh(categories: List<Category>) {
        data = categories
        notifyDataSetChanged()
    }

}

class MoviesAdapter(
        category: Category,
        owner: LifecycleOwner,
        private val viewModel: FilmsViewModel,
        private val prefs: Preferences,
        private val onClicks: OnClicks
    ) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private var data: List<Movie> = ArrayList()

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
        var movie: Movie? = null
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
            ret.movie?.let { it1 -> onClicks.onMovie(it1) }
        }
        itemView.setOnLongClickListener {
            ret.movie?.id?.let { it ->
                viewModel.getMovie(it)?.value?.description?.let { it ->
                    itemView.showDescription(it)
                }
            }
            return@setOnLongClickListener true
        }
        return ret
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = data[position]
        Picasso.get()
            .load(FilmsDataModel.getImageUrl(movie.pictureUrl))
            .into(holder.moviePictureView)
        holder.movieNameView?.text = movie.name
        holder.movie = movie
    }

    override fun getItemCount(): Int = data.size

    private fun refresh(movies: List<Movie>) {
        var filtered = mutableListOf<Movie>()
        val adult = prefs.getAdult()
        for (movie in movies) {
            if (adult == movie.adult) {
                filtered.add(movie)
            }
        }
        data = filtered
        notifyDataSetChanged()
    }

    private fun View.showDescription (text: String, length: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, text, length).show()
    }

}

class MainActivity : AppCompatActivity(R.layout.activity_main), OnClicks {

    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var prefs: Preferences
    private lateinit var serviceIntent: Intent
    private lateinit var movieFragment: MovieFragment
    var dataBroadcastReceiver: BroadcastReceiver? = null
    var networkBroadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)
        movieFragment = MovieFragment(viewModel)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.movie_view, movieFragment)
                .hide(movieFragment)
                .commit()
        }
        val button: Button = findViewById(R.id.search)
        button.setOnClickListener {
        }
        prefs = Preferences.create(this)
        val categoriesView: RecyclerView = findViewById(R.id.categories)
        categoriesView.layoutManager = LinearLayoutManager(this)
        categoriesAdapter = CategoriesAdapter(viewModel, this, prefs, this)
        categoriesView.adapter = categoriesAdapter
        serviceIntent = Intent(applicationContext, TmdbService::class.java)
        dataBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                //when (intent?.action) {
                //    BROADCAST_CHANGE_TYPE_CHANGED -> handleChangeTypeChanged()
                //}
            }
        }
        networkBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null || intent.extras == null) {
                    return
                }
                context?.let {
                    if (isNetworkAvailable(it)) {
                        Toast.makeText(context, "Сеть доступна", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Сеть недоступна", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        menu?.findItem(R.id.adult)?.isChecked = prefs.getAdult()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(dataBroadcastReceiver, IntentFilter(TmdbService.BROADCAST_DATA_CHANGED))
        registerReceiver(
            networkBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        startService(serviceIntent)
    }

    override fun onStop() {
        unregisterReceiver(dataBroadcastReceiver)
        unregisterReceiver(networkBroadcastReceiver)
        stopService(serviceIntent)
        super.onStop()
    }

    override fun onBackPressed() {
        if (movieFragment.isHidden) {
            super.onBackPressed()
        } else {
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .hide(movieFragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.adult -> {
                item.isChecked = !item.isChecked
                prefs.setAdult(item.isChecked)
                categoriesAdapter.notifyDataSetChanged()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMovie(movie: Movie) {
        movieFragment.setMovie(movie)
        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .show(movieFragment)
            .commit()
        //val intent = Intent(it.context, MovieActivity::class.java)
        //intent.putExtra(MovieActivity.MOVIE_ID, ret.movie?.id)
        //it.context.startActivity(intent)
    }

    fun isNetworkAvailable(context: Context) =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

}