This is a Gradle project with three sub-projects:

1. **loan-client** application will create a LoanRequest object, convert it to a JSON string and send the JSON string via a message to a queue (e.g., myFirstQueue). You can run this application with the **run** task.
2. **bank** application will receive messages from the queue, deserialize JSON string to BankRequest and show it in GUI. You can run this application with the **run** task.
3. **shared** is a module where you can find (but also add) some code which will be shared by other applications (see dependencies in build.gradle in loan-client and bank).

Tips: 

1. If you want to be able to run multiple applications (e.g. loan-client and bank), 
you can add a new Run Configuration od the type **Compund** or **Multirun**, and then add an arbitrary number of **run** tasks to it.
   
2. If you want to run a Run Configuration multiple times in parallel, you must check **Allow parallel run** chek-box in the Run Configuration.