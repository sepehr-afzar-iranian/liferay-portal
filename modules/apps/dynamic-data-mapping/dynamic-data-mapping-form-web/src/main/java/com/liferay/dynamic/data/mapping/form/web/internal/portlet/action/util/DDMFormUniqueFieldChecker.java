package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;

import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

@Component(immediate = true, service = DDMFormUniqueFieldChecker.class)
public class DDMFormUniqueFieldChecker {

	public boolean Check(String fieldValue, String fieldReference)
		throws PortalException {

		DynamicQuery dynamicQuery = DDMContentLocalServiceUtil.dynamicQuery();

		String value = String.format("%%%s%%", fieldValue);
		String reference = String.format("%%%s%%", fieldReference);
		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
			"data")).add(RestrictionsFactoryUtil.
			like("data", value)).add(RestrictionsFactoryUtil.
			like("data", reference));

		List<DDMContent> oldValues = DDMContentLocalServiceUtil.
			dynamicQuery(dynamicQuery);

		for (Object oldValue : oldValues) {
			String jsonString = String.valueOf(oldValue);
			JSONObject jsonObject = _JSONFactory.createJSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("fieldValues");
			for (int i = 0; i < jsonArray.length(); i++) {

				String jsonReference = jsonArray.getJSONObject(i).
					getString("fieldReference");

				String jsonValue = jsonArray.getJSONObject(i).
					getJSONObject("value").
					getString("en_US");

				if (fieldValue.equals(jsonValue) && fieldReference.equals(
					jsonReference)) {
					return true;
				}
			}


		}
		return false;
	}

	@Reference
	private JSONFactory _JSONFactory;

}
