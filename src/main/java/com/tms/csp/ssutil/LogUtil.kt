package com.tms.csp.ssutil

import java.util.logging.Logger

object LogUtil {

    fun createLogger(cls: Class<*>): Logger {
        return createLogger(cls.simpleName)
    }

    fun createLogger(name: String): Logger {
        val tmp = Logger.getLogger(name)
        val handlers = tmp.parent.handlers
        for (handler in handlers) {
            handler.formatter = SingleLineLogFormatter()
        }
        return tmp
    }
}