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
class PresenterHolder<P> : LifeCycleProvider, LifeCycleObserver, InvocationHandler, MvpView<P> {

    private var mLifeCycleObservers: CopyOnWriteArrayList<LifeCycleObserver>? = null
    private var mPresenter: P? = null
    private var mPresenterInterfaces: Array<Class<*>>? = null
    private var mPresenterProxy: P? = null
    private var mTasks: MutableList<Runnable>? = null
    override var lifeCycle = LifeCycle.INIT
        private set(value: LifeCycle) {
            super.lifeCycle = value
        }

    override val presenter: P
        get() {
            if (mPresenter != null) {
                return mPresenter
            }

            if (mPresenterProxy == null) {
                if (mPresenterInterfaces == null) {
                    throw NullPointerException("must call setPresenterInterface to set mPresenterInterfaces")
                }
                mPresenterProxy = Proxy.newProxyInstance(javaClass.classLoader, mPresenterInterfaces!!, this) as P
            }
            return mPresenterProxy
        }

    override fun hasPresenter(): Boolean {
        return mPresenter != null
    }

    override fun setPresenterInterface(cls: Class<P>) {
        mPresenterInterfaces = arrayOf(cls)
    }

    override fun addLifeCycleObserver(observer: LifeCycleObserver) {
        if (mLifeCycleObservers == null) {
            mLifeCycleObservers = CopyOnWriteArrayList()
        }
        mLifeCycleObservers!!.add(observer)
    }

    override fun removeLifeCycleObserver(observer: LifeCycleObserver) {
        if (mLifeCycleObservers == null) {
            return
        }
        mLifeCycleObservers!!.remove(observer)
    }

    override fun notifyLifeCycleChanged(lifeCycle: LifeCycle) {
        this.lifeCycle = lifeCycle
        if (mLifeCycleObservers != null) {
            for (observer in mLifeCycleObservers!!) {
                observer.notifyLifeCycleChanged(lifeCycle)
            }
        }
    }

    override fun bindPresenter(presenter: P) {
        mPresenter = presenter
        executeTasks()
    }

    private fun executeTasks() {
        if (mTasks == null || mTasks!!.isEmpty()) {
            return
        }

        val iterator = mTasks!!.iterator()
        while (iterator.hasNext()) {
            val runnable = iterator.next()
            runnable.run()
            iterator.remove()
        }
    }

    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        if (mPresenter != null) {
            method.invoke(mPresenter, *args)
            return null
        }

        if (mTasks == null) {
            mTasks = LinkedList()
        }
        mTasks!!.add(Runnable {
            try {
                method.invoke(mPresenter, *args)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        })
        return null
    }
}