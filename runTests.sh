#!/usr/bin/env bash

if [ -d results ]; then
    rm  -rf results
fi

mkdir results

TAB_SIZE=(5 8)
MUT_RATE=0.1
CROSS_RATE=0.8
POP_SIZE=150
ITERATIONS=50000
TIMES=30
for board in ${TAB_SIZE[@]}; do
    for i in $(seq 1 30);do
        echo "running it ${i} to board size ${board}"
        java -jar target/lia.knightstour-1.0-SNAPSHOT.jar --board $board --population $POP_SIZE --crossover $CROSS_RATE --mutation $MUT_RATE --iterations $ITERATIONS --result "results/${board}_${i}.csv"
    done
done
