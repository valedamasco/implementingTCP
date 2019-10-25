# implementingTCP

Hi there! Here will have some steps to run this program

We have Client-Server , and we need to start the Server first.


Open the project in IntelliJ but the directory Server only
Enter the directory /exampleServer and execute in the command line of the IDE
```
mvn clean install 
java -jar target/{theNameOfYourJar}
```

Then open another IntelliJ IDE but the Client direcory 
Enter to /exampleCliente and execute in the command line of the IDE 
```
mvn clean install 
java -jar target/{theNameOfYourJar}
```

Now is yourking!
Every text you put in the client side, will appear in the server, the client will send a seq number in the begining of the message and the server will send a message back to the client with only that seq number. If the seq number match, will appear a text that the message was recived, if not, the client will send it again. The client only sends the message 4 times. 
