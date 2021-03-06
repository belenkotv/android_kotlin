package com.example.films

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

data class CategoryResponse(
    @SerializedName("genres")
    val genres: ArrayList<Category>
)
data class Category(val id: Int, val name: String);

data class MovieResponse(
    @SerializedName("results")
    val results: ArrayList<Movie>
)
data class Movie(
    val id: Int,
    @SerializedName("title")
    val name: String,
    @SerializedName("overview")
    val description: String,
    @SerializedName("poster_path")
    val pictureUrl: String,
    val adult: Boolean
)

data class MovieBrief(val name: String, val id: Int)
data class MovieDetail(val description: String, val id: Int)

interface TmdbApi {
    @GET("/3/genre/movie/list")
    fun getCategoryList(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<CategoryResponse>
    @GET
    fun getMovieListByCategory(
        @Url url: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<MovieResponse>
}

object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

}

object FilmsDataModel {

    private const val API_KEY: String = "692b9c89be39ac56b0a55158a4489580"
    private const val TMDB_URL: String = "https://api.themoviedb.org/"
    private const val TMDB_IMAGE_URL: String = "https://image.tmdb.org/t/p/w500"

    private val retrofit: TmdbApi
       get() = RetrofitClient.getClient(TMDB_URL).create(TmdbApi::class.java)

    fun getCategories(categories: MutableLiveData<List<Category>>) {
        retrofit.getCategoryList(API_KEY, "ru").enqueue(
            object : Callback<CategoryResponse> {
                override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                    Log.e("Films", "Error getting categories")
                }
                override fun onResponse(
                    call: Call<CategoryResponse>,
                    response: Response<CategoryResponse>
                ) {
                    categories.value = response.body()?.genres
                }
            }
        )
    }

    fun getImageUrl(path: String): String {
        return TMDB_IMAGE_URL + path
    }

    fun getMovies(category: Category, movies: MutableLiveData<List<Movie>>) {
        val url = "3/genre/" + category.id + "/movies";
        retrofit.getMovieListByCategory(url, API_KEY, "ru").enqueue(
            object : Callback<MovieResponse> {
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Log.e("Films", "Error getting movies of ${category.id} category")
                }
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    val results = response.body()?.results
                    var listOf = mutableListOf<Movie>()
                    if (results != null) {
                        for (movie in results) {
                            listOf.add(movie)
                        }
                    }
                    movies.value = listOf
                }
            }
        )
    }

}
