#!/usr/bin/env bash

mvn package
scp target/meow-album-0.0.1-SNAPSHOT.jar  tokyo:~/
