package com.atoto.update.listener

/**
 * author:huitao
 */
abstract class LogInterceptor {
    abstract fun d(tag: String, msg: String)

    abstract fun e(tag: String, msg: String)

    abstract fun i(tag: String, msg: String)

    abstract fun w(tag: String, msg: String)

    abstract fun v(tag: String, msg: String)

}