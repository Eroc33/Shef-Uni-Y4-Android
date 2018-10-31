#!/usr/bin/env bash

ANDROID_COMPILE_SDK=$1
ANDROID_BUILD_TOOLS=$2
ANDROID_SDK_TOOLS=$3

apt-get --quiet update --yes
apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1
#perhaps android-sdk-linux is being cached, if so don't download it again
if [ ! -d android-sdk-linux ]; then
	wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_TOOLS}.zip
	unzip -d android-sdk-linux android-sdk.zip
	echo y | android-sdk-linux/tools/bin/sdkmanager "platforms;android-${ANDROID_COMPILE_SDK}" >/dev/null
	echo y | android-sdk-linux/tools/bin/sdkmanager "platform-tools" >/dev/null
	echo y | android-sdk-linux/tools/bin/sdkmanager "build-tools;${ANDROID_BUILD_TOOLS}" >/dev/null
	export ANDROID_HOME=$PWD/android-sdk-linux
	export PATH=$PATH:$PWD/android-sdk-linux/platform-tools/
	# temporarily disable checking for EPIPE error and use yes to accept all licenses
	set +o pipefail
	yes | android-sdk-linux/tools/bin/sdkmanager --licenses
	set -o pipefail
fi
chmod +x ./gradlew