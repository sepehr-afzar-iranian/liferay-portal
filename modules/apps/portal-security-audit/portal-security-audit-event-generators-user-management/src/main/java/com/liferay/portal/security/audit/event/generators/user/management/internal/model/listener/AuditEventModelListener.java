package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.*;

import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.security.audit.event.generators.constants.AuditConstants;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = ModelListener.class)
public class AuditEventModelListener extends BaseModelListener<AuditEvent> {

    private static final int AUDIT_THRESHOLD = 1000;
    private long auditCounter = 0;

    @Activate
    public void activate() {
        try {
            auditCounter = _auditEventLocalService.getAuditEventsCount();
        } catch (Exception e) {
            System.out.println("Error initializing counters: " + e.getMessage());
        }
    }

    @Override
    public void onAfterCreate(AuditEvent auditEvent) throws ModelListenerException {
        try {
            auditCounter++;
            if (auditCounter == AUDIT_THRESHOLD || (auditCounter > AUDIT_THRESHOLD && auditCounter % AUDIT_THRESHOLD == 0)) {
                _sendNotificationToInstanceAdministrators(auditEvent.getCompanyId(), auditCounter);
            }
        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }

    private void _sendNotificationToInstanceAdministrators(long companyId, long auditCounter)
            throws PortalException {
        Role role = _roleLocalService.getRole(
                companyId, RoleConstants.ADMINISTRATOR);
        JSONObject payload = JSONFactoryUtil.createJSONObject();
        payload.put("counter", auditCounter);

        for (User user : _userLocalService.getRoleUsers(role.getRoleId())) {
            _userNotificationEventLocalService.sendUserNotificationEvents(user.getUserId(), AuditConstants.AUDIT_THRESHOLD_NOTIFICATION, UserNotificationDeliveryConstants.TYPE_WEBSITE, payload);
        }
    }

    @Reference
    private RoleLocalService _roleLocalService;

    @Reference
    private UserLocalService _userLocalService;

    @Reference
    private UserNotificationEventLocalService _userNotificationEventLocalService;

    @Reference
    private AuditEventLocalService _auditEventLocalService;
}