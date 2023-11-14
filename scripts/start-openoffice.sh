#!/bin/bash

if [ -d /home/openoffice/.openoffice ]; then
chown -R liferay:liferay /home/openoffice/.openoffice
fi

/usr/bin/soffice -accept="socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -norestore -nofirststartwizard -nologo -headless -conversionmode &