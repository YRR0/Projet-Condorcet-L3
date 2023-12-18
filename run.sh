#!/bin/bash

# Compilation
javac ElectionSimulator.java

# Exécution
java ElectionSimulator

# Suppression des fichiers .class
find . -name "*.class" -type f -delete

