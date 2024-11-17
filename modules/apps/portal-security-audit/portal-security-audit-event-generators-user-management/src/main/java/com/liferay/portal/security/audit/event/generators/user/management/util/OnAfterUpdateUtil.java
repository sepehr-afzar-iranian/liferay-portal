package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalServiceUtil;

import java.util.List;

public class OnAfterUpdateUtil {

    public static void update(String className, long id) {
        update(className, id, EventTypes.UPDATE);
    }

    public static void update(String className, long id, String eventType) {
        try {
            DynamicQuery dynamicQuery = AuditEventLocalServiceUtil.dynamicQuery()
                    .add(PropertyFactoryUtil.forName("className").eq(className))
                    .add(PropertyFactoryUtil.forName("classPK").eq(String.valueOf(id)))
                    .add(PropertyFactoryUtil.forName("eventType").eq(eventType))
                    .addOrder(OrderFactoryUtil.desc("createDate"));

            List<AuditEvent> auditEvents = AuditEventLocalServiceUtil.dynamicQuery(dynamicQuery, 0, 1);

            if (!auditEvents.isEmpty()) {
                AuditEvent auditEvent = auditEvents.get(0);
                auditEvent.setEventType(eventType + "_AFTER");
                AuditEventLocalServiceUtil.updateAuditEvent(auditEvent);
            }
        }
        catch (Exception exception) {
            if (_log.isWarnEnabled()) {
                _log.warn("Unable to update audit message", exception);
            }
        }
    }

    private static final Log _log = LogFactoryUtil.getLog(
            OnAfterUpdateUtil.class);
}
