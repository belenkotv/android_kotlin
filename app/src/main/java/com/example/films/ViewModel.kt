package com.example.films

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilmsViewModel() : ViewModel() {

    private var categories: MutableLiveData<List<Category>> = MutableLiveData()
    private var movies: HashMap<Category, MutableLiveData<List<Movie>>> = HashMap()

    init {
        val categoryList = FilmsDataModel.getCategories()
        categories.value = categoryList
        val iter = categoryList.listIterator()
        for (category in iter) {
            var movieList = MutableLiveData(FilmsDataModel.getMovies(category))
            movies.put(category, movieList)
        }
    }

    fun getCategories() = categories
    fun getMovies(category: Category) = movies.get(category)

}