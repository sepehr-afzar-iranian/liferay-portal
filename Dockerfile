FROM liferay/portal:7.3.7-ga8

# MAINTAINER="Mahdi Noorbala | noorbla7418@gmail.com" ## Do Not UnCommnet this line!

LABEL Company="SAIN Corp."

COPY --chown=1000:1000 ./SAIN-bundles/bundles/osgi/modules /opt/liferay/osgi/marketplace/override
COPY --chown=1000:1000 SAIN-modules/bundles/osgi/modules/* /opt/liferay/extensions
COPY ./scripts/extension_mover.sh /usr/local/liferay/scripts/pre-configure/