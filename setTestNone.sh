#!/bin/bash

sed -i.bak -e 's/^\/\/ *@org.junit.Ignore/@org.junit.Ignore/g' src/test/java/jcomicdownloader/*.java
