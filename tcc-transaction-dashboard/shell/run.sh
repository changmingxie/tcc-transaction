#!/usr/bin/env bash

WORK_DIR=$(
  cd $(dirname $0)
  pwd
)

## 应用名
APP='tcc-transaction-dashboard'

LOG_DIR=/var/log/$APP
TD=$WORK_DIR/temp
pid=$WORK_DIR/pid/a.pid
lx=$LOG_DIR/n.out

mkdir -p "$WORK_DIR/pid"
mkdir -p "$LOG_DIR"
mkdir -p "$TD"

JAVA=/usr/local/jdk8/bin/java

JAVA_OPTS="-server
  -Xmx4g
  -Xms4g
  -Xmn2g
  -XX:+UseG1GC
  -XX:+UseGCLogFileRotation
  -XX:NumberOfGCLogFiles=10
  -XX:GCLogFileSize=10M
  -XX:+PrintGCApplicationStoppedTime
  -XX:+PrintGCApplicationConcurrentTime
  -XX:+PrintGCDetails
  -XX:+PrintGCDateStamps
  -XX:+DisableExplicitGC
  -XX:+HeapDumpOnOutOfMemoryError
  -javaagent:/opt/jars/aspectjweaver-1.8.9.jar
  -Xloggc:${CURRENT_DIR}/logs/gc.log
  -Djava.io.tmpdir=$TD"

echo $JAVA_OPTS >>$lx

if [ "$1" = "start" ]; then

  if [ -f $pid ]; then
    if kill -0 $(cat $pid) >/dev/null 2>&1; then
      echo $(date) server running as process $(cat $pid). stop it first. >>$lx
      exit 1
    fi
  fi
  nohup "$JAVA" $JAVA_OPTS -jar "$WORK_DIR/$APP.jar" "$@" >>"$lx" 2>&1 </dev/null &
  echo $! >$pid

elif [ "$1" = "stop" ]; then
  if [ -f $pid ]; then
    # kill -0 == see if the PID exists
    if kill -0 $(cat $pid) >/dev/null 2>&1; then
      kill -15 $(cat $pid) >/dev/null 2>&1
      while kill -0 $(cat $pid) >/dev/null 2>&1; do
        echo "." >>$lx
        sleep 1
      done
      rm $pid
      echo "shutdown success" >>$lx
    else
      retval=$?
      echo no $command to stop because kill -0 of pid $(cat $pid) failed with status $retval >>$lx
    fi
  else
    echo no $command to stop because no pid file $pid
  fi
else
  echo "  start             start titan in a separate window"
  echo "  stop              stop titan, waiting up to 5 seconds for the process to end"

fi
