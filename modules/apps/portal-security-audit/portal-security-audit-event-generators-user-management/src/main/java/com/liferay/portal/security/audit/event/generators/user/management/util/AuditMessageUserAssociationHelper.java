/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

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

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = AuditMessageUserAssociationHelper.class)
public class AuditMessageUserAssociationHelper {

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

	public String getValue(String associationClassName, long associationclassP)
		throws PortalException {

		if (associationClassName.equals(Role.class.getName())) {
			return _getRoleName(associationclassP);
		}

		if (associationClassName.equals(Organization.class.getName())) {
			return _getOrganiztionName(associationclassP);
		}

		if (associationClassName.equals(Group.class.getName())) {
			return _getGroupName(associationclassP);
		}

		if (associationClassName.equals(UserGroup.class.getName())) {
			return _getUserGroupName(associationclassP);
		}

		if (associationClassName.equals(Team.class.getName())) {
			return _getTeamName(associationclassP);
		}

		return null;
	}

	private String _getGroupName(long groupId) throws PortalException {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (!Objects.equals(group, null)) {
			return group.getDescriptiveName();
		}

		return null;
	}

	private String _getOrganiztionName(long organizationId) {
		Organization organization = _organizationLocalService.fetchOrganization(
			organizationId);

		if (!Objects.equals(organization, null)) {
			return organization.getName();
		}

		return null;
	}

	private String _getRoleName(long roleId) {
		Role role = _roleLocalService.fetchRole(roleId);

		if (!Objects.equals(role, null)) {
			return role.getName();
		}

		return null;
	}

	private String _getTeamName(long teamId) {
		Team team = _teamLocalService.fetchTeam(teamId);

		if (!Objects.equals(team, null)) {
			return team.getName();
		}

		return null;
	}

	private String _getUserGroupName(long userGroupId) {
		UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
			userGroupId);

		if (!Objects.equals(userGroup, null)) {
			return userGroup.getName();
		}

		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TeamLocalService _teamLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}