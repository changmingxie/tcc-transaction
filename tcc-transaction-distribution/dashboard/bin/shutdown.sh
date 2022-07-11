#!/bin/bash

cd `dirname $0`/../lib
target_dir=`pwd`

pid=`ps ax | grep -i 'tcc.dashboard' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No tcc-transaction-dashboard running."
        exit -1;
fi

echo "The tcc-transaction-dashboard(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to tcc-transaction-dashboard(${pid}) OK"
