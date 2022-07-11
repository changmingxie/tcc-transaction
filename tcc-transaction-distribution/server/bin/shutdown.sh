#!/bin/bash

cd `dirname $0`/../lib
target_dir=`pwd`

pid=`ps ax | grep -i 'tcc.server' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No tcc-transaction-server running."
        exit -1;
fi

echo "The tcc-transaction-server(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to tcc-transaction-server(${pid}) OK"
