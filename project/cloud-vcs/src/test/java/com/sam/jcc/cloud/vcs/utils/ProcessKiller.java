package com.sam.jcc.cloud.vcs.utils;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.sun.jna.platform.win32.WinNT.HANDLE;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.invokeMethod;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * @author Alexey Zhytnik
 * @since 05-Dec-16
 */
@Slf4j
public class ProcessKiller {

    private JavaSysMon system;

    public ProcessKiller() {
        system = new JavaSysMon();

        if (!system.supportedPlatform()) {
            throw new RuntimeException("Unsupported platform");
        }
    }

    /**
     * Kills the Process and it's child-processes.
     *
     * @see <a href="https://github.com/jezhumble/javasysmon">JavaSysMon</a>
     */
    public void kill(Process process) {
        final int pid = getPid(process);

        final List<ProcessInfo> processes = Arrays
                .stream(system.processTable())
                .collect(toList());
        printProcessDump(processes);

        log.info("Process[pid={}] will be killed", pid);
        process.destroyForcibly();

        log.info("Process[pid={}] was killed", pid);
        killChildren(processes, pid);
    }

    private void printProcessDump(List<ProcessInfo> processes) {
        final String dump = processes
                .stream()
                .filter(process -> !process.getName().equals("<unknown>"))
                .map(process ->
                        format("Process[name={0},pid={1},command={2}]",
                                process.getName(),
                                process.getPid(),
                                process.getCommand())
                )
                .collect(joining("\n"));

        log.info(dump);
    }

    private void killChildren(List<ProcessInfo> processes, int parentPid) {
        processes.stream()
                .filter(byParentPidFilter(parentPid))
                .mapToInt(ProcessInfo::getPid)
                .forEach(pid -> {
                    killProcess(pid);
                    killChildren(processes, pid);
                });
    }

    private void killProcess(int pid) {
        try {
            system.killProcess(pid);
        } catch (Exception e) {
            boolean hasInnerKillProblem = e.getMessage().startsWith("Could not kill");

            if (!hasInnerKillProblem) throw e;
            try {
                killProcessInOracle(pid);
            } catch (Exception child) {
                throw e;
            }
        }
        log.info("Child-process[pid={}] was killed", pid);
    }

    /**
     * Fix killing processes for JavaSysMon in Oracle JDK 8.
     */
    private void killProcessInOracle(int pid) throws ClassNotFoundException {
        final Method destroy = findMethod(
                Class.forName("java.lang.UNIXProcess"),
                "destroyProcess",
                int.class,
                boolean.class
        );
        makeAccessible(destroy);
        invokeMethod(destroy, null, pid, true);
    }

    private Predicate<ProcessInfo> byParentPidFilter(int pid) {
        return process -> process.getParentPid() == pid;
    }

    private int getPid(Process process) {
        if (IS_OS_UNIX) return getPidInUnix(process);
        if (IS_OS_WINDOWS) return getPidInWindows(process);

        throw new RuntimeException("Unknown OS: " + system.osName());
    }

    private int getPidInUnix(Process process) {
        return (Integer) getFieldValue(process, "pid");
    }

    private int getPidInWindows(Process process) {
        final HANDLE handle = new HANDLE();

        long peer = (Long) getFieldValue(process, "handle");
        handle.setPointer(Pointer.createConstant(peer));
        return Kernel32.INSTANCE.GetProcessId(handle);
    }

    private Object getFieldValue(Process process, String name) {
        final Field field = findField(process.getClass(), name);
        makeAccessible(field);
        return getField(field, process);
    }
}