# NPMANCILLARY EXTENSION - NPM

## *Unix

**Prerequisites**

Install the following:
- Python 2.7
- make
- g++

## Windows

**Prerequisites**

To run npm install without on windows servers without errors, you need to install the following:

- Java JDK - add JAVA_HOME to environment variables
- Maven (see https://wiki.hybris.com/display/release5/How+To+Configure+Maven)
- node + npm - add to PATH
- git - add to PATH
- Python 2.7 - add to PATH
- 7z open source zip tool - add to PATH
- Microsoft Visual C++ 2010 Redistributable Package (x64) https://www.microsoft.com/en-ca/download/details.aspx?id=14632
- Microsoft Windows SDK for Windows
- Install all the required tools and configurations using Microsoft’s windows-build-tools using: "npm install --global --production windows-build-tools"
- Run your script from Powershell, not Command prompt.

Node: 
- if you see an error like "Missing POM for de.hybris.platform:npm-ancillary-module:zip:19.05.0-RC5" probably you are using a wrong maven repo. 
  - go to https://repository.hybris.com/hybris-repository/settings.xml
  - put generated settings.xml in .m2 folder under your user name (for example, C:\Users\admin\.m2)
  - restart the process again.

Source: https://github.com/nodejs/node-gyp/wiki/Visual-Studio-2010-Setuphttps://repository.hybris.com/hybris-repository/settings.xml
