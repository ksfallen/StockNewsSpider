#!/bin/sh
set encoding=utf-8
cp=.:${CLASSPATH}
for filename in `ls ./lib/*.jar`
do
cp=${cp}:${filename}
done

java -cp ${cp} com.cairenhui.news.spider.startUp.StartUp -Xmx2048M &