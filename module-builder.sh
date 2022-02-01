#!/bin/sh

../gradlew \
	 apps:document-library:document-library-item-selector-web:deploy \
	 apps:dynamic-data-mapping:dynamic-data-mapping-form-field-type:deploy \
	 apps:dynamic-data-mapping:dynamic-data-mapping-web:deploy \
	 apps:frontend-js:frontend-js-aui-web:deploy \
	 apps:frontend-js:frontend-js-react-web:deploy \
	 apps:frontend-js:frontend-js-web:deploy \
	 apps:frontend-taglib:frontend-taglib-clay:deploy \
	 apps:item-selector:item-selector-taglib:deploy \
	 apps:item-selector:item-selector-web:deploy \
	 apps:journal:journal-item-selector-web:deploy \
	 apps:journal:journal-service:deploy \
	 apps:journal:journal-web:deploy