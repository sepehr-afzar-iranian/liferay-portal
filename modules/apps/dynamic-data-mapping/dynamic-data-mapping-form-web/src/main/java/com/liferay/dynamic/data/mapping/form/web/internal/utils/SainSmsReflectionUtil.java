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

package com.liferay.dynamic.data.mapping.form.web.internal.utils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Yousef Ghadiri
 */
public class SainSmsReflectionUtil {

	public static SMSMessageInfo getLastSMSMessage(long groupId, String mobile)
		throws NoSuchSMSMessageReflectionException {

		try {
			Class<?> clazz = _loadClass(
				_CLASS_NAME_SMS_MESSAGE_LOCAL_SERVICE_UTIL);

			Method method = clazz.getMethod(
				"getLastSMSMessage", long.class, String.class);

			Object smsMessage = method.invoke(null, groupId, mobile);

			return _toSMSMessageInfo(smsMessage);
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable != null) {
				Class<?> clazz = throwable.getClass();

				if (_CLASS_NAME_NO_SUCH_SMS_MESSAGE_EXCEPTION.equals(
						clazz.getName())) {

					throw new NoSuchSMSMessageReflectionException(throwable);
				}
			}

			_log.error(
				"Failed to invoke method: getLastSMSMessage",
				invocationTargetException);

			return null;
		}
		catch (Exception exception) {
			_log.error("Failed to invoke method: getLastSMSMessage", exception);

			return null;
		}
	}

	public static List<SMSProfileInfo> getSmsProfiles(long groupId) {
		List<SMSProfileInfo> smsProfileInfos = new ArrayList<>();

		try {
			Class<?> clazz = _loadClass(
				_CLASS_NAME_SMS_PROFILE_LOCAL_SERVICE_UTIL);

			Method method = clazz.getMethod("getSmsProfiles", long.class);

			List<?> smsProfiles = (List<?>)method.invoke(null, groupId);

			if (smsProfiles != null) {
				for (Object smsProfile : smsProfiles) {
					smsProfileInfos.add(_toSMSProfileInfo(smsProfile));
				}
			}
		}
		catch (Exception exception) {
			_log.error("Failed to invoke method: getSmsProfiles", exception);
		}

		return smsProfileInfos;
	}

	public static boolean sendSMSWithFormTrackingCode(
		long smsProfileId, String mobile, String trackingCode, String text,
		ServiceContext serviceContext) {

		try {
			Class<?> clazz = _loadClass(
				_CLASS_NAME_SMS_MESSAGE_LOCAL_SERVICE_UTIL);

			Method method = clazz.getMethod(
				"sendSMSWithFormTrackingCode", long.class, String.class,
				String.class, String.class, ServiceContext.class);

			return (boolean)method.invoke(
				null, smsProfileId, mobile, trackingCode, text, serviceContext);
		}
		catch (Exception exception) {
			_log.error(
				"Failed to invoke method: sendSMSWithFormTrackingCode",
				exception);

			return false;
		}
	}

	/**
	 * Local stand-in for ir.sain.definition.exception.NoSuchSMSMessageException,
	 * since we can't reference that type at compile time.
	 */
	public static class NoSuchSMSMessageReflectionException extends Exception {

		public NoSuchSMSMessageReflectionException(Throwable throwable) {
			super(throwable);
		}

	}

	/**
	 * Local stand-in for ir.sain.definition.model.SMSMessage.
	 */
	public static class SMSMessageInfo {

		public SMSMessageInfo(Date createDate, String code) {
			_createDate = createDate;
			_code = code;
		}

		public String get_code() {
			return _code;
		}

		public Date get_createDate() {
			return _createDate;
		}

		private final String _code;
		private final Date _createDate;

	}

	/**
	 * Local stand-in for ir.sain.definition.model.SMSProfile.
	 */
	public static class SMSProfileInfo {

		public SMSProfileInfo(long smsProfileId, String profileName) {
			_smsProfileId = smsProfileId;
			_profileName = profileName;
		}

		public String get_profileName() {
			return _profileName;
		}

		public long getSMSProfileId() {
			return _smsProfileId;
		}

		private final String _profileName;
		private final long _smsProfileId;

	}

	private static Class<?> _loadClass(String className)
		throws ClassNotFoundException {

		Bundle currentBundle = FrameworkUtil.getBundle(
			SainSmsReflectionUtil.class);

		BundleContext bundleContext = currentBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (_BUNDLE_SYMBOLIC_NAME.equals(bundle.getSymbolicName())) {
				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

				ClassLoader classLoader = bundleWiring.getClassLoader();

				return classLoader.loadClass(className);
			}
		}

		throw new ClassNotFoundException(
			"Bundle not found: " + _BUNDLE_SYMBOLIC_NAME);
	}

	private static SMSMessageInfo _toSMSMessageInfo(Object smsMessage)
		throws Exception {

		if (smsMessage == null) {
			return null;
		}

		Class<?> clazz = smsMessage.getClass();

		Method method = clazz.getMethod("getCreateDate");

		Date createDate = (Date)method.invoke(smsMessage);

		method = clazz.getMethod("getCode");

		String code = (String)method.invoke(smsMessage);

		return new SMSMessageInfo(createDate, code);
	}

	private static SMSProfileInfo _toSMSProfileInfo(Object smsProfile)
		throws Exception {

		Class<?> clazz = smsProfile.getClass();

		Method method = clazz.getMethod("getSMSProfileId");

		long smsProfileId = (long)method.invoke(smsProfile);

		method = clazz.getMethod("getProfileName");

		String profileName = (String)method.invoke(smsProfile);

		return new SMSProfileInfo(smsProfileId, profileName);
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"ir.sain.definition.api";

	private static final String _CLASS_NAME_NO_SUCH_SMS_MESSAGE_EXCEPTION =
		"ir.sain.definition.exception.NoSuchSMSMessageException";

	private static final String _CLASS_NAME_SMS_MESSAGE_LOCAL_SERVICE_UTIL =
		"ir.sain.definition.service.SMSMessageLocalServiceUtil";

	private static final String _CLASS_NAME_SMS_PROFILE_LOCAL_SERVICE_UTIL =
		"ir.sain.definition.service.SMSProfileLocalServiceUtil";

	private static final Log _log = LogFactoryUtil.getLog(
		SainSmsReflectionUtil.class);

}