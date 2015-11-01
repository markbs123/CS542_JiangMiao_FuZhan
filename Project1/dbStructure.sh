#!/bin/bash
File=/Users/bin/cs542.db
while read File
do
echo $File
done < $File