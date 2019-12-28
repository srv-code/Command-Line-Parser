#!/bin/bash

cd out
jar -cvfe0 cmdlnparser.jar Tester Tester.class util
mv -v cmdlnparser.jar ../artifacts
cd ..