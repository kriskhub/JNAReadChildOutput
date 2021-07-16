/*
    https://stackoverflow.com/questions/8903510/how-to-get-the-process-output-when-using-jna-and-createprocessw
*/
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;


public class RunTest {

    static HANDLEByReference childStdOutRead = new HANDLEByReference();
    static HANDLEByReference childStdOutWrite = new HANDLEByReference();

    static final int HANDLE_FLAG_INHERIT = 0x00000001;

    static final int BUFSIZE = 4096;

    private static final DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
    private static final int STARTF_USESTDHANDLES = 0x00000100;

    static PROCESS_INFORMATION processInformation = new PROCESS_INFORMATION();

    static void createChildProcess(String executable, String cmd, String username, String password, String domain){
        WString nullW = null;
        STARTUPINFO startupInfo = new STARTUPINFO();
        startupInfo.cb = new DWORD(processInformation.size());
        startupInfo.hStdError = childStdOutWrite.getValue();
        startupInfo.hStdOutput = childStdOutWrite.getValue();
        startupInfo.dwFlags |= STARTF_USESTDHANDLES;

        String commands = executable + cmd;
        // Create the child process.
        boolean result = MoreAdvApi32.INSTANCE.CreateProcessWithLogonW
                (new WString(username),                         // user
                 domain == "" ? nullW : new WString(domain),                            // domain , null if local
                new WString(password),                         // password
                MoreAdvApi32.LOGON_WITH_PROFILE,                 // dwLogonFlags
                new WString(executable),                     // lpApplicationName
                new WString(commands),                           // command line
                MoreAdvApi32.CREATE_NEW_CONSOLE,                 // dwCreationFlags
                null,                              // lpEnvironment
                new WString("C:\\temp"),                   // directory
                startupInfo,
                processInformation
                );
        if (!result){
            System.err.println(Kernel32.INSTANCE.GetLastError());
        }
        else {
            com.sun.jna.platform.win32.Kernel32.INSTANCE.WaitForSingleObject(processInformation.hProcess, 0xFFFFFFFF);
            com.sun.jna.platform.win32.Kernel32.INSTANCE.CloseHandle(processInformation.hProcess);
            com.sun.jna.platform.win32.Kernel32.INSTANCE.CloseHandle(processInformation.hThread);
        }
    }


    static void ReadFromPipe()

    // Read output from the child process's pipe for STDOUT
    // and write to the parent process's pipe for STDOUT.
    // Stop when there is no more data.
    {
        IntByReference dwRead = new IntByReference();
        IntByReference dwWritten = new IntByReference();
        byte[] readBuffer = new byte[BUFSIZE];
        boolean bSuccess = true;
        HANDLE hParentStdOut = Kernel32.INSTANCE.GetStdHandle(STD_OUTPUT_HANDLE);

        // The pipe is assumed to have enough buffer space to hold the
        // data the child process has already written to it.

        if (!com.sun.jna.platform.win32.Kernel32.INSTANCE.CloseHandle(childStdOutWrite.getValue())){
            System.err.println(Kernel32.INSTANCE.GetLastError());
        }

        for (;;)
        {
            bSuccess = com.sun.jna.platform.win32.Kernel32.INSTANCE.ReadFile( childStdOutRead.getValue(), readBuffer, BUFSIZE, dwRead, null);
            if( ! bSuccess || dwRead.getValue() == 0 ) break;

            bSuccess = com.sun.jna.platform.win32.Kernel32.INSTANCE.WriteFile(hParentStdOut, readBuffer, dwRead.getValue(), dwWritten, null);
            if (! bSuccess ) break;
        }
    }

    public static void main(String[] args) {
        // Interpret argument list as command, username, password and domain

        String executable = "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe";
        String cmd = " -ExecutionPolicy Bypass " + "-File  \"C:\\git\\JNAReadChildOutput\\script1.ps1\"  -ComputerName  \"www.google.de\"";
        String username = "";
        String password = "";
        String domain = "";

        if (args.length < 4) {
            System.err.println("Please specify command, username, password and domain (optional).\n");
            System.exit(1);
        }

        if (args.length == 4) {
            executable = args[0];
            cmd = args[1];
            username = args[2];
            password = args[3];
            domain = "";

        } else if (args.length == 5) {
            executable = args[0];
            cmd = args[1];
            username = args[2];
            password = args[3];
            domain = args[4];
        }

        SECURITY_ATTRIBUTES saAttr = new SECURITY_ATTRIBUTES();
        saAttr.dwLength = new DWORD(saAttr.size());
        saAttr.bInheritHandle = true;
        saAttr.lpSecurityDescriptor = null;

        // Create a pipe for the child process's STDOUT.
        if (!com.sun.jna.platform.win32.Kernel32.INSTANCE.CreatePipe(childStdOutRead, childStdOutWrite, saAttr, 0)){
            System.err.println(Kernel32.INSTANCE.GetLastError());
        }

        // Ensure the read handle to the pipe for STDOUT is not inherited.
        if (!com.sun.jna.platform.win32.Kernel32.INSTANCE.SetHandleInformation(childStdOutRead.getValue(), HANDLE_FLAG_INHERIT, 0)){
            System.err.println(Kernel32.INSTANCE.GetLastError());;
        }

        createChildProcess(executable, cmd, username, password, domain);


        // Read from pipe that is the standard output for child process.

        System.out.println( "\n->Contents of child process STDOUT:\n\n");
        ReadFromPipe();

        System.out.println("\n->End of parent execution.\n");

        // The remaining open handles are cleaned up when this process terminates. 
        // To avoid resource leaks in a larger application, close handles explicitly. 

    }

}