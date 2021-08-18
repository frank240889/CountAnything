package com.cornershop.counterstest.usecase

abstract class UseCase {
    abstract fun execute()
    open fun release(){}
}