package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = AuditMessageRoleAssociationUtil.class)
public class AuditMessageRoleAssociationUtil {
	public String getName(String associationClassName) {
		if (associationClassName.equals(Group.class.getName())) {
			return "GROUP";
		}
		if (associationClassName.equals(User.class.getName())) {
			return "USER";
		}
		return null;
	}

	public String getValue(String associationClassName, Object associationClassP)
		throws PortalException {
		if (associationClassName.equals(Group.class.getName())) {
			return getGroupName((long)associationClassP);
		}
		if (associationClassName.equals(User.class.getName())) {
			return getUserName((long)associationClassP);
		}
		return null;
	}

	private String getGroupName(long groupId) throws PortalException {
		Group group = _groupLocalService.fetchGroup(groupId);
		if (Validator.isNotNull(group)) {
			return group.getDescriptiveName();
		}
		return null;
	}

	private String getUserName(long userId) throws PortalException {
		User user = _userLocalService.fetchUser(userId);
		if (Validator.isNotNull(user)) {
			return user.getFullName();
		}
		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;
}
