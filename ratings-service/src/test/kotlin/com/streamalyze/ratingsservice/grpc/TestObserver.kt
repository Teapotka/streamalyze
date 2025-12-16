package com.streamalyze.ratingsservice.grpc

import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestObserver<T> : StreamObserver<T> {
    var value: T? = null
        private set

    var error: Throwable? = null
        private set

    var completed: Boolean = false
        private set

    private val latch = CountDownLatch(1)

    override fun onNext(v: T) {
        value = v
    }

    override fun onError(t: Throwable) {
        error = t
        latch.countDown()
    }

    override fun onCompleted() {
        completed = true
        latch.countDown()
    }

    fun await(timeoutSeconds: Long = 5): Boolean = latch.await(timeoutSeconds, TimeUnit.SECONDS)
}
