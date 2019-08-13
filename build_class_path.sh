#!/bin/bash

TARGET=./opentracing_jars

CLASSPATH=""
for jar in $(ls $TARGET/*.jar);  do
  CLASSPATH=$CLASSPATH:$jar
done

echo "classpath is"
echo $CLASSPATH

