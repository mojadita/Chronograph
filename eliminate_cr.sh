#!/bin/sh

set -v

find src -type f | while read fn
do
    ex $fn <<EOF
%s///
w
EOF

    detab $fn
done
