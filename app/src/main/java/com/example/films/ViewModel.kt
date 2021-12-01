package com.example.films

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilmsViewModel() : ViewModel() {

    private var categories: MutableLiveData<List<Category>> = MutableLiveData()
    private var moviesByCategory: HashMap<Category, MutableLiveData<List<Movie>>> = HashMap()
    private var moviesById: HashMap<Int, MutableLiveData<Movie>> = HashMap()

    init {
        FilmsDataModel.getCategories(categories)
        categories.observeForever {
            for (category in it.listIterator()) {
                val movieList = MutableLiveData<List<Movie>>()
                FilmsDataModel.getMovies(category, movieList)
                moviesByCategory.put(category, movieList)
                movieList.observeForever {
                    for (movie in movieList.value!!.listIterator()) {
                        moviesById.put(movie.id, MutableLiveData(movie))
                    }
                }
            }
        }
    }

    fun getCategories() = categories
    fun getMovies(category: Category) = moviesByCategory.get(category)
    fun getMovie(id: Int) = moviesById.get(id)

}