package com.example.films

data class Category(val name: String);

data class Movie(val name: String)

object FilmsDataModel {

    fun getCategories() = listOf(
        Category("Комедии"),
        Category("Драмы"),
        Category("Боевики"),
        Category("Детское")
    )

    fun getMovies(category: Category): List<Movie> {
        return when (category.name) {
            "Комедии" -> listOf(
                Movie("Смешная"),
                Movie("Страшная"),
                Movie("Глупая"),
                Movie("Длинная")
            )
            "Драмы" -> listOf(
                Movie("Смешная"),
                Movie("Страшная"),
                Movie("Глупая"),
                Movie("Длинная")
            )
            "Боевики" -> listOf(
                Movie("Смешная"),
                Movie("Страшная"),
                Movie("Глупая"),
                Movie("Это он"),
                Movie("Длинная")
            )
            "Детское" -> listOf(
                Movie("Смешная"),
                Movie("Страшная"),
                Movie("Глупая"),
                Movie("Длинная")
            )
            else -> listOf()
        }
    }

}
