/*
    https://www.rgagnon.com/javadetails/java-start-process-as-another-user-using-jna.html
*/
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;


public interface MoreAdvApi32 extends Advapi32 {
    MoreAdvApi32 INSTANCE =
            (MoreAdvApi32) Native.loadLibrary("AdvApi32", MoreAdvApi32.class);

    /*
     * BOOL WINAPI CreateProcessWithLogonW( __in LPCWSTR lpUsername,
     * __in_opt LPCWSTR lpDomain, __in LPCWSTR lpPassword, __in DWORD
     * dwLogonFlags, __in_opt LPCWSTR lpApplicationName, __inout_opt LPWSTR
     * lpCommandLine, __in DWORD dwCreationFlags, __in_opt LPVOID
     * lpEnvironment, __in_opt LPCWSTR lpCurrentDirectory, __in
     * LPSTARTUPINFOW lpStartupInfo, __out LPPROCESS_INFORMATION
     * lpProcessInfo );
     */

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms682431%28v=vs.85%29.aspx
    boolean CreateProcessWithLogonW
    (WString lpUsername,
     WString lpDomain,
     WString lpPassword,
     int dwLogonFlags,
     WString lpApplicationName,
     WString lpCommandLine,
     int dwCreationFlags,
     Pointer lpEnvironment,
     WString lpCurrentDirectory,
     STARTUPINFO  lpStartupInfo,
     PROCESS_INFORMATION lpProcessInfo);

    public static final int LOGON_WITH_PROFILE          = 0x00000001;
    public static final int LOGON_NETCREDENTIALS_ONLY   = 0x00000002;


    int CREATE_NO_WINDOW            = 0x08000000;
    int CREATE_UNICODE_ENVIRONMENT  = 0x00000400;
    int CREATE_NEW_CONSOLE          = 0x00000010;
    int DETACHED_PROCESS            = 0x00000008;
}