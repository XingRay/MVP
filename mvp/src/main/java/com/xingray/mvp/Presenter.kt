package com.xingray.mvp

import android.util.LongSparseArray
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.util.*

/**
 * MVP模式中的 Presenter 基类， 可以在指定 View 的方法的执行的时机
 * 同时解决了 View 的空指针和泄漏问题
 *
 * @author : leixing
 * @date : 2019/7/11 20:54
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
open class Presenter<V : LifeCycleProvider>(cls: Class<V>) : MvpPresenter<V> {

    private val viewInterfaces: Array<Class<*>>?
    private var lifeCycle = LifeCycle.INIT
    private val tasks by lazy { LinkedList<PresenterTask<*>>() }
    private val lifeCycleTasks by lazy { LinkedList<LifeCycleTask>() }
    private val viewProxies by lazy { LongSparseArray<V>(2) }
    private var viewProxy: V? = null

    internal var targetView: V? = null


    /**
     * 获取视图对象
     *
     * @return 代理的视图对象
     */
    @Suppress("UNCHECKED_CAST")
    protected val view: V
        get() {
            var proxy = viewProxy
            if (proxy != null) {
                return proxy
            }

            val vi = viewInterfaces
                ?: throw NullPointerException("must call setViewInterface to set viewInterfaces")
            proxy = createProxy(vi)
            viewProxy = proxy
            return proxy
        }

    protected val resumeViewLast: V
        get() = getLifeCyclesView(AddStrategy.OVERRIDE, LifeCycle.RESUME)

    protected val resumeViewOnce: V
        get() = getLifeCyclesView(AddStrategy.ADD_IF_NOT_EXIST, LifeCycle.RESUME)

    protected val resumeView: V
        get() = getLifeCyclesView(AddStrategy.INSERT_TAIL, LifeCycle.RESUME)

    init {
        viewInterfaces = arrayOf(cls)
    }

    protected fun getLifeCycleView(lifeCycle: LifeCycle): V {
        return getLifeCyclesView(AddStrategy.INSERT_TAIL)
    }

    protected fun getLifeCycleView(strategy: AddStrategy<PresenterTask<*>>, lifeCycle: LifeCycle): V {
        val key = getProxyKey(strategy, lifeCycle)
        var view: V? = viewProxies[key]
        if (view == null) {
            val vi = viewInterfaces
                ?: throw NullPointerException("must call setViewInterface to set viewInterfaces")

            @Suppress("UNCHECKED_CAST")
            view = Proxy.newProxyInstance(
                javaClass.classLoader, vi,
                createInvocationHandler(strategy, arrayOf(lifeCycle))
            ) as V
            viewProxies.put(key, view)
        }
        return view
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun getLifeCyclesView(strategy: AddStrategy<PresenterTask<*>>, vararg lifeCycles: LifeCycle): V {
        val key = getProxyKey(strategy, *lifeCycles)

        var view: V? = viewProxies[key]
        if (view == null) {
            val vi = viewInterfaces
                ?: throw NullPointerException("must call setViewInterface to set viewInterfaces")

            @Suppress("UNCHECKED_CAST")
            view = Proxy.newProxyInstance(
                javaClass.classLoader, vi,
                createInvocationHandler(strategy, lifeCycles as Array<LifeCycle>)
            ) as V
            viewProxies.put(key, view)
        }
        return view
    }

    protected fun runOnLifeCycle(lifeCycle: LifeCycle, task: Runnable) {
        runOnLifeCycles(arrayOf(lifeCycle), task::run)
    }

    protected fun runOnLifeCycle(lifeCycle: LifeCycle, task: () -> Unit) {
        runOnLifeCycles(arrayOf(lifeCycle), task)
    }

    protected fun runOnLifeCycles(lifeCycles: Array<LifeCycle>, task: Runnable) {
        runOnLifeCycles(lifeCycles, task::run)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun runOnLifeCycles(lifeCycles: Array<LifeCycle>, task: () -> Unit) {
        if (lifeCycles.contains(lifeCycle)) {
            task.invoke()
            return
        }

        lifeCycleTasks.add(LifeCycleTask(lifeCycles, task))
    }

    private fun createInvocationHandler(
        strategy: AddStrategy<PresenterTask<*>>,
        lifeCycles: Array<LifeCycle>
    ): InvocationHandler {
        return InvocationHandler { _, method, args ->
            if (targetView != null && lifeCycles.contains(lifeCycle)) {
                method.invoke(targetView, *(args ?: arrayOfNulls(0)))
                return@InvocationHandler null
            }

            val task = PresenterTask(this@Presenter, method, args, lifeCycles)
            strategy.addTask(tasks, task)

            null
        }
    }

    protected open fun getProxyKey(strategy: AddStrategy<*>, vararg lifeCycles: LifeCycle/*: Array<LifeCycle>*/): Long {
        var key = strategy.hashCode().toLong()
        for (lifeCycle in lifeCycles) {
            key += (1 shl lifeCycle.ordinal).toLong()
        }
        return key
    }

    protected open fun getProxyKey(strategy: AddStrategy<*>, lifeCycle: LifeCycle): Long {
        var key = strategy.hashCode().toLong()
        key += (1 shl lifeCycle.ordinal).toLong()
        return key
    }

    override fun bindView(view: V) {
        targetView = view
        addLifeCycleObserver(view)
        updateLifeCycle(view.lifeCycle)
    }

    private fun updateLifeCycle(lifeCycle: LifeCycle) {
        if (lifeCycle === this.lifeCycle) {
            return
        }

        this.lifeCycle = lifeCycle
        executeTasks()
        executeLifeCycleTasks()

        if (this.lifeCycle === LifeCycle.DESTROY) {
            onViewDestroy()
        }
    }

    protected open fun onViewDestroy() {
        this.targetView = null
        tasks.clear()

        viewProxy = null
        viewProxies.clear()
    }

    private fun addLifeCycleObserver(lifeCycleProvider: LifeCycleProvider) {
        lifeCycleProvider.addLifeCycleObserver(object : LifeCycleObserver {
            override fun onLifeCycleChanged(lifeCycle: LifeCycle) {
                updateLifeCycle(lifeCycle)
            }
        })
    }

    private fun executeTasks() {
        if (targetView == null || tasks.isEmpty()) {
            return
        }

        val iterator = tasks.iterator()
        val lifeCycle = this.lifeCycle

        while (iterator.hasNext()) {
            val task = iterator.next()
            val lifeCycles = task.lifeCycles
            if (lifeCycles == null || lifeCycles.contains(lifeCycle)) {
                task.run()
                iterator.remove()
            }
        }
    }

    private fun executeLifeCycleTasks() {
        if (lifeCycleTasks.isEmpty()) {
            return
        }

        val iterator = lifeCycleTasks.iterator()
        val lifeCycle = this.lifeCycle

        while (iterator.hasNext()) {
            val task = iterator.next()
            if (task.lifeCycles.contains(lifeCycle)) {
                task.task.invoke()
                iterator.remove()
            }
        }
    }

    private fun createProxy(vi: Array<Class<*>>): V {
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(javaClass.classLoader, vi, InvocationHandler { _, method, args ->
            if (targetView != null) {
                try {
                    method.invoke(targetView, *(args ?: arrayOfNulls(0)))
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }

                return@InvocationHandler null
            }

            val task = PresenterTask(this@Presenter, method, args, null)
            tasks.add(task)

            null
        }) as V
    }
}