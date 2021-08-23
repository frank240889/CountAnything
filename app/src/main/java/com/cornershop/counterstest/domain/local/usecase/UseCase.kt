package com.cornershop.counterstest.domain.local.usecase

abstract class UseCase {
    abstract fun execute()
    open fun release(){}
}