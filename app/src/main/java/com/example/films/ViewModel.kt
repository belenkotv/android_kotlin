package com.example.films

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilmsViewModel() : ViewModel() {

    private var categories: MutableLiveData<List<Category>> = MutableLiveData()
    private var movies: HashMap<Category, MutableLiveData<List<MovieBrief>>> = HashMap()
    private var movieDetails: HashMap<Int, MutableLiveData<MovieDetail>> = HashMap()

    init {
        val categoryList = FilmsDataModel.getCategories()
        categories.value = categoryList
        for (category in categoryList.listIterator()) {
            val movieList = FilmsDataModel.getMovies(category)
            movies.put(category, MutableLiveData(movieList))
            for (movie in movieList.listIterator()) {
                movieDetails.put(movie.id, MutableLiveData(FilmsDataModel.getMovieDetail(movie.id)))
            }
        }
    }

    fun getCategories() = categories
    fun getMovies(category: Category) = movies.get(category)
    fun getMovieDetail(id: Int) = movieDetails.get(id)

}