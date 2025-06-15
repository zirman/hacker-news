#!/bin/sh

curl -s "https://get.sdkman.io?ci=true" | bash
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.7-jbr
#sdk env
