COMP2601-A Assignment 2
Due Date: February 26, 2017 (11:55pm)

Authors: Alexei Tipenko (100995947)
              Avery Vine (100999500)

Program: Program is a social android application of tic-tac-toe that uses a server. 

Testing instructions:
1) Open COMP2601A2.
2) Right-click on “edu.carleton.COMP2601.com2601a2(test) folder
3) Select “Run ‘Tests in a2””


Operating instructions:
1) Right-click on “MainActivity” class
2) Select “Run ‘MainActivity’”
3) To compile and run the server:

	1) Open the Terminal.
	2) Navigate to the project folder. 
	3) Navigate to /app/src/main/java.
	4) javac -cp ".:./edu/carleton/COMP2601/comp2601a2/json-simple-1.1.jar" edu/carleton/COMP2601/comp2601a2/Server.java

		(This is to ensure that the Server.java file can access and use JSON)

	5) java -cp ".:./edu/carleton/COMP2601/comp2601a2/json-simple-1.1.jar" edu.carleton.COMP2601.comp2601a2.Server

		(This is for running the actual server)


