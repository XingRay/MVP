package com.xingray.sample.data

import java.util.ArrayList

/**
 * xxx
 *
 * @author : leixing
 * @version : 1.0.0
 * mail : leixing@baidu.com
 * @date : 2019/7/11 17:30
 */
class StudentMockDataSource : StudentDataSource {
    override fun loadStudents(): List<Student> {
        val count = 100
        val list = ArrayList<Student>(100)
        for (i in 0 until count) {
            list.add(Student(i.toString(), "学生$i", 10 + i % 10))
        }
        return list
    }
}
