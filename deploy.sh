#!/usr/bin/env bash

mvn package -Dmaven.test.skip=true
scp target/meow-album-0.0.1-SNAPSHOT.jar  tokyo:~/
