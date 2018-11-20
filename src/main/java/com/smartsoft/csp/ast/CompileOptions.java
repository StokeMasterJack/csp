package com.smartsoft.csp.ast;

public class CompileOptions {

    enum SerializationFormat {
        TINY, XML
    }

    public boolean gc = true;
    public boolean at = true;
    public SerializationFormat serializationFormat = SerializationFormat.TINY;
    public boolean varsLine = true;
    public boolean invVarsLine = true;

}
