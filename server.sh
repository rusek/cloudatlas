#!/bin/sh
#
# Distributed Systems Lab
# Copyright (C) Konrad Iwanicki, 2012-2014
#
# This file contains code samples for the distributed systems
# course. It is intended for internal use only.
#

java -Djava.rmi.server.codebase=file:/home/iwanicki/rmi/mine/ -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy server.ZMIServer
