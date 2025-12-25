package com.example.memalloc;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Name("AAServiceMethodMemoryAllocEvent")
@Label("Service Method Memory Alloc")
@Category("Memory")
@StackTrace(false)
public class AAServiceMethodMemoryAllocEvent extends Event {
    @Label("Allocated Bytes")
    long allocatedBytes;

    @Label("Method Signature")
    String methodSignature;

    @Label("Success")
    boolean success;
}
