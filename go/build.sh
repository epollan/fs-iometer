#!/bin/bash

set -o errexit

pushd `dirname $0` &> /dev/null

GOPATH=`pwd`
go install iometer

popd &> /dev/null
