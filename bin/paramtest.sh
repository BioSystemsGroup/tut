#!/bin/bash
java -cp ".:dist/tut.jar"$(unset cp;for i in lib/*.jar; do cp=$cp":"$i; done; echo $cp) tut.ctrl.Parameters cfg/parameters.json
exit

