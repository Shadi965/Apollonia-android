package songbird.apollo.presentation.ui

sealed class LoadResult<out T> {
    data object Loading : LoadResult<Nothing>()
    data class Success<out T>(val data: T) : LoadResult<T>()

    data class Error(val error: Throwable, val message: String? = error.message) : LoadResult<Nothing>()
    data object Empty : LoadResult<Nothing>()
}

fun <T, R> LoadResult<T>.map(transform: (T) -> R): LoadResult<R> {
    return when (this) {
        is LoadResult.Loading -> LoadResult.Loading
        is LoadResult.Success -> LoadResult.Success(transform(data))
        is LoadResult.Error -> LoadResult.Error(error)
        is LoadResult.Empty -> LoadResult.Empty
    }
}
