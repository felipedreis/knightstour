#!/usr/bin/env bash

if [ -d results ]; then
    rm  -rf results
fi

mkdir results

TAB_SIZE=(5 8 16 32 64)
MUT_RATE=0.1
CROSS_RATE=0.8
POP_SIZE=100
ITERATIONS=50000
TIMES=30
for board in ${TAB_SIZE[@]}; do
    for i in $(seq 1 30);do
        echo "running it ${i} to board size ${board}"
        sbatch scriptSub.sh  $board $POP_SIZE $CROSS_RATE $MUT_RATE $ITERATIONS "results/${board}_${i}.csv"
    done
done