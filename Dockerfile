FROM liferay/portal:7.3.7-ga8

# MAINTAINER="Mahdi Noorbala | noorbla7418@gmail.com" ## Do Not UnCommnet this line!

LABEL Company="SAIN Corp."

ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8
ENV DEBIAN_FRONTEND noninteractive
ENV OPENOFFICEVERSION 4.1.14
ENV OPENOFFICEPACKAGE Apache_OpenOffice_4.1.14_Linux_x86-64_install-deb_en-US.tar.gz

RUN mkdir -p "/opt/liferay/extensions"

COPY --chown=1000:1000 ./SAIN-bundles/bundles/osgi/modules/* /opt/liferay/osgi/marketplace/override/
COPY --chown=1000:1000 ./SAIN-bundles/bundles/osgi/portal/com.liferay.* /opt/liferay/osgi/marketplace/override/
COPY --chown=1000:1000 ./SAIN-modules/bundles/osgi/static/* /opt/liferay/osgi/static/
COPY --chown=1000:1000 ./SAIN-modules/bundles/osgi/modules/* /opt/liferay/extensions/
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/webapps/ROOT/WEB-INF/lib/portal-impl.jar /opt/liferay/tomcat-9.0.43/webapps/ROOT/WEB-INF/lib/portal-impl.jar
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/lib/ext/portal-kernel.jar	/opt/liferay/tomcat-9.0.43/lib/ext/portal-kernel.jar
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/webapps/ROOT/WEB-INF/lib/util-taglib.jar	/opt/liferay/tomcat-9.0.43/webapps/ROOT/WEB-INF/lib/util-taglib.jar
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/webapps/ROOT/WEB-INF/liferay-web.xml	/opt/liferay/tomcat-9.0.43/webapps/ROOT/WEB-INF/liferay-web.xml
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/webapps/ROOT/html/taglib/ui/input_date/page.jsp	/opt/liferay/tomcat-9.0.43/webapps/ROOT/html/taglib/ui/input_date/page.jsp
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/webapps/ROOT/html/taglib/ui/input_field/page.jsp	/opt/liferay/tomcat-9.0.43/webapps/ROOT/html/taglib/ui/input_field/page.jsp
COPY --chown=1000:1000 ./SAIN-bundles/bundles/tomcat-9.0.43/webapps/ROOT/html/taglib/ui/page_iterator/start.jsp	/opt/liferay/tomcat-9.0.43/webapps/ROOT/html/taglib/ui/page_iterator/start.jsp
COPY --chown=1000:1000 ./portal-bundle.properties /opt/liferay/
COPY ./scripts/extension_mover.sh /usr/local/liferay/scripts/pre-configure/

USER root

RUN apt-get update
RUN apt-get install -y locales locales-all libxt6 libxrender1 wget

COPY ./fonts/* /usr/share/fonts/

RUN wget -q https://sourceforge.net/projects/openofficeorg.mirror/files/$OPENOFFICEVERSION/binaries/en-US/$OPENOFFICEPACKAGE/download -O /tmp/$OPENOFFICEPACKAGE; \
    mkdir /tmp/OpenOffice; \
    tar -xzf /tmp/$OPENOFFICEPACKAGE -C /tmp/OpenOffice; \
    dpkg -i /tmp/OpenOffice/en-US/DEBS/*.deb; \
    dpkg -i /tmp/OpenOffice/en-US/DEBS/desktop-integration/*.deb; \
    rm -rf /tmp/$OPENOFFICEPACKAGE; \
    rm -rf /tmp/OpenOffice

COPY --chown=1000:1000 ./scripts/start-openoffice.sh /usr/local/liferay/scripts/pre-configure/

RUN mkdir -p /home/openoffice/.local/share
RUN chown -R liferay:liferay /home/openoffice/.local/share