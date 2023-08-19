package com.masyanolchik.grandtheftradio2.domain

sealed class Result<T> {
    class Loading<T>(): Result<T>()
    class Success<T>(val data: T) : Result<T>()
    class Completed<T>(): Result<T>()
    class Error<T>(val throwable: Throwable) : Result<T>()
}