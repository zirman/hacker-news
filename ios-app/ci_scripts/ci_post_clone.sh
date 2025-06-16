#!/bin/sh

curl -s "https://get.sdkman.io?ci=true" | bash
SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"
#sdk install java 21.0.7-jbr
sdk env install
cd ../..
./gradlew dependencies
