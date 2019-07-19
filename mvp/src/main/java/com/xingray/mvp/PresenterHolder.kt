package com.xingray.mvp

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/11 20:54
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
class PresenterHolder<P> : LifeCycleProvider, MvpView<P> {

    private var mPresenter: P? = null
    private var mPresenterInterfaces: Array<Class<*>>? = null
    private var mPresenterProxy: P? = null
    private val mTasks by lazy { LinkedList<Runnable>() }
    private var mLifeCycle = LifeCycle.INIT
    private val mLifeCycleObservers by lazy { CopyOnWriteArrayList<LifeCycleObserver>() }

    override val lifeCycle: LifeCycle
        get() = mLifeCycle

    @Suppress("UNCHECKED_CAST")
    override val presenter: P
        get() {
            var p: P? = mPresenter
            if (p != null) {
                return p
            }
            p = mPresenterProxy
            return if (p == null) {
                val presenterInterface = mPresenterInterfaces
                    ?: throw NullPointerException("must call setPresenterInterface to set mPresenterInterfaces")
                p =
                    Proxy.newProxyInstance(javaClass.classLoader, presenterInterface, object : InvocationHandler {
                        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                            method ?: throw NullPointerException("")
                            if (mPresenter != null) {
                                method.invoke(mPresenter, *(args ?: arrayOfNulls(0)))
                                return null
                            }
                            mTasks.add(Runnable {
                                val taskPresenter = mPresenter ?: throw NullPointerException()
                                try {
                                    method.invoke(taskPresenter, *(args ?: arrayOfNulls(0)))
                                } catch (e: IllegalAccessException) {
                                    e.printStackTrace()
                                } catch (e: InvocationTargetException) {
                                    e.printStackTrace()
                                }
                            })
                            return null
                        }
                    }) as P
                mPresenterProxy = p
                p
            } else {
                p
            }
        }

    override fun hasPresenter(): Boolean {
        return mPresenter != null
    }

    override fun setPresenterInterface(cls: Class<P>) {
        mPresenterInterfaces = arrayOf(cls)
    }

    override fun addLifeCycleObserver(observer: LifeCycleObserver) {
        mLifeCycleObservers.add(observer)
    }

    override fun removeLifeCycleObserver(observer: LifeCycleObserver) {
        mLifeCycleObservers.remove(observer)
    }

    override fun notifyLifeCycleChanged(lifeCycle: LifeCycle) {
        mLifeCycleObservers.forEach { it.onLifeCycleChanged(mLifeCycle) }
    }

    override fun bindPresenter(p: P) {
        mPresenter = p
        executeTasks()
    }

    private fun executeTasks() {
        if (mTasks.isEmpty()) {
            return
        }

        val iterator = mTasks.iterator()
        while (iterator.hasNext()) {
            val runnable = iterator.next()
            runnable.run()
            iterator.remove()
        }
    }
}