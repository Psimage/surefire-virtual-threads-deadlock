[![Java CI with Maven](https://github.com/Psimage/surefire-virtual-threads-deadlock/actions/workflows/maven.yml/badge.svg)](https://github.com/Psimage/surefire-virtual-threads-deadlock/actions/workflows/maven.yml)

https://github.com/Psimage/surefire-virtual-threads-deadlock/blob/12be5960dc5552067581eadf3d651f405d430395/src/test/java/me/yarosbug/DeadlockTest.java#L13-L43

Profile with JFR:

```
<argLine>-Djdk.virtualThreadScheduler.parallelism=2 -XX:StartFlightRecording:duration=30s,settings=profile,jdk.VirtualThreadStart#enabled=true,jdk.VirtualThreadEnd#enabled=true,jdk.VirtualThreadPinned#threshold=1ms</argLine>
```

Thread Dump with Virtual Threads:

```
jcmd <pid> Thread.dump_to_file -format=json <file>
```

Slice of thread dump:

```JSON5
{
  "container": "java.util.concurrent.ThreadPerTaskExecutor@5a6f9660",
  "parent": "<root>",
  "owner": null,
  "threads": [
    {
      "tid": "1904",
      "name": "",
      "stack": [
        "java.base\/java.io.PrintStream.format(PrintStream.java:1351)",
        "java.base\/java.io.PrintStream.printf(PrintStream.java:1245)",
        "me.yarosbug.DeadlockTest.lambda$deadlockMe$0(DeadlockTest.java:23)",
        "java.base\/java.util.concurrent.ThreadPerTaskExecutor$TaskRunner.run(ThreadPerTaskExecutor.java:314)",
        "java.base\/java.lang.VirtualThread.run(VirtualThread.java:309)"
      ]
    },
    //...
    {
      //A lot of virtual threads with same stack
      "tid": "*",
      "name": "",
      "stack": [
        "java.base\/java.lang.VirtualThread.park(VirtualThread.java:582)",
        "java.base\/java.lang.System$2.parkVirtualThread(System.java:2639)",
        "java.base\/jdk.internal.misc.VirtualThreads.park(VirtualThreads.java:54)",
        "java.base\/java.util.concurrent.locks.LockSupport.park(LockSupport.java:219)",
        "java.base\/java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:754)",
        "java.base\/java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:990)",
        "java.base\/java.util.concurrent.locks.ReentrantLock$Sync.lock(ReentrantLock.java:153)",
        "java.base\/java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:322)",
        "java.base\/jdk.internal.misc.InternalLock.lock(InternalLock.java:74)",
        "java.base\/java.io.BufferedOutputStream.write(BufferedOutputStream.java:198)",
        "org.apache.maven.surefire.api.util.internal.Channels$4.writeImpl(Channels.java:199)",
        "org.apache.maven.surefire.api.util.internal.AbstractNoninterruptibleWritableChannel.write(AbstractNoninterruptibleWritableChannel.java:66)",
        "org.apache.maven.surefire.api.util.internal.AbstractNoninterruptibleWritableChannel.writeBuffered(AbstractNoninterruptibleWritableChannel.java:49)",
        "org.apache.maven.surefire.api.stream.AbstractStreamEncoder.write(AbstractStreamEncoder.java:69)",
        "org.apache.maven.surefire.booter.spi.EventChannelEncoder.write(EventChannelEncoder.java:288)",
        "org.apache.maven.surefire.booter.spi.EventChannelEncoder.setOutErr(EventChannelEncoder.java:173)",
        "org.apache.maven.surefire.booter.spi.EventChannelEncoder.testOutput(EventChannelEncoder.java:168)",
        "org.apache.maven.surefire.api.booter.ForkingRunListener.writeTestOutput(ForkingRunListener.java:99)",
        "org.apache.maven.surefire.api.booter.ForkingRunListener.writeTestOutput(ForkingRunListener.java:43)",
        "org.apache.maven.surefire.junitplatform.RunListenerAdapter.writeTestOutput(RunListenerAdapter.java:394)",
        "org.apache.maven.surefire.api.report.ConsoleOutputCapture$ForwardingPrintStream.println(ConsoleOutputCapture.java:116)",
        "me.yarosbug.DeadlockTest.lambda$deadlockMe$0(DeadlockTest.java:22)",
        "java.base\/java.util.concurrent.ThreadPerTaskExecutor$TaskRunner.run(ThreadPerTaskExecutor.java:314)",
        "java.base\/java.lang.VirtualThread.run(VirtualThread.java:309)"
      ]
    },
    //...
    {
      "tid": "18215",
      "name": "",
      "stack": [
        "java.base\/jdk.internal.misc.Unsafe.park(Native Method)",
        "java.base\/java.lang.VirtualThread.parkOnCarrierThread(VirtualThread.java:663)",
        "java.base\/java.lang.VirtualThread.park(VirtualThread.java:593)",
        "java.base\/java.lang.System$2.parkVirtualThread(System.java:2639)",
        "java.base\/jdk.internal.misc.VirtualThreads.park(VirtualThreads.java:54)",
        "java.base\/java.util.concurrent.locks.LockSupport.park(LockSupport.java:219)",
        "java.base\/java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:754)",
        "java.base\/java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:990)",
        "java.base\/java.util.concurrent.locks.ReentrantLock$Sync.lock(ReentrantLock.java:153)",
        "java.base\/java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:322)",
        "java.base\/jdk.internal.misc.InternalLock.lock(InternalLock.java:74)",
        "java.base\/java.io.BufferedOutputStream.write(BufferedOutputStream.java:198)",
        "org.apache.maven.surefire.api.util.internal.Channels$4.writeImpl(Channels.java:199)",
        "org.apache.maven.surefire.api.util.internal.AbstractNoninterruptibleWritableChannel.write(AbstractNoninterruptibleWritableChannel.java:66)",
        "org.apache.maven.surefire.api.util.internal.AbstractNoninterruptibleWritableChannel.writeBuffered(AbstractNoninterruptibleWritableChannel.java:49)",
        "org.apache.maven.surefire.api.stream.AbstractStreamEncoder.write(AbstractStreamEncoder.java:69)",
        "org.apache.maven.surefire.booter.spi.EventChannelEncoder.write(EventChannelEncoder.java:288)",
        "org.apache.maven.surefire.booter.spi.EventChannelEncoder.setOutErr(EventChannelEncoder.java:173)",
        "org.apache.maven.surefire.booter.spi.EventChannelEncoder.testOutput(EventChannelEncoder.java:168)",
        "org.apache.maven.surefire.api.booter.ForkingRunListener.writeTestOutput(ForkingRunListener.java:99)",
        "org.apache.maven.surefire.api.booter.ForkingRunListener.writeTestOutput(ForkingRunListener.java:43)",
        "org.apache.maven.surefire.junitplatform.RunListenerAdapter.writeTestOutput(RunListenerAdapter.java:394)",
        "org.apache.maven.surefire.api.report.ConsoleOutputCapture$ForwardingPrintStream.print(ConsoleOutputCapture.java:167)",
        "org.apache.maven.surefire.api.report.ConsoleOutputCapture$ForwardingPrintStream.append(ConsoleOutputCapture.java:179)",
        "org.apache.maven.surefire.api.report.ConsoleOutputCapture$ForwardingPrintStream.append(ConsoleOutputCapture.java:44)",
        "java.base\/java.util.Formatter$FixedString.print(Formatter.java:2876)",
        "java.base\/java.util.Formatter.format(Formatter.java:2780)",
        "java.base\/java.io.PrintStream.implFormat(PrintStream.java:1367)",
        "java.base\/java.io.PrintStream.format(PrintStream.java:1352)",
        "java.base\/java.io.PrintStream.printf(PrintStream.java:1245)",
        "me.yarosbug.DeadlockTest.lambda$deadlockMe$0(DeadlockTest.java:23)",
        "java.base\/java.util.concurrent.ThreadPerTaskExecutor$TaskRunner.run(ThreadPerTaskExecutor.java:314)",
        "java.base\/java.lang.VirtualThread.run(VirtualThread.java:309)"
      ]
    }
  ],
  "threadCount": "98109"
}
```
