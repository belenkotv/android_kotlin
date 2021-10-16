package com.example.films

data class Category(val name: String);
data class MovieBrief(val name: String, val id: Int)
data class MovieDetail(val description: String, val id: Int)

object FilmsDataModel {

    fun getCategories() = listOf(
        Category("Комедии"),
        Category("Драмы"),
        Category("Боевики"),
        Category("Детское")
    )

    fun getMovies(category: Category): List<MovieBrief> {
        return when (category.name) {
            "Комедии" -> listOf(
                MovieBrief("Смешная", 1),
                MovieBrief("Страшная", 2),
                MovieBrief("Глупая", 3),
                MovieBrief("Длинная", 4)
            )
            "Драмы" -> listOf(
                MovieBrief("Смешная", 5),
                MovieBrief("Страшная", 6),
                MovieBrief("Глупая", 7),
                MovieBrief("Длинная", 8)
            )
            "Боевики" -> listOf(
                MovieBrief("Смешная", 9),
                MovieBrief("Страшная", 10),
                MovieBrief("Глупая", 11),
                MovieBrief("Это он", 12),
                MovieBrief("Длинная", 13)
            )
            "Детское" -> listOf(
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
