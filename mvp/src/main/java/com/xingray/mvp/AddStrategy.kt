package com.xingray.mvp

/**
 * 队列添加策略
 *
 * @author : leixing
 * @date : 2019/7/11 20:43
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 *
 */
interface AddStrategy<E> {

    /**
     * 每一个策略需要有一个唯一的策略编号
     *
     * @return 策略编号
     */
    val strategyCode: Int

    /**
     * 添加任务到任务列表
     *
     * @param list 列表
     * @param e    待添加的元素
     */
    fun addTask(list: MutableList<E>, e: E)

    companion object {

        const val CODE_OVERRIDE = 0
        const val CODE_ADD_IF_NOT_EXIST = 1
        const val CODE_INSERT_TAIL = 2
        const val CODE_INSERT_HEAD = 3

        /**
         * 覆盖
         */
        val OVERRIDE: AddStrategy<PresenterTask<*>> =
            object : AddStrategy<PresenterTask<*>> {

                override val strategyCode: Int
                    get() = CODE_OVERRIDE

                override fun addTask(list: MutableList<PresenterTask<*>>, e: PresenterTask<*>) {
                    list.remove(e)
                    list.add(e)
                }
            }


        /**
         * 不存在才添加
         */
        val ADD_IF_NOT_EXIST: AddStrategy<PresenterTask<*>> = object : AddStrategy<PresenterTask<*>> {

            override val strategyCode: Int
                get() = CODE_ADD_IF_NOT_EXIST

            override fun addTask(list: MutableList<PresenterTask<*>>, e: PresenterTask<*>) {
                if (list.contains(e)) {
                    return
                }
                list.add(e)
            }
        }


        /**
         * 尾部插入
         */
        val INSERT_TAIL: AddStrategy<PresenterTask<*>> = object : AddStrategy<PresenterTask<*>> {

            override val strategyCode: Int
                get() = CODE_INSERT_TAIL

            override fun addTask(list: MutableList<PresenterTask<*>>, e: PresenterTask<*>) {
                list.add(e)
            }
        }

        /**
         * 头部插入
         */
        @Suppress("unused")
        val INSERT_HEAD: AddStrategy<PresenterTask<*>> = object : AddStrategy<PresenterTask<*>> {

            override val strategyCode: Int
                get() = CODE_INSERT_HEAD

            override fun addTask(list: MutableList<PresenterTask<*>>, e: PresenterTask<*>) {
                list.add(0, e)
            }
        }
    }

}