#!/bin/bash
set -e

# Install OpenCode CLI
curl -fsSL https://opencode.ai/install | bash

# Add your additional commands here
curl --compressed https://static.snyk.io/cli/latest/snyk-linux-arm64 -o snyk
chmod +x ./snyk
sudo mv -f ./snyk /usr/local/bin/snyk