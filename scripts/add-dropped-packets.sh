#!/bin/bash
sudo tc qdisc add dev lo root netem loss 10.0%
echo "sudo tc qdisc add dev lo root netem loss 10.0%"
