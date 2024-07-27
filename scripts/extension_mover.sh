#!/bin/sh

cp /opt/liferay/extensions/*.jar /opt/liferay/osgi/modules
cp /opt/liferay/extensions/*.war /opt/liferay/osgi/war
rm -Rf /opt/liferay/osgi/marketplace/Liferay\ CE\ Static\ -\ *
rm -Rf /opt/liferay/osgi/state
rm -Rf /opt/liferay/tomcat-9.0.43/work/*
rm -Rf /opt/liferay/tomcat-9.0.43/temp/*