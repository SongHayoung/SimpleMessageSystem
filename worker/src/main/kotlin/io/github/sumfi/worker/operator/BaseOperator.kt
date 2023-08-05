package io.github.sumfi.worker.operator

internal open class BaseOperator<T> {
    open fun operate(t: T) = Unit
    open fun relocate(t: T) = Unit
    protected open fun onSuccess(t: T) = Unit
    protected open fun onFailure(t: T) = Unit
}