# indexing_company
This application allow you to fill unique indexing of the certain column


For example:

![image](https://user-images.githubusercontent.com/106987380/187068187-ca467236-8482-4438-a169-532f92178db0.png)

Before:
Unique index | Column referenced    - > 
        null    Tuna
        null    Tuna
        null    Salmon
        null    Salmon
        null    Animal
        null    Flower
        null    Flower
        null    Tuna
        
After:       
Unique index | Column referenced    
        1    Tuna
        1    Tuna
        2    Salmon
        2    Salmon
        3    Animal
        4    Flower
        4    Flower
        1    Tuna

# Notes for future maintanance

remote run on the Digital Ocean 
"nohup gradle bootRun &" - this command allow you to close your droplet window. this does not allow you to perform other commands in the droplet

gradle bootRun &
gradle bootJar & - > java -jar ${name}.jar does not allow you to make offline run

projects -> indexingCompany

jar is placed in build->libs

to find ports (java ones)(need to install):  netstat -tulpn | grep LISTEN
to kill old application: sudo kill -9 $(sudo lsof -t -i:${port})
