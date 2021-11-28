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

data class MovieBrief(val name: String, val id: Int)
data class MovieDetail(val description: String, val id: Int)

interface TmdbApi {
    @GET("/3/genre/movie/list")
    fun getCategoryList(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<CategoryResponse>
    @GET("/3/genre")
    fun getMovieListByCategory(
        @Url url: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Call<CategoryResponse>
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
    private val retrofit: TmdbApi
       get() = RetrofitClient.getClient(TMDB_URL).create(TmdbApi::class.java)

    fun getCategories(categories: MutableLiveData<List<Category>>) {
        retrofit.getCategoryList(API_KEY, "ru").enqueue(object : Callback<CategoryResponse> {
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("Films", "Error getting categories")
            }
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>
            ) {
                categories.value = response.body()?.genres
            }
        })
    }

    fun getMovies(category: Category): List<MovieBrief> {
        return when (category.name.lowercase()) {
            "комедия" -> listOf(
                MovieBrief("Смешная", 1),
                MovieBrief("Страшная", 2),
                MovieBrief("Глупая", 3),
                MovieBrief("Длинная", 4)
            )
            "драма" -> listOf(
                MovieBrief("Смешная", 5),
                MovieBrief("Страшная", 6),
                MovieBrief("Глупая", 7),
                MovieBrief("Длинная", 8)
            )
            "боевик" -> listOf(
                MovieBrief("Смешная", 9),
                MovieBrief("Страшная", 10),
                MovieBrief("Глупая", 11),
                MovieBrief("Это он", 12),
                MovieBrief("Длинная", 13)
            )
            "семейный" -> listOf(
                MovieBrief("Смешная", 14),
                MovieBrief("Страшная", 15),
                MovieBrief("Глупая", 16),
                MovieBrief("Длинная", 17)
            )
            else -> listOf()
        }
    }

    fun getMovieDetail(id: Int): MovieDetail {
        return MovieDetail("Это нужно увидеть лично", id)
    }

}
