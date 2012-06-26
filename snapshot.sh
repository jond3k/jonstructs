#!/bin/bash

base=../jond3k-mvn-repo
snapshot=$base/snapshots
snapshotrepo=snapshot-repo

mvn -DaltDeploymentRepository=$snapshotrepo::default::file:$snapshot clean deploy

