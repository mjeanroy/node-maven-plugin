#!/bin/bash

echo "${PATH}"

if [[ ! -z "${TEST_ENV_VAR}" ]]; then
  echo "${TEST_ENV_VAR}"
fi

exit 0
