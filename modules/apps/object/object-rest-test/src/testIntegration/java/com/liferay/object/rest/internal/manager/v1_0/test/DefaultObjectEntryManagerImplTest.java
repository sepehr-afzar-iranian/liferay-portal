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

package com.liferay.object.rest.internal.manager.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFilterConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.AggregationObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.Link;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.petra.sql.dsl.expression.FilterPredicateFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.util.LocalizedMapUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.math.BigDecimal;
import java.math.MathContext;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class DefaultObjectEntryManagerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_companyId = TestPropsValues.getCompanyId();
		_group = GroupTestUtil.addGroup();
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		_adminUser = TestPropsValues.getUser();

		_simpleDTOConverterContext = new DefaultDTOConverterContext(
			false, Collections.emptyMap(), _dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, _adminUser);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_adminUser));

		PrincipalThreadLocal.setName(_adminUser.getUserId());

		PropsUtil.addProperties(
			UnicodePropertiesBuilder.setProperty(
				"feature.flag.LPS-164801", "true"
			).build());
	}

	@Before
	public void setUp() throws Exception {
		_objectDefinition1 = _createObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build()));

		_setUpDTOConverterContext();

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, _adminUser.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()));

		_objectDefinition2 = _createObjectDefinition(
			Arrays.asList(
				new AttachmentObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"attachmentObjectFieldName"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							"acceptedFileExtensions", "txt"),
						_createObjectFieldSetting(
							"fileSource", "documentsAndMedia"),
						_createObjectFieldSetting("maximumFileSize", "100"))
				).build(),
				new DateObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new DecimalObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"decimalObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"integerObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new LongIntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longIntegerObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new PicklistObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					_listTypeDefinition.getListTypeDefinitionId()
				).name(
					"picklistObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new PrecisionDecimalObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"precisionDecimalObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new RichTextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"richTextObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).objectFieldSettings(
					Collections.emptyList()
				).build()));

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				_adminUser.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationshipName",
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_addAggregationObjectField(
			"AVERAGE", "precisionDecimalObjectFieldName",
			objectRelationship.getName());
		_addAggregationObjectField("COUNT", null, objectRelationship.getName());
		_addAggregationObjectField(
			"MAX", "integerObjectFieldName", objectRelationship.getName());
		_addAggregationObjectField(
			"MIN", "longIntegerObjectFieldName", objectRelationship.getName());
		_addAggregationObjectField(
			"SUM", "decimalObjectFieldName", objectRelationship.getName());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		_objectRelationshipERCObjectFieldName = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.
				NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
			objectField);
		_objectRelationshipFieldName = objectField.getName();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		ObjectEntry parentObjectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"externalReferenceCode", "newExternalReferenceCode"
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		String listTypeEntryKey = _addListTypeEntry();

		ObjectEntry childObjectEntry1 = new ObjectEntry() {
			{
				properties = HashMapBuilder.<String, Object>put(
					_objectRelationshipERCObjectFieldName,
					"newExternalReferenceCode"
				).put(
					"attachmentObjectFieldName",
					_getAttachmentObjectFieldValue()
				).put(
					"dateObjectFieldName", "2022-01-01"
				).put(
					"decimalObjectFieldName", 15.5
				).put(
					"integerObjectFieldName", 10
				).put(
					"longIntegerObjectFieldName", 50000L
				).put(
					"picklistObjectFieldName", listTypeEntryKey
				).put(
					"precisionDecimalObjectFieldName",
					new BigDecimal(0.1234567891234567, MathContext.DECIMAL64)
				).put(
					"richTextObjectFieldName",
					StringBundler.concat(
						"<i>", RandomTestUtil.randomString(), "</i>")
				).put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
			}
		};

		_assertEquals(
			childObjectEntry1,
			_objectEntryManager.addObjectEntry(
				_dtoConverterContext, _objectDefinition2, childObjectEntry1,
				ObjectDefinitionConstants.SCOPE_COMPANY));

		_assertEquals(
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"averageAggregationObjectFieldName",
						"0.12345678912345670000"
					).put(
						"countAggregationObjectFieldName", "1"
					).put(
						"maxAggregationObjectFieldName", "10"
					).put(
						"minAggregationObjectFieldName", "50000"
					).put(
						"sumAggregationObjectFieldName", "15.5"
					).put(
						"textObjectFieldName",
						MapUtil.getString(
							parentObjectEntry1.getProperties(),
							"textObjectFieldName")
					).build();
				}
			},
			_objectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry1.getId()));

		_objectEntryManager.addObjectEntry(
			_dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_objectRelationshipERCObjectFieldName,
						"newExternalReferenceCode"
					).put(
						"dateObjectFieldName", "2020-01-02"
					).put(
						"decimalObjectFieldName", 15.7
					).put(
						"integerObjectFieldName", 15
					).put(
						"longIntegerObjectFieldName", 100L
					).put(
						"picklistObjectFieldName", _addListTypeEntry()
					).put(
						"precisionDecimalObjectFieldName",
						new BigDecimal(
							0.9876543217654321, MathContext.DECIMAL64)
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_assertEquals(
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"averageAggregationObjectFieldName",
						"0.55555555544444440000"
					).put(
						"countAggregationObjectFieldName", "2"
					).put(
						"maxAggregationObjectFieldName", "15"
					).put(
						"minAggregationObjectFieldName", "100"
					).put(
						"sumAggregationObjectFieldName", "31.2"
					).put(
						"textObjectFieldName",
						MapUtil.getString(
							parentObjectEntry1.getProperties(),
							"textObjectFieldName")
					).build();
				}
			},
			_objectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry1.getId()));

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition1.getObjectDefinitionId(),
			"countAggregationObjectFieldName");

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"integerObjectFieldName", ObjectFilterConstants.TYPE_EQUALS,
			"{\"eq\": \"15\"}");

		_assertCountAggregationObjectFieldValue(1, parentObjectEntry1);

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"integerObjectFieldName", ObjectFilterConstants.TYPE_NOT_EQUALS,
			"{\"ne\":\"15\"}");

		_assertCountAggregationObjectFieldValue(0, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField.getObjectFieldId());

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"picklistObjectFieldName", ObjectFilterConstants.TYPE_EXCLUDES,
			"{\"not\":{\"in\":[\"" + listTypeEntryKey + "\"]}}");

		_assertCountAggregationObjectFieldValue(1, parentObjectEntry1);

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"picklistObjectFieldName", ObjectFilterConstants.TYPE_INCLUDES,
			"{\"in\":[\"" + listTypeEntryKey + "\"]}");

		_assertCountAggregationObjectFieldValue(0, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField.getObjectFieldId());

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(), "status",
			ObjectFilterConstants.TYPE_INCLUDES,
			"{\"in\": [" + WorkflowConstants.STATUS_APPROVED + "]}");

		_assertCountAggregationObjectFieldValue(2, parentObjectEntry1);

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(), "status",
			ObjectFilterConstants.TYPE_EXCLUDES,
			"{\"not\":{\"in\": [" + WorkflowConstants.STATUS_APPROVED + "]}}");

		_assertCountAggregationObjectFieldValue(0, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField.getObjectFieldId());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		String currentDateString = dateFormat.format(new Date());

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"createDate", ObjectFilterConstants.TYPE_DATE_RANGE,
			StringBundler.concat(
				"{\"le\": \"", currentDateString, "\", \"ge\": \"",
				currentDateString, "\"}"));
		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"modifiedDate", ObjectFilterConstants.TYPE_DATE_RANGE,
			StringBundler.concat(
				"{\"le\": \"", currentDateString, "\", \"ge\": \"",
				currentDateString, "\"}"));

		_assertCountAggregationObjectFieldValue(2, parentObjectEntry1);

		_objectFilterLocalService.addObjectFilter(
			_adminUser.getUserId(), objectField.getObjectFieldId(),
			"dateObjectFieldName", ObjectFilterConstants.TYPE_DATE_RANGE,
			"{\"le\": \"2020-01-02\", \"ge\": \"2020-01-02\"}");

		_assertCountAggregationObjectFieldValue(1, parentObjectEntry1);
	}

	@Test
	public void testAddObjectEntryAccountRestriction() throws Exception {
		_restrictObjectDefinitionByAccount();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		// Account scope

		AccountEntry accountEntry1 = _addAccountEntry();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), _user.getUserId());

		try {
			_objectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName_accountEntryId",
							accountEntry1.getAccountEntryId()
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have ADD_OBJECT_ENTRY ",
					"permission for ", _objectDefinition1.getResourceName(),
					StringPool.SPACE));
		}

		Role buyerRole = _roleLocalService.getRole(_companyId, "Buyer");

		_resourcePermissionLocalService.addResourcePermission(
			_companyId, _objectDefinition1.getResourceName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", buyerRole.getRoleId(),
			"ADD_OBJECT_ENTRY");

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry1.getAccountEntryGroupId(),
			buyerRole.getRoleId());

		ObjectEntry objectEntry = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertNotNull(objectEntry);

		AccountEntry accountEntry2 = _addAccountEntry();

		try {
			_objectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName_accountEntryId",
							accountEntry2.getAccountEntryId()
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " has no access to the ",
					"account entry ", accountEntry2.getAccountEntryId()));
		}

		// Organization scope

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			organization1.getOrganizationId());

		Role accountManagerRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

		Group organizationGroup1 = _groupLocalService.getOrganizationGroup(
			_companyId, organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		try {
			_objectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName_accountEntryId",
							accountEntry1.getAccountEntryId()
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have ADD_OBJECT_ENTRY ",
					"permission for ", _objectDefinition1.getResourceName(),
					StringPool.SPACE));
		}

		_resourcePermissionLocalService.addResourcePermission(
			_companyId, _objectDefinition1.getResourceName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			accountManagerRole.getRoleId(), "ADD_OBJECT_ENTRY");

		_objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			_objectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName_accountEntryId",
							accountEntry2.getAccountEntryId()
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " has no access to the ",
					"account entry ", accountEntry2.getAccountEntryId()));
		}

		// Sub Organization scope

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry1.getAccountEntryId(),
				organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			suborganization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			suborganization2.getOrganizationId());

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		try {
			_objectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName_accountEntryId",
							accountEntry1.getAccountEntryId()
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have ADD_OBJECT_ENTRY ",
					"permission for ", _objectDefinition1.getResourceName(),
					StringPool.SPACE));
		}

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			_objectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName_accountEntryId",
							accountEntry2.getAccountEntryId()
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " has no access to the ",
					"account entry ", accountEntry2.getAccountEntryId()));
		}
	}

	@Test
	public void testDeleteObjectEntryAccountRestriction() throws Exception {
		_restrictObjectDefinitionByAccount();

		AccountEntry accountEntry1 = _addAccountEntry();
		AccountEntry accountEntry2 = _addAccountEntry();

		ObjectEntry objectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry2 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry2.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		// Account scope

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), _user.getUserId());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), _user.getUserId());

		Role accountAdministratorRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		_addResourcePermission(
			ActionKeys.VIEW, accountAdministratorRole.getRoleId());
		_addResourcePermission(
			ActionKeys.DELETE, accountAdministratorRole.getRoleId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry1.getAccountEntryGroupId(),
			accountAdministratorRole.getRoleId());

		Role buyerRole = _roleLocalService.getRole(_companyId, "Buyer");

		_addResourcePermission(ActionKeys.VIEW, buyerRole.getRoleId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry2.getAccountEntryGroupId(),
			buyerRole.getRoleId());

		_assertObjectEntriesSize(2);

		_objectEntryManager.deleteObjectEntry(
			_objectDefinition1, objectEntry1.getId());

		_assertObjectEntriesSize(1);

		try {
			_objectEntryManager.deleteObjectEntry(
				_objectDefinition1, objectEntry2.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have DELETE permission ",
					"for ", _objectDefinition1.getClassName(), StringPool.SPACE,
					objectEntry2.getId()));
		}

		_assertObjectEntriesSize(1);

		// Organization scope

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_adminUser));

		objectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			organization2.getOrganizationId());

		Role accountManagerRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

		Group organizationGroup1 = _groupLocalService.getOrganizationGroup(
			_companyId, organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_addResourcePermission(ActionKeys.VIEW, accountManagerRole.getRoleId());

		_assertObjectEntriesSize(1);

		try {
			_objectEntryManager.deleteObjectEntry(
				_objectDefinition1, objectEntry1.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have DELETE permission ",
					"for ", _objectDefinition1.getClassName(), StringPool.SPACE,
					objectEntry1.getId()));
		}

		_assertObjectEntriesSize(1);

		_addResourcePermission(
			ActionKeys.DELETE, accountManagerRole.getRoleId());

		_objectEntryManager.deleteObjectEntry(
			_objectDefinition1, objectEntry1.getId());

		_assertObjectEntriesSize(0);

		// Sub organization scope

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_adminUser));

		objectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry1.getAccountEntryId(),
				organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			suborganization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry2.getAccountEntryId(),
				organization2.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			suborganization2.getOrganizationId());

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_assertObjectEntriesSize(1);

		_resourcePermissionLocalService.removeResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			accountManagerRole.getRoleId(), ActionKeys.DELETE);

		try {
			_objectEntryManager.deleteObjectEntry(
				_objectDefinition1, objectEntry1.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have DELETE permission ",
					"for ", _objectDefinition1.getClassName(), StringPool.SPACE,
					objectEntry1.getId()));
		}

		_assertObjectEntriesSize(1);

		_addResourcePermission(
			ActionKeys.DELETE, accountManagerRole.getRoleId());

		_objectEntryManager.deleteObjectEntry(
			_objectDefinition1, objectEntry1.getId());

		_assertObjectEntriesSize(0);
	}

	@Test
	public void testGetObjectEntries() throws Exception {
		_testGetObjectEntries(Collections.emptyMap());

		String picklistObjectFieldValue1 = _addListTypeEntry();

		ObjectEntry parentObjectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry childObjectEntry1 = _objectEntryManager.addObjectEntry(
			_dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_objectRelationshipFieldName, parentObjectEntry1.getId()
					).put(
						"picklistObjectFieldName", picklistObjectFieldValue1
					).put(
						"textObjectFieldName", "aaa"
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry parentObjectEntry2 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		String picklistObjectFieldValue2 = _addListTypeEntry();

		ObjectEntry childObjectEntry2 = _objectEntryManager.addObjectEntry(
			_dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_objectRelationshipFieldName, parentObjectEntry2.getId()
					).put(
						"picklistObjectFieldName", picklistObjectFieldValue2
					).put(
						"textObjectFieldName", "aab"
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		// Equals expression

		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildEqualsExpressionFilterString(
					"picklistObjectFieldName", picklistObjectFieldValue1)
			).put(
				"search", "aa"
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildEqualsExpressionFilterString(
					"picklistObjectFieldName", picklistObjectFieldValue2)
			).put(
				"search", "aa"
			).build(),
			childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildEqualsExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildEqualsExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry2.getExternalReferenceCode())
			).build(),
			childObjectEntry2);

		// In expression

		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"id", true, childObjectEntry1.getId())
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"id", false, childObjectEntry1.getId())
			).build(),
			childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"picklistObjectFieldName", true, picklistObjectFieldValue1)
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"picklistObjectFieldName", false, picklistObjectFieldValue1)
			).build(),
			childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipERCObjectFieldName, true,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipERCObjectFieldName, false,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipFieldName.substring(
						_objectRelationshipFieldName.lastIndexOf("_") + 1),
					true, parentObjectEntry1.getId())
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipFieldName.substring(
						_objectRelationshipFieldName.lastIndexOf("_") + 1),
					false, parentObjectEntry1.getId())
			).build(),
			childObjectEntry2);

		// Lambda expression

		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildLambdaExpressionFilterString(
					"status", true, WorkflowConstants.STATUS_APPROVED)
			).build(),
			childObjectEntry1, childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildLambdaExpressionFilterString(
					"status", false, WorkflowConstants.STATUS_APPROVED)
			).build());

		// Range expression

		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildRangeExpression(
					childObjectEntry1.getDateCreated(), new Date(),
					"dateCreated")
			).build(),
			childObjectEntry1, childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildRangeExpression(
					childObjectEntry1.getDateModified(), new Date(),
					"dateModified")
			).build(),
			childObjectEntry1, childObjectEntry2);

		_testGetObjectEntries(
			HashMapBuilder.put(
				"search", String.valueOf(childObjectEntry1.getId())
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"search", String.valueOf(childObjectEntry2.getId())
			).build(),
			childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"search", picklistObjectFieldValue1
			).build(),
			childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"search", picklistObjectFieldValue2
			).build(),
			childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"search", "aa"
			).build(),
			childObjectEntry1, childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "createDate:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "createDate:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "id:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "id:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "textObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		_testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "textObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
	}

	@Test
	public void testGetObjectEntriesAccountRestrictions() throws Exception {
		_restrictObjectDefinitionByAccount();

		AccountEntry accountEntry1 = _addAccountEntry();
		AccountEntry accountEntry2 = _addAccountEntry();

		ObjectEntry objectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry2.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_assertObjectEntriesSize(0);

		// Regular roles permissions should not be restricted by account

		Role randomRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.addResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(_companyId),
			randomRole.getRoleId(), ActionKeys.VIEW);

		_userLocalService.addRoleUser(randomRole.getRoleId(), _user);

		_assertObjectEntriesSize(2);

		_resourcePermissionLocalService.removeResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(_companyId),
			randomRole.getRoleId(), ActionKeys.VIEW);

		// Regular roles' individual permissions should not be restricted by
		// account

		_resourcePermissionLocalService.setResourcePermissions(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getId()), randomRole.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_assertObjectEntriesSize(1);

		_userLocalService.deleteRoleUser(randomRole.getRoleId(), _user);

		_assertObjectEntriesSize(0);

		// Users who only have SHARED account roles (e.g.: Account Member)
		// should be able to view all entries related to the account entry that
		// the users belong if those account roles have the view resource
		// permission

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), _user.getUserId());

		Role accountAdministratorRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		_addResourcePermission(
			ActionKeys.VIEW, accountAdministratorRole.getRoleId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry1.getAccountEntryGroupId(),
			accountAdministratorRole.getRoleId());

		_assertObjectEntriesSize(1);

		_resourcePermissionLocalService.removeResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			accountAdministratorRole.getRoleId(), ActionKeys.VIEW);

		_assertObjectEntriesSize(0);

		// Users who only have OWNED account roles (e.g.: Foo Account Role)
		// should be able to view all entries related to the account entry that
		// they (User and owned roles) belong if those account roles have the
		// view resource permission

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			_user.getUserId(), accountEntry2.getAccountEntryId(),
			RandomTestUtil.randomString(), Collections.emptyMap(),
			Collections.emptyMap());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry2.getAccountEntryGroupId(),
			accountRole.getRoleId());

		_addResourcePermission(ActionKeys.VIEW, accountRole.getRoleId());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), _user.getUserId());

		_assertObjectEntriesSize(1);

		_resourcePermissionLocalService.removeResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			accountRole.getRoleId(), ActionKeys.VIEW);

		_assertObjectEntriesSize(0);

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelsByAccountUserId(_user.getUserId());

		// The User who is associated an organization must be able to view all
		// entries related to account entries that belong to that organization
		// if the user has the VIEW resource permission in at least one
		// organization role assigned to them

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			organization1.getOrganizationId());

		_assertObjectEntriesSize(0);

		Role accountManagerRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

		Group organizationGroup1 = _groupLocalService.getOrganizationGroup(
			_companyId, organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_addResourcePermission(ActionKeys.VIEW, accountManagerRole.getRoleId());

		_assertObjectEntriesSize(2);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry2.getAccountEntryId(),
				organization1.getOrganizationId());

		_assertObjectEntriesSize(1);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry1.getAccountEntryId(),
				organization1.getOrganizationId());

		_assertObjectEntriesSize(0);

		_resourcePermissionLocalService.removeResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			accountManagerRole.getRoleId(), ActionKeys.VIEW);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		// The User who is associated an organization must be able to view all
		// entries related to account entries that belong to that organization
		// and its sub organizations if the user has the VIEW resource
		// permission in at least one organization role assigned to them

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			suborganization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			suborganization2.getOrganizationId());

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_addResourcePermission(ActionKeys.VIEW, accountManagerRole.getRoleId());

		_assertObjectEntriesSize(1);

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), suborganization2.getOrganizationId());

		_assertObjectEntriesSize(1);

		Group organizationGroup2 = _groupLocalService.getOrganizationGroup(
			_companyId, organization2.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup2.getGroupId(),
			accountManagerRole.getRoleId());

		_assertObjectEntriesSize(2);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry1.getAccountEntryId(),
				suborganization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			organization1.getOrganizationId());

		_assertObjectEntriesSize(1);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry2.getAccountEntryId(),
				suborganization2.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			organization2.getOrganizationId());

		_assertObjectEntriesSize(0);
	}

	@Test
	public void testUpdateObjectEntryAccountRestriction() throws Exception {
		_restrictObjectDefinitionByAccount();

		AccountEntry accountEntry1 = _addAccountEntry();
		AccountEntry accountEntry2 = _addAccountEntry();

		ObjectEntry objectEntry1 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry1.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry2 = _objectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry2.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		// Account scope

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), _user.getUserId());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), _user.getUserId());

		Role accountAdministratorRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		_addResourcePermission(
			ActionKeys.VIEW, accountAdministratorRole.getRoleId());
		_addResourcePermission(
			ActionKeys.UPDATE, accountAdministratorRole.getRoleId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry1.getAccountEntryGroupId(),
			accountAdministratorRole.getRoleId());

		Role buyerRole = _roleLocalService.getRole(_companyId, "Buyer");

		_addResourcePermission(ActionKeys.VIEW, buyerRole.getRoleId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry2.getAccountEntryGroupId(),
			buyerRole.getRoleId());

		_assertObjectEntriesSize(2);

		_objectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			objectEntry1.getId(), objectEntry1);

		try {
			_objectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				objectEntry2.getId(), objectEntry2);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have UPDATE permission ",
					"for ", _objectDefinition1.getClassName(), " ",
					objectEntry2.getId()));
		}

		// Organization scope

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			organization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			organization2.getOrganizationId());

		Role accountManagerRole = _roleLocalService.getRole(
			_companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

		Group organizationGroup1 = _groupLocalService.getOrganizationGroup(
			_companyId, organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_addResourcePermission(ActionKeys.VIEW, accountManagerRole.getRoleId());

		try {
			_objectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				objectEntry1.getId(), objectEntry1);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have UPDATE permission ",
					"for ", _objectDefinition1.getClassName(), " ",
					objectEntry1.getId()));
		}

		_addResourcePermission(
			ActionKeys.UPDATE, accountManagerRole.getRoleId());

		_objectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			objectEntry1.getId(), objectEntry1);

		// Sub organization scope

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			suborganization1.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			suborganization2.getOrganizationId());

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organizationGroup1.getGroupId(),
			accountManagerRole.getRoleId());

		_assertObjectEntriesSize(1);

		_resourcePermissionLocalService.removeResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			accountManagerRole.getRoleId(), ActionKeys.UPDATE);

		try {
			_objectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				objectEntry1.getId(), objectEntry1);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				com.liferay.petra.string.StringBundler.concat(
					"User ", _user.getUserId(), " must have UPDATE permission ",
					"for ", _objectDefinition1.getClassName(), " ",
					objectEntry1.getId()));
		}

		_addResourcePermission(
			ActionKeys.UPDATE, accountManagerRole.getRoleId());

		_objectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition1,
			objectEntry1.getId(), objectEntry1);
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			_adminUser.getUserId(), 0L, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null, null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addAggregationObjectField(
			String functionName, String objectFieldName,
			String objectRelationshipName)
		throws Exception {

		List<ObjectFieldSetting> objectFieldSettings = new ArrayList<>();

		objectFieldSettings.add(
			_createObjectFieldSetting("function", functionName));

		if (!Objects.equals(functionName, "COUNT")) {
			objectFieldSettings.add(
				_createObjectFieldSetting("objectFieldName", objectFieldName));
		}

		objectFieldSettings.add(
			_createObjectFieldSetting(
				"objectRelationshipName", objectRelationshipName));

		_addCustomObjectField(
			new AggregationObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.lowerCase(functionName) +
					"AggregationObjectFieldName"
			).objectDefinitionId(
				_objectDefinition1.getObjectDefinitionId()
			).objectFieldSettings(
				objectFieldSettings
			).build());
	}

	private void _addCustomObjectField(ObjectField objectField)
		throws Exception {

		_objectFieldService.addCustomObjectField(
			objectField.getExternalReferenceCode(),
			objectField.getListTypeDefinitionId(),
			objectField.getObjectDefinitionId(), objectField.getBusinessType(),
			objectField.getDBType(), objectField.getDefaultValue(),
			objectField.isIndexed(), objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.getName(), objectField.isRequired(),
			objectField.isState(), objectField.getObjectFieldSettings());
	}

	private String _addListTypeEntry() throws Exception {
		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.addListTypeEntry(
				_adminUser.getUserId(),
				_listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()));

		return listTypeEntry.getKey();
	}

	private void _addResourcePermission(String actionId, long roleId)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			_companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", roleId, actionId);
	}

	private void _assertCountAggregationObjectFieldValue(
			int expectedValue, ObjectEntry objectEntry)
		throws Exception {

		_assertEquals(
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"countAggregationObjectFieldName",
						String.valueOf(expectedValue)
					).build();
				}
			},
			_objectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				objectEntry.getId()));
	}

	private void _assertEquals(
			List<ObjectEntry> expectedObjectEntries,
			List<ObjectEntry> actualObjectEntries)
		throws Exception {

		Assert.assertEquals(
			actualObjectEntries.toString(), expectedObjectEntries.size(),
			actualObjectEntries.size());

		for (int i = 0; i < expectedObjectEntries.size(); i++) {
			_assertEquals(
				expectedObjectEntries.get(i), actualObjectEntries.get(i));
		}
	}

	private void _assertEquals(
			ObjectEntry expectedObjectEntry, ObjectEntry actualObjectEntry)
		throws Exception {

		Map<String, Object> actualObjectEntryProperties =
			actualObjectEntry.getProperties();

		Map<String, Object> expectedObjectEntryProperties =
			expectedObjectEntry.getProperties();

		for (Map.Entry<String, Object> expectedEntry :
				expectedObjectEntryProperties.entrySet()) {

			if (Objects.equals(
					expectedEntry.getKey(), "attachmentObjectFieldName")) {

				FileEntry fileEntry =
					(FileEntry)actualObjectEntryProperties.get(
						expectedEntry.getKey());

				Assert.assertEquals(
					expectedEntry.getValue(), fileEntry.getId());

				DLFileEntry dlFileEntry = _dlFileEntryLocalService.getFileEntry(
					fileEntry.getId());

				Assert.assertEquals(
					fileEntry.getName(), dlFileEntry.getFileName());

				Link link = fileEntry.getLink();

				Assert.assertEquals(link.getLabel(), dlFileEntry.getFileName());

				com.liferay.portal.kernel.repository.model.FileEntry
					repositoryFileEntry = _dlAppService.getFileEntry(
						fileEntry.getId());

				String url = _dlURLHelper.getDownloadURL(
					repositoryFileEntry, repositoryFileEntry.getFileVersion(),
					null, StringPool.BLANK);

				url = HttpComponentsUtil.addParameter(
					url, "objectDefinitionExternalReferenceCode",
					_objectDefinition2.getExternalReferenceCode());
				url = HttpComponentsUtil.addParameter(
					url, "objectEntryExternalReferenceCode",
					actualObjectEntry.getExternalReferenceCode());

				Assert.assertEquals(url, link.getHref());
			}
			else if (Objects.equals(
						expectedEntry.getKey(), "dateObjectFieldName")) {

				if ((expectedEntry.getValue() == null) &&
					(actualObjectEntryProperties.get(expectedEntry.getKey()) ==
						null)) {

					continue;
				}

				Assert.assertEquals(
					expectedEntry.getKey(),
					expectedEntry.getValue() + " 00:00:00.0",
					String.valueOf(
						actualObjectEntryProperties.get(
							expectedEntry.getKey())));
			}
			else if (Objects.equals(
						expectedEntry.getKey(), "picklistObjectFieldName")) {

				ListEntry listEntry =
					(ListEntry)actualObjectEntryProperties.get(
						expectedEntry.getKey());

				if (expectedEntry.getValue() instanceof String) {
					Assert.assertEquals(
						expectedEntry.getValue(), listEntry.getKey());
				}
				else {
					Assert.assertEquals(expectedEntry.getValue(), listEntry);
				}
			}
			else if (Objects.equals(
						expectedEntry.getKey(), "richTextObjectFieldName")) {

				Assert.assertEquals(
					expectedEntry.getValue(),
					actualObjectEntryProperties.get(expectedEntry.getKey()));
			}
			else if (Objects.equals(
						expectedEntry.getKey(),
						"richTextObjectFieldNameRawText")) {

				Assert.assertEquals(
					HtmlParserUtil.extractText(
						String.valueOf(expectedEntry.getValue())),
					String.valueOf(
						actualObjectEntryProperties.get(
							expectedEntry.getKey())));
			}
			else if (Objects.equals(
						expectedEntry.getKey(),
						_objectRelationshipERCObjectFieldName)) {

				Assert.assertEquals(
					expectedEntry.getValue(),
					actualObjectEntryProperties.get(expectedEntry.getKey()));

				_assertEquals(
					_objectEntryManager.getObjectEntry(
						_simpleDTOConverterContext,
						GetterUtil.getString(expectedEntry.getValue()),
						_objectDefinition1.getCompanyId(), _objectDefinition1,
						null),
					(ObjectEntry)actualObjectEntryProperties.get(
						StringUtil.replaceLast(
							_objectRelationshipFieldName, "Id",
							StringPool.BLANK)));
			}
			else {
				Assert.assertEquals(
					expectedEntry.getKey(), expectedEntry.getValue(),
					actualObjectEntryProperties.get(expectedEntry.getKey()));
			}
		}
	}

	private void _assertObjectEntriesSize(long size) throws Exception {
		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			_companyId, _objectDefinition1, null, null,
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), _dtoConverterRegistry, null,
				LocaleUtil.getDefault(), null, _user),
			null,
			_filterPredicateFactory.create(
				null, _objectDefinition2.getObjectDefinitionId()),
			null, null);

		Collection<ObjectEntry> objectEntries = page.getItems();

		Assert.assertEquals(
			objectEntries.toString(), size, objectEntries.size());
	}

	private String _buildEqualsExpressionFilterString(
		String fieldName, String value) {

		return StringBundler.concat("( ", fieldName, " eq '", value, "')");
	}

	private String _buildInExpressionFilterString(
		String fieldName, boolean includes, Object... values) {

		List<String> valuesList = new ArrayList<>();

		for (Object value : values) {
			valuesList.add(StringUtil.quote(String.valueOf(value)));
		}

		String filterString = StringBundler.concat(
			"(", fieldName, " in (",
			StringUtil.merge(valuesList, StringPool.COMMA_AND_SPACE), "))");

		if (includes) {
			return filterString;
		}

		return StringBundler.concat("(not ", filterString, ")");
	}

	private String _buildLambdaExpressionFilterString(
		String fieldName, boolean includes, int... values) {

		List<String> valuesList = new ArrayList<>();

		for (int value : values) {
			valuesList.add(
				StringBundler.concat(
					"(x ", includes ? "eq " : "ne ", String.valueOf(value),
					")"));
		}

		return StringBundler.concat(
			"(", fieldName, "/any(x:",
			StringUtil.merge(valuesList, includes ? " or " : " and "), "))");
	}

	private String _buildRangeExpression(
		Date date1, Date date2, String fieldName) {

		return StringBundler.concat(
			"(( ", fieldName, " ge ", _simpleDateFormat.format(date1),
			") and ( ", fieldName, " le ", _simpleDateFormat.format(date2),
			"))");
	}

	private ObjectDefinition _createObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				_adminUser.getUserId(), false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"A" + RandomTestUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT, objectFields);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			_adminUser.getUserId(), objectDefinition.getObjectDefinitionId());
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private Long _getAttachmentObjectFieldValue() throws Exception {
		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, _group.getCreatorUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			MimeTypesUtil.getExtensionContentType("txt"),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, inputStream, bytes.length, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return dlFileEntry.getFileEntryId();
	}

	private void _restrictObjectDefinitionByAccount() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_companyId, "AccountEntry");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				_adminUser.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationshipName",
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_objectDefinition1.setAccountEntryRestrictedObjectFieldId(
			objectRelationship.getObjectFieldId2());

		_objectDefinition1.setAccountEntryRestricted(true);

		_objectDefinition1 =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition1);
	}

	private void _setUpDTOConverterContext() {
		UriInfo uriInfo = (UriInfo)ProxyUtil.newProxyInstance(
			DefaultObjectEntryManagerImplTest.class.getClassLoader(),
			new Class<?>[] {UriInfo.class},
			(proxy, method, arguments) -> {
				if (Objects.equals(method.getName(), "getBaseUriBuilder")) {
					return UriBuilder.fromPath(RandomTestUtil.randomString());
				}
				else if (Objects.equals(method.getName(), "getMatchedURIs")) {
					return Arrays.asList(StringPool.BLANK);
				}
				else if (Objects.equals(
							method.getName(), "getPathParameters")) {

					return new MultivaluedHashMap<>();
				}
				else if (Objects.equals(
							method.getName(), "getQueryParameters")) {

					MultivaluedMap<String, String> multivaluedMap =
						new MultivaluedHashMap<>();

					multivaluedMap.add(
						"nestedFields",
						_objectDefinition1.getPKObjectFieldName());

					return multivaluedMap;
				}

				throw new UnsupportedOperationException(
					"Unsupported method " + method.getName());
			});

		_dtoConverterContext = new DefaultDTOConverterContext(
			false, Collections.emptyMap(), _dtoConverterRegistry, null,
			LocaleUtil.getDefault(), uriInfo, _adminUser);
	}

	private void _testGetObjectEntries(
			Map<String, String> context, ObjectEntry... expectedObjectEntries)
		throws Exception {

		Sort[] sorts = null;

		if (context.containsKey("sort")) {
			String[] sort = StringUtil.split(context.get("sort"), ":");

			sorts = new Sort[] {
				SortFactoryUtil.create(sort[0], Objects.equals(sort[1], "desc"))
			};
		}

		Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
			_companyId, _objectDefinition2, null, null, _dtoConverterContext,
			null,
			_filterPredicateFactory.create(
				context.get("filter"),
				_objectDefinition2.getObjectDefinitionId()),
			context.get("search"), sorts);

		_assertEquals(
			ListUtil.fromArray(expectedObjectEntries),
			(List<ObjectEntry>)page.getItems());
	}

	private static User _adminUser;
	private static long _companyId;
	private static DTOConverterContext _dtoConverterContext;

	@Inject
	private static DTOConverterRegistry _dtoConverterRegistry;

	@DeleteAfterTestRun
	private static Group _group;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;
	private static DateFormat _simpleDateFormat;
	private static DTOConverterContext _simpleDTOConverterContext;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private FilterPredicateFactory _filterPredicateFactory;

	@Inject
	private GroupLocalService _groupLocalService;

	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private ObjectEntryManager _objectEntryManager;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldService _objectFieldService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectFilterLocalService _objectFilterLocalService;

	private String _objectRelationshipERCObjectFieldName;
	private String _objectRelationshipFieldName;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}