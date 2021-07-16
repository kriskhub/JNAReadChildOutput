/*
    https://stackoverflow.com/questions/8903510/how-to-get-the-process-output-when-using-jna-and-createprocessw
*/
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public interface Kernel32 extends StdCallLibrary {

    final static Map<String, Object> WIN32API_OPTIONS = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;

        {
            put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
        }
    };

    public Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class, WIN32API_OPTIONS);

    /*
        BOOL WINAPI CreateProcess(
                __in_opt     LPCTSTR lpApplicationName,
                __inout_opt  LPTSTR lpCommandLine,
                __in_opt     LPSECURITY_ATTRIBUTES lpProcessAttributes,
                __in_opt     LPSECURITY_ATTRIBUTES lpThreadAttributes,
                __in         BOOL bInheritHandles,
                __in         DWORD dwCreationFlags,
                __in_opt     LPVOID lpEnvironment,
                __in_opt     LPCTSTR lpCurrentDirectory,
                __in         LPSTARTUPINFO lpStartupInfo,
                __out        LPPROCESS_INFORMATION lpProcessInformation
                );
    */
    public boolean CreateProcess(
            String lpApplicationName,
            String lpCommandLine,
            SECURITY_ATTRIBUTES lpProcessAttributes,
            SECURITY_ATTRIBUTES lpThreadAttributes,
            boolean bInheritHandles,
            DWORD dwCreationFlags,
            Pointer lpEnvironment,
            String lpCurrentDirectory,
            STARTUPINFO lpStartupInfo,
            PROCESS_INFORMATION lpProcessInformation
    );

    public HANDLE GetStdHandle(DWORD nStdHandle);

    public int GetLastError();
}