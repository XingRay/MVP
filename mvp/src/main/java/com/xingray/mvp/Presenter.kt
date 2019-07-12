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

    private val viewInterfaces: Array<Class<*>>?
    private var lifeCycle = LifeCycle.INIT
    private val tasks: MutableList<PresenterTask<*>> by lazy { LinkedList<PresenterTask<*>>() }
    private val viewProxies: HashMap<Long, V>  by lazy { HashMap<Long, V>(2) }
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
            proxy = Proxy.newProxyInstance(javaClass.classLoader, vi,
                InvocationHandler { _, method, args ->
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
            viewProxy = proxy
            return proxy
        }

    protected val resumeViewLast: V
        get() = runOnLifeCycles(AddStrategy.OVERRIDE, LifeCycle.RESUME)

    protected val resumeViewOnce: V
        get() = runOnLifeCycles(AddStrategy.ADD_IF_NOT_EXIST, LifeCycle.RESUME)

    protected val resumeView: V
        get() = runOnLifeCycles(AddStrategy.INSERT_TAIL, LifeCycle.RESUME)

    init {
        viewInterfaces = arrayOf(cls)
    }

    protected open fun runOnLifeCycles(lifeCycle: LifeCycle): V {
        return runOnLifeCycles(AddStrategy.INSERT_TAIL)
    }

    protected open fun runOnLifeCycles(strategy: AddStrategy<PresenterTask<*>>, lifeCycle: LifeCycle): V {
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
            viewProxies[key] = view
        }
        return view
    }

    protected open fun runOnLifeCycles(strategy: AddStrategy<PresenterTask<*>>, vararg lifeCycles: LifeCycle): V {
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
            viewProxies[key] = view
        }
        return view
    }

    private fun createInvocationHandler(
        strategy: AddStrategy<PresenterTask<*>>,
        lifeCycles: Array<LifeCycle>
    ): InvocationHandler {
        return InvocationHandler { _, method, args ->
            if (targetView != null && Util.contains(lifeCycles, lifeCycle)) {
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
        updateLifeCycle(view.lifeCycle)
        addLifeCycleObserver(view)
        executeTasks()
    }

    private fun updateLifeCycle(lifeCycle: LifeCycle) {
        this.lifeCycle = lifeCycle
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
            override fun notifyLifeCycleChanged(lifeCycle: LifeCycle) {
                updateLifeCycle(lifeCycle)
                executeTasks()
            }
        })
    }

    private fun executeTasks() {
        if (targetView == null || tasks.isEmpty()) {
            return
        }

        val iterator = tasks.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            val lifeCycles = task.lifeCycles
            if (lifeCycles == null || Util.contains(lifeCycles, lifeCycle)) {
                task.run()
                iterator.remove()
            }
        }
    }
}