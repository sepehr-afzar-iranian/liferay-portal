package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Objects;

/**
 * @author Yousef Ghadiri
 */
@Component(service = AuditMessageUserAssociationUtil.class)
public class AuditMessageUserAssociationUtil {
	public String getName(String className) {
		String[] classPath = className.split("\\.");
		String name = classPath[classPath.length-1];
		if (Objects.equals(name, "Role")) {
			return "REGULAR ROLE";
		}
		if (Objects.equals(name, "Organization")) {
			return "ORGANIZATION";
		}
		if (Objects.equals(name, "Group")) {
			return "SITE";
		}
		if (Objects.equals(name, "UserGroup")) {
			return "USER GROUP";
		}
		if (Objects.equals(name, "Team")) {
			return "TEAM";
		}
		return "";
	}
	public String getValue(String className, Object classP)
		throws PortalException {
		String[] classPath = className.split("\\.");
		String name = classPath[classPath.length-1];
		if (Objects.equals(name, "Role")) {
			return getRoleName((long)classP);
		}
		if (Objects.equals(name, "Organization")) {
			return getOrganiztionName((long)classP);
		}
		if (Objects.equals(name, "Group")) {
			return getGroupName((long)classP);
		}
		if (Objects.equals(name, "UserGroup")) {
			return getUserGroupName((long)classP);
		}
		if (Objects.equals(name, "Team")) {
			return getTeamName((long)classP);
		}
		return "";
	}

	private String getRoleName(long roleId) throws PortalException {
		return _roleLocalService.getRole(roleId).getName();
	}

	private String getOrganiztionName(long organizationId) throws PortalException {
		return _organizationLocalService.getOrganization(organizationId).getName();
	}

	private String getGroupName(long groupId) throws PortalException {
		return _groupLocalService.getGroup(groupId).getDescriptiveName();
	}

	private String getUserGroupName(long userGroupId) throws PortalException {
		return _userGroupLocalService.getUserGroup(userGroupId).getName();
	}

	private String getTeamName(long teamId) throws PortalException {
		return _teamLocalService.getTeam(teamId).getName();
	}

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private TeamLocalService _teamLocalService;
}
