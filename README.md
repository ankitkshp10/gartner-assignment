# Getting Started

our expected output is the subset of the data provided in clicks.json file. 

How to run -> 
1. change the path of the source file at line 32. set the path as it is placed in your system.
2. change the path of the destination file at line 33. if that file exists, it will update the fileName, otherwise will create a new run. 

3.Run the GartnerAssignmentApplication file. 

->the output file will be generated with expected result set. 

dependencies used :
in pom.xml 
spring-boot-dev-tools - automatically restarts the application when any file changes.
lombok - to get rid of boiler plate code in pojo's.
json - simple - to encode or decode the json data. 
gson - used to convert java objects to json.
