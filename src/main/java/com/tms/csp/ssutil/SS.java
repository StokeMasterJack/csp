package com.tms.csp.ssutil;

import java.util.logging.Handler;
import java.util.logging.Logger;

public class SS {

    public static Logger createLogger(Class cls) {
        Logger tmp = Logger.getLogger(cls.getName());
        Handler[] handlers = tmp.getParent().getHandlers();
        for (Handler handler : handlers) {
            handler.setFormatter(new SingleLineLogFormatter());
        }
        return tmp;
    }
}
