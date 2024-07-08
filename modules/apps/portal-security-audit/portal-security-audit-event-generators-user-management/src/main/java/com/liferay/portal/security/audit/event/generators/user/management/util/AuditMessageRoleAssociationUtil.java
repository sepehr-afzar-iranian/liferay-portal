package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Objects;

/**
 * @author Yousef Ghadiri
 */
@Component(service = AuditMessageRoleAssociationUtil.class)
public class AuditMessageRoleAssociationUtil {
	public String getName(String className) {
		String[] classPath = className.split("\\.");
		String name = classPath[classPath.length-1];
		if (Objects.equals(name, "Group")) {
			return "GROUP";
		}
		if (Objects.equals(name, "User")) {
			return "USER";
		}
		return "";
	}

	public String getValue(String className, Object classP)
		throws PortalException {
		String[] classPath = className.split("\\.");
		String name = classPath[classPath.length-1];
		if (Objects.equals(name, "Group")) {
			return getGroupName((long)classP);
		}
		if (Objects.equals(name, "User")) {
			return getUserName((long)classP);
		}
		return "";
	}

	private String getGroupName(long groupId) throws PortalException {
		return _groupLocalService.getGroup(groupId).getDescriptiveName();
	}

	private String getUserName(long userId) throws PortalException {
		return _userLocalService.getUser(userId).getFullName();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;
}
