#!/bin/bash
# Get the latest changes
cd ~
git clone git@bitbucket.org:jrh379-drexel/team03-drexel-16.git
cd team03-drexel-16
git pull
# General Bot
cd CardsAgainstTelegram
mvn clean package
cp -f jar/CardsAgainstTelegram.jar ~/CardsAgainstTelegram.jar
touch ~/update_available_cah
# Presentation
cd ~/team03-drexel-16/Presentation
mvn clean package
cp -f jar/Presentation.jar ~/Presentation.jar
touch ~/update_available_pres


