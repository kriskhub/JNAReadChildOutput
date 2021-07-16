Java Native Access Read Child Output

# Description
This repository gives an example how to use the Java Native Access Library to read the stdout put of a child process. In this particular example we are starting a powershell script as a child process in an specific user context. 

# How-To
* Argument 0: Path to the executable
* Argument 1: Commands that should be passed to the process
* Argument 2: Username
* Argument 3: Password
* (Optional) Argument 4: Domain (local if null)

### Example

Parameters:

*  ``"C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe"``
* ``" -ExecutionPolicy Bypass -File  C:\\git\\JNAReadChildOutput\\script1.ps1  -ComputerName  www.google.de"``  
* ```"cnorris"``` 
* ``"Norris1940"``
* 


# Resources:

https://github.com/java-native-access/jna

https://www.rgagnon.com/javadetails/java-start-process-as-another-user-using-jna.html

https://stackoverflow.com/questions/8903510/how-to-get-the-process-output-when-using-jna-and-createprocessw
