package com.xingray.sample.lib

import android.os.Handler
import android.os.Looper

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

/**
 * 任务执行器
 *
 * @author : leixing
 * @date : 2018/5/18
 * Version : 0.0.1
 */
@Suppress("unused")
class TaskExecutor private constructor() {

    init {
        throw UnsupportedOperationException()
    }

    private class NamedThreadFactory internal constructor(private val mThreadName: String) : ThreadFactory {
        private val mCount: AtomicInteger = AtomicInteger(1)

        override fun newThread(r: Runnable): Thread {
            return Thread(r, mThreadName + "#" + mCount.getAndIncrement())
        }
    }

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = max(4, min(CPU_COUNT - 1, 8))
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1

        /**
         * 主线程handler，用于将任务提交至主线程执行
         */
        @Suppress("MemberVisibilityCanBePrivate")
        val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

        fun ui(task: Runnable) {
            handler.post(task)
        }

        fun ui(task: Runnable, delayMills: Long) {
            handler.postDelayed(task, delayMills)
        }

        fun io(task: Runnable) {
            ioPool.execute(task)
        }

        fun cpu(task: Runnable) {
            cpuPool.execute(task)
        }

        fun enqueue(task: Runnable) {
            serialPool.execute(task)
        }

        fun infinite(task: Runnable) {
            cachePool.execute(task)
        }

        val uiPool: Executor by lazy { Executor { command -> handler.post(command) } }

        /**
         * IO读写线程池,最多CORE_POOL_SIZE同时执行
         */
        @Suppress("MemberVisibilityCanBePrivate")
        val ioPool: Executor by lazy {
            ThreadPoolExecutor(
                CORE_POOL_SIZE,
                15,
                5L, TimeUnit.SECONDS,
                LinkedBlockingQueue(),
                NamedThreadFactory("io-pool")
            )
        }

        /**
         * 繁重 任务线程池，适用于像ImageLoader转换图像这种时间不长但又很占CPU的任务
         * 排队执行的ThreadPool,核心线程为CORE_POOL_SIZE+1个
         */
        private val cpuPool: Executor by lazy {
            ThreadPoolExecutor(
                1,
                1,
                5L, TimeUnit.SECONDS,
                LinkedBlockingQueue(128),
                NamedThreadFactory("cpu-pool")
            )
        }

        /**
         * 串行线程池
         */
        private val serialPool: Executor by lazy {
            ThreadPoolExecutor(
                1,
                1,
                0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                NamedThreadFactory("serial-pool")
            )
        }

        /**
         * 全局cachePool,适用于AsyncHttpClient等不
         * 限制任务数的请求
         */
        private val cachePool: Executor by lazy {
            ThreadPoolExecutor(
                0,
                MAXIMUM_POOL_SIZE,
                60L, TimeUnit.SECONDS,
                SynchronousQueue(),
                NamedThreadFactory("cache-pool")
            )
        }
    }
}
