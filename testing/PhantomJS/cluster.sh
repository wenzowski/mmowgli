#!/bin/bash
 
for idx in {1..40}
do
  echo Submitting load test session $idx
  qsub /home/mcgredo/GhostDriver/individualRun.sh 
  sleep 2 
done
