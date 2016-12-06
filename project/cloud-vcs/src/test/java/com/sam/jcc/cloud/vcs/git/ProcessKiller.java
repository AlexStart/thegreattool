package com.sam.jcc.cloud.vcs.git;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.sun.jna.platform.win32.WinNT.HANDLE;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.springframework.util.ReflectionUtils.*;

/**
 * @author Alexey Zhytnik
 * @since 05-Dec-16
 */
class ProcessKiller {

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

        process.destroyForcibly();
        killChildren(processes, pid);
    }

    private void killChildren(List<ProcessInfo> processes, int parentPid) {
        processes.stream()
                .filter(byParentPidFilter(parentPid))
                .mapToInt(ProcessInfo::getPid)
                .forEach(pid -> {
                    system.killProcess(pid);
                    killChildren(processes, pid);
                });
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
        return getFieldValue(process, "pid");
    }

    private int getPidInWindows(Process process) {
        final HANDLE handle = new HANDLE();

        long peer = getFieldValue(process, "handle");
        handle.setPointer(Pointer.createConstant(peer));
        return Kernel32.INSTANCE.GetProcessId(handle);
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Process process, String name) {
        final Field field = findField(process.getClass(), name);
        makeAccessible(field);
        return (T) getField(field, process);
    }
}