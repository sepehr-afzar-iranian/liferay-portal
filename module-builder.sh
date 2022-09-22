#!/bin/sh

../gradlew \
apps:analytics:analytics-message-sender-impl:$1 \
apps:analytics:analytics-settings-web:$1 \
apps:asset:asset-taglib:$1 \
apps:batch-engine:batch-engine-service:$1 \
apps:commerce:commerce-channel-web:$1 \
apps:commerce:commerce-lang:$1 \
apps:commerce:commerce-product-definitions-web:$1 \
apps:data-engine:data-engine-taglib:$1 \
apps:document-library:document-library-item-selector-web:$1 \
apps:dynamic-data-mapping:dynamic-data-mapping-form-field-type:$1 \
apps:dynamic-data-mapping:dynamic-data-mapping-form-report-web:$1 \
apps:dynamic-data-mapping:dynamic-data-mapping-service:$1 \
apps:dynamic-data-mapping:dynamic-data-mapping-web:$1 \
apps:friendly-url:friendly-url-service:$1 \
apps:frontend-editor:frontend-editor-ckeditor-web:$1 \
apps:frontend-js:frontend-js-aui-web:$1 \
apps:frontend-js:frontend-js-react-web:$1 \
apps:frontend-js:frontend-js-web:$1 \
apps:frontend-taglib:frontend-taglib-clay:$1 \
apps:item-selector:item-selector-taglib:$1 \
apps:item-selector:item-selector-web:$1 \
apps:journal:journal-item-selector-web:$1 \
apps:journal:journal-service:$1 \
apps:journal:journal-web:$1 \
apps:layout:layout-impl:$1 \
apps:layout:layout-impl:$1 \
apps:layout:layout-seo-web-test:$1 \
apps:layout:layout-seo-web:$1 \
apps:portal-search-elasticsearch7:portal-search-elasticsearch7-impl:$1 \
apps:portal-search:portal-search-web:$1 \
apps:site:site-browser-web:$1 \
apps:site:site-memberships-web:$1