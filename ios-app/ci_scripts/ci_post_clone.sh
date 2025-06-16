#!/bin/sh

cd ../..
curl -s "https://get.sdkman.io?ci=true" | bash
SDKMAN_DIR="$HOME/.sdkman"
"./$SDKMAN_DIR/bin/sdkman-init.sh"
sdk env install
./gradlew dependencies
