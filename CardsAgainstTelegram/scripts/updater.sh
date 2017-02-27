#!/bin/bash
cd ~
git clone git@bitbucket.org:jrh379-drexel/team03-drexel-16.git
cd team03-drexel-16
git pull
cd CardsAgainstTelegram
mvn clean package
cp -f jar/CardsAgainstTelegram.jar ~/CardsAgainstTelegram.jar
touch ~/update_available

