package com.tms.csp.ssutil;


import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class SingleLineLogFormatter extends Formatter {
    // This method is called for every log records
    public String format(LogRecord rec) {
        StringBuffer sb = new StringBuffer(1000);
        sb.append(rec.getLevel());
        sb.append(':');
        sb.append(' ');
        sb.append(formatMessage(rec));
        sb.append('\n');
        return sb.toString();
    }

//    private String calcDate(long millis) {
//        DateTimeFormat format = DateTimeFormat.getFormat("MMM dd,yyyy HH:mm");
//        return format.format(new Date(millis));
//    }

    // This method is called just after the handler using this
    // formatter is created
    public String getHead(Handler h) {
        return "";
    }

    // This method is called just after the handler using this
    // formatter is closed
    public String getTail(Handler h) {
        return "";
    }
}
