

Hello World for MongoDB Java Driver 3.5.

All required dependencies for MongoDB Driver, MongoDB Core and BSON Library are stored in com/* and org/* directories.

use "javac Hello.java" to compile
use "java Hello" to run

This program connects to MongoDB Server, Running on localhost:27017, and adds {Hello:"World",Date:<Current Date in ISO>} document to test.test collecction, and lists all documents in this collection. Also, program prints its count.