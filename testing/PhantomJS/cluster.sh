#!/bin/bash
#
# Submits a number of jobs to Sun grid engine (sge/qsub), a
# scheduler often used in Rocks environments.
#
# Note the -v and the USERNAME and PASSWORD args. These are a
# way to pass environment variables to the script being scheudled
# to run.
#
# @author DMcG

for idx in {1..40}
do
  echo Submitting load test session $idx
  qsub -v USERNAME="bot${idx},PASSWORD="bot${idx} /home/mcgredo/GhostDriver/individualRun.sh 
  sleep 5
done
