package com.xingray.sample.data

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing1012@qq.com
 * @date : 2019/7/11 17:29
 */
data class Student(var id: String, var name: String, var age: Int) {

    override fun toString(): String {
        return "Student{" +
                "id='" + id + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", age=" + age +
                '}'.toString()
    }
}
