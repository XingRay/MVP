package com.xingray.mvp

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.util.*

/**
 * xxx
 *
 * @author : leixing
 * @date : 2019/7/11 20:54
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
open class Presenter<V : LifeCycleProvider>(cls: Class<V>) : MvpPresenter<V> {

    private val mViewInterfaces: Array<Class<*>>?
    private var mLifeCycle = LifeCycle.INIT
    private var mTasks: MutableList<PresenterTask<V>>? = null
    private var mViewProxies: HashMap<Long, V>? = null
    private var mViewProxy: V? = null

    internal var mView: V? = null

    /**
     * 获取视图对象
     *
     * @return 代理的视图对象
     */
    protected val view: V
        get() {
            if (mViewProxy == null) {
                if (mViewInterfaces == null) {
                    throw NullPointerException("must call setViewInterface to set mViewInterfaces")
                }
                mViewProxy = Proxy.newProxyInstance(javaClass.classLoader, mViewInterfaces,
                    InvocationHandler { proxy, method, args ->
                        if (mView != null) {
                            try {
                                method.invoke(mView, *args)
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            } catch (e: InvocationTargetException) {
                                e.printStackTrace()
                            }

                            return@InvocationHandler null
                        }

                        val task = PresenterTask(this@Presenter, method, args, null!!)
                        if (mTasks === null) {
                            mTasks = LinkedList<PresenterTask<V>>()
                        }
                        mTasks!!.add(task)

                        null
                    }) as V
            }
            return mViewProxy
        }

    protected val resumeViewLast: V
        get() = runOnLifeCycles(AddStrategy.OVERRIDE, LifeCycle.RESUME)

    protected val resumeViewOnce: V
        get() = runOnLifeCycles(AddStrategy.ADD_IF_NOT_EXIST, LifeCycle.RESUME)

    protected val resumeView: V
        get() = runOnLifeCycles(AddStrategy.INSERT_TAIL, LifeCycle.RESUME)

    init {
        mViewInterfaces = arrayOf(cls)
    }

    protected fun runOnLifeCycles(lifeCycle: LifeCycle): V {
        return runOnLifeCycles(AddStrategy.INSERT_TAIL)
    }

    protected fun runOnLifeCycles(strategy: AddStrategy<PresenterTask<V>>, lifeCycle: LifeCycle): V {
        if (mViewProxies == null) {
            mViewProxies = HashMap(2)
        }

        val key = getProxyKey(strategy, lifeCycle)
        var view: V? = mViewProxies!![key]
        if (view == null) {
            if (mViewInterfaces == null) {
                throw NullPointerException("must call setViewInterface to set mViewInterfaces")
            }

            view = Proxy.newProxyInstance(
                javaClass.classLoader, mViewInterfaces,
                createInvocationHandler(strategy, arrayOf(lifeCycle))
            ) as V
            mViewProxies!![key] = view
        }
        return view
    }

    protected fun runOnLifeCycles(strategy: AddStrategy<PresenterTask<V>>, vararg lifeCycles: LifeCycle): V {
        if (mViewProxies == null) {
            mViewProxies = HashMap(2)
        }

        val key = getProxyKey(strategy, lifeCycles)

        var view: V? = mViewProxies!![key]
        if (view == null) {
            if (mViewInterfaces == null) {
                throw NullPointerException("must call setViewInterface to set mViewInterfaces")
            }

            view = Proxy.newProxyInstance(
                javaClass.classLoader, mViewInterfaces,
                createInvocationHandler(strategy, lifeCycles)
            ) as V
            mViewProxies!![key] = view
        }
        return view
    }

    private fun createInvocationHandler(
        strategy: AddStrategy<PresenterTask<V>>,
        lifeCycles: Array<LifeCycle>
    ): InvocationHandler {
        return InvocationHandler { proxy, method, args ->
            if (mView != null && Util.contains(lifeCycles, mLifeCycle)) {
                method.invoke(mView, *args)
                return@InvocationHandler null
            }

            val task = PresenterTask(this@Presenter, method, args, lifeCycles)
            if (mTasks == null) {
                mTasks = LinkedList()
            }
            strategy.addTask(mTasks!!, task)

            null
        }
    }

    protected fun getProxyKey(strategy: AddStrategy<*>, lifeCycles: Array<LifeCycle>): Long {
        var key = strategy.hashCode().toLong()
        for (lifeCycle in lifeCycles) {
            key += (1 shl lifeCycle.ordinal).toLong()
        }
        return key
    }

    protected fun getProxyKey(strategy: AddStrategy<*>, lifeCycle: LifeCycle): Long {
        var key = strategy.hashCode().toLong()
        key += (1 shl lifeCycle.ordinal).toLong()
        return key
    }

    override fun bindView(view: V) {
        mView = view
        updateLifeCycle(view.lifeCycle)
        addLifeCycleObserver(view)
        executeTasks()
    }

    private fun updateLifeCycle(lifeCycle: LifeCycle) {
        mLifeCycle = lifeCycle
        if (mLifeCycle === LifeCycle.DESTROY) {
            onViewDestroy()
        }
    }

    protected fun onViewDestroy() {
        mView = null
        if (mTasks != null) {
            mTasks!!.clear()
        }

        mViewProxy = null
        if (mViewProxies != null) {
            mViewProxies!!.clear()
        }
    }

    private fun addLifeCycleObserver(lifeCycleProvider: LifeCycleProvider) {
        lifeCycleProvider.addLifeCycleObserver(object : LifeCycleObserver {
            override fun notifyLifeCycleChanged(lifeCycle: LifeCycle) {
                updateLifeCycle(lifeCycle)
                executeTasks()
            }
        })
    }

    private fun executeTasks() {
        if (mView == null || mTasks == null || mTasks!!.isEmpty()) {
            return
        }

        val iterator = mTasks!!.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            val lifeCycles = task.getLifeCycles()
            if (lifeCycles == null || Util.contains(lifeCycles, mLifeCycle)) {
                task.run()
                iterator.remove()
            }
        }
    }
}