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

package com.liferay.dynamic.data.mapping.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.dynamic.data.mapping.service.http.DDMTrackingCodeServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 * @generated
 */
@Deprecated
public class DDMTrackingCodeSoap implements Serializable {

	public static DDMTrackingCodeSoap toSoapModel(DDMTrackingCode model) {
		DDMTrackingCodeSoap soapModel = new DDMTrackingCodeSoap();

		soapModel.setMvccVersion(model.getMvccVersion());
		soapModel.setCtCollectionId(model.getCtCollectionId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setFormInstanceRecordId(model.getFormInstanceRecordId());
		soapModel.setTrackingCode(model.getTrackingCode());

		return soapModel;
	}

	public static DDMTrackingCodeSoap[] toSoapModels(DDMTrackingCode[] models) {
		DDMTrackingCodeSoap[] soapModels =
			new DDMTrackingCodeSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static DDMTrackingCodeSoap[][] toSoapModels(
		DDMTrackingCode[][] models) {

		DDMTrackingCodeSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new DDMTrackingCodeSoap[models.length][models[0].length];
		}
		else {
			soapModels = new DDMTrackingCodeSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static DDMTrackingCodeSoap[] toSoapModels(
		List<DDMTrackingCode> models) {

		List<DDMTrackingCodeSoap> soapModels =
			new ArrayList<DDMTrackingCodeSoap>(models.size());

		for (DDMTrackingCode model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new DDMTrackingCodeSoap[soapModels.size()]);
	}

	public DDMTrackingCodeSoap() {
	}

	public long getPrimaryKey() {
		return _formInstanceRecordId;
	}

	public void setPrimaryKey(long pk) {
		setFormInstanceRecordId(pk);
	}

	public long getMvccVersion() {
		return _mvccVersion;
	}

	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	public long getCtCollectionId() {
		return _ctCollectionId;
	}

	public void setCtCollectionId(long ctCollectionId) {
		_ctCollectionId = ctCollectionId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getFormInstanceRecordId() {
		return _formInstanceRecordId;
	}

	public void setFormInstanceRecordId(long formInstanceRecordId) {
		_formInstanceRecordId = formInstanceRecordId;
	}

	public String getTrackingCode() {
		return _trackingCode;
	}

	public void setTrackingCode(String trackingCode) {
		_trackingCode = trackingCode;
	}

	private long _mvccVersion;
	private long _ctCollectionId;
	private long _companyId;
	private long _formInstanceRecordId;
	private String _trackingCode;

}