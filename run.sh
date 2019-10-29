#!/bin/bash

echo "compiling..."
javac src/csvreader.java src/Entry.java

echo "executing..."
java csvreader
