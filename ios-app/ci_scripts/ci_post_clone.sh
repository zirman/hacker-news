#!/bin/zsh

cd ../..
curl -s "https://get.sdkman.io?ci=true" | zsh
SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$SDKMAN_DIR/bin/sdkman-init.sh" ]] && source "$SDKMAN_DIR/bin/sdkman-init.sh"
sdk env install
./gradlew dependencies
