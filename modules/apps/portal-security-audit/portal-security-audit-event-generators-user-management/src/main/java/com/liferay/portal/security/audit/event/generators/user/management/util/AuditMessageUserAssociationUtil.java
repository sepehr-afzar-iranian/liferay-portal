package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;

import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = AuditMessageUserAssociationUtil.class)
public class AuditMessageUserAssociationUtil {
	public String getName(String associationClassName) {
		if (associationClassName.equals(Role.class.getName())) {
			return "REGULAR ROLE";
		}
		if (associationClassName.equals(Organization.class.getName())) {
			return "ORGANIZATION";
		}
		if (associationClassName.equals(Group.class.getName())) {
			return "SITE";
		}
		if (associationClassName.equals(UserGroup.class.getName())) {
			return "USER GROUP";
		}
		if (associationClassName.equals(Team.class.getName())) {
			return "TEAM";
		}
		return null;
	}
	public String getValue(String associationClassName, Object associationclassP)
		throws PortalException {
		if (associationClassName.equals(Role.class.getName())) {
			return getRoleName((long)associationclassP);
		}
		if (associationClassName.equals(Organization.class.getName())) {
			return getOrganiztionName((long)associationclassP);
		}
		if (associationClassName.equals(Group.class.getName())) {
			return getGroupName((long)associationclassP);
		}
		if (associationClassName.equals(UserGroup.class.getName())) {
			return getUserGroupName((long)associationclassP);
		}
		if (associationClassName.equals(Team.class.getName())) {
			return getTeamName((long)associationclassP);
		}
		return null;
	}

	private String getRoleName(long roleId) throws PortalException {
		Role role = _roleLocalService.fetchRole(roleId);
		if (Validator.isNotNull(role)) {
			return role.getName();
		}
		return null;
	}

	private String getOrganiztionName(long organizationId) throws PortalException {
		Organization organization =
			_organizationLocalService.fetchOrganization(organizationId);
		if (Validator.isNotNull(organization)) {
			return organization.getName();
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

	private String getUserGroupName(long userGroupId) throws PortalException {
		UserGroup userGroup = _userGroupLocalService.fetchUserGroup(userGroupId);
		if (Validator.isNotNull(userGroup)) {
			return userGroup.getName();
		}
		return null;
	}

	private String getTeamName(long teamId) throws PortalException {
		Team team = _teamLocalService.fetchTeam(teamId);
		if (Validator.isNotNull(team)) {
			return team.getName();
		}
		return null;
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
