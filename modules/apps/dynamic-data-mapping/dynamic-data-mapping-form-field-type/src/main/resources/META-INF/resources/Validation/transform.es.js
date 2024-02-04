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

import VALIDATIONS from '../util/validations.es';

const getValidationFromExpression = (validations, validation) => {
	return function transformValidationFromExpression(expression) {
		let mutValidation;

		if (!expression && validation) {
			expression = validation.expression;
		}

		if (expression) {
			mutValidation = validations.find(
				(validation) => validation.name === expression.name
			);
		}

		return mutValidation;
	};
};

const getDataProviderFromParameter = (dataProviders) => {
	return function transformDataProviderFromParameter(dataProviderInstanceId) {
		let mutDataProvider;

		if (!dataProviderInstanceId && dataProviders[0]) {
			dataProviderInstanceId = dataProviders[0].value;
		}

		if (dataProviderInstanceId) {
			mutDataProvider = dataProviders.find(
				(datap) => datap.value == dataProviderInstanceId
			);
		}

		return mutDataProvider;
	};
};

const getDataProviderOutputFromParameter = () => {
	return function transformDataProviderOutputFromParameter(
		selectedDataProvider,
		dataProviderOutputInstanceId
	) {
		let mutDataProviderOutput;

		if (!dataProviderOutputInstanceId && selectedDataProvider.outputs[0]) {
			dataProviderOutputInstanceId =
				selectedDataProvider.outputs[0].value;
		}

		if (dataProviderOutputInstanceId) {
			mutDataProviderOutput = selectedDataProvider.outputs.find(
				(datao) => datao.value === dataProviderOutputInstanceId
			);
		}

		return mutDataProviderOutput;
	};
};
const transformValidations = (
	validations,
	initialDataType,
	hasDataProviderAdvancedFormBuilder,
	hasRegexValidationAdvancedFormBuilder,
	hasListValidationAdvancedFormBuilder
) => {
	const dataType = initialDataType == 'string' ? initialDataType : 'numeric';

	return VALIDATIONS[dataType]
		.filter((validation) => {
			if (validation.name === 'isInList') {
				return hasListValidationAdvancedFormBuilder;
			}
			else if (validation.name === 'dataprovider') {
				return hasDataProviderAdvancedFormBuilder;
			}
			else if (hasRegexValidationAdvancedFormBuilder) {
				return true;
			}
			else {
				return validation.advanced !== true;
			}
		})
		.map((validation) => {
			return {
				...validation,
				checked: false,
				value: validation.name,
			};
		});
};

const getValidation = (
	defaultLanguageId,
	editingLanguageId,
	validations,
	transformValidationFromExpression
) => {
	return function transformValue(value) {
		const {errorMessage = {}, expression = {}, parameter = {}} = value;
		let parameterMessage = '';
		let selectedValidation = transformValidationFromExpression(expression);
		const enableValidation = !!expression.value;

		if (selectedValidation) {
			parameterMessage = selectedValidation.parameterMessage;
		}
		else {
			selectedValidation = validations[0];
		}

		return {
			enableValidation,
			errorMessage:
				errorMessage[editingLanguageId] ||
				errorMessage[defaultLanguageId],
			expression,
			parameter:
				parameter[editingLanguageId] || parameter[defaultLanguageId],
			parameterMessage,
			selectedValidation,
		};
	};
};

const getDataProvider = (
	defaultLanguageId,
	editingLanguageId,
	dataProviders,
	transformDataProviderFromParameter,
	transformDataProviderOutputFromParameter
) => {
	return function transformValue(value) {
		if (dataProviders === undefined || dataProviders.length === 0) {
			return {
				parameter: null,
				selectedDataProvider: null,
				selectedDataProviderOutput: null,
			};
		}
		let dataProviderInstanceId = '';
		let dataProviderOutputInstanceId = '';
		if (value.parameter[editingLanguageId]) {
			dataProviderInstanceId = value.parameter[editingLanguageId].split(
				'_$_$_'
			)[0];
			dataProviderOutputInstanceId = value.parameter[
				editingLanguageId
			].split('_$_$_')[1];
		}
		let selectedDataProvider = transformDataProviderFromParameter(
			dataProviderInstanceId
		);

		if (!selectedDataProvider) {
			selectedDataProvider = dataProviders[0];
		}

		let selectedDataProviderOutput = transformDataProviderOutputFromParameter(
			selectedDataProvider,
			dataProviderOutputInstanceId
		);

		if (!selectedDataProviderOutput) {
			selectedDataProviderOutput = selectedDataProvider.outputs[0];
		}

		return {
			parameter: {
				...value.parameter,
				[editingLanguageId]:
					selectedDataProvider.value +
					'_$_$_' +
					selectedDataProviderOutput.value,
			},
			selectedDataProvider,
			selectedDataProviderOutput,
		};
	};
};

export const getSelectedValidation = (validations) => {
	return function transformSelectedValidation(value) {
		if (Array.isArray(value)) {
			value = value[0];
		}

		let selectedValidation = validations.find(({name}) => name === value);

		if (!selectedValidation) {
			selectedValidation = validations[0];
		}

		return selectedValidation;
	};
};

export const getSelectedDataProvider = (dataProviders) => {
	return function transformSelectedDataProvider(value1) {
		if (Array.isArray(value1)) {
			value1 = value1[0];
		}

		let selectedDataProvider = dataProviders.find(
			({value}) => value === value1
		);

		if (!selectedDataProvider) {
			selectedDataProvider = dataProviders[0];
		}

		return selectedDataProvider;
	};
};

export const getSelectedDataProviderOutput = () => {
	return function transformSelectedDataProvider(dataproviderOutputs, value1) {
		if (Array.isArray(value1)) {
			value1 = value1[0];
		}

		let selectedDataProviderOutput = dataproviderOutputs.find(
			({value}) => value === value1
		);

		if (!selectedDataProviderOutput) {
			selectedDataProviderOutput = dataproviderOutputs[0];
		}

		return selectedDataProviderOutput;
	};
};

export const transformData = ({
	dataProviders,
	defaultLanguageId,
	editingLanguageId,
	hasDataProviderAdvancedFormBuilder,
	hasListValidationAdvancedFormBuilder,
	hasRegexValidationAdvancedFormBuilder,
	initialDataType,
	initialValidations,
	validation,
	value,
}) => {
	const dataType =
		validation && validation.dataType
			? validation.dataType
			: initialDataType;
	const validations = transformValidations(
		initialValidations,
		dataType,
		hasDataProviderAdvancedFormBuilder,
		hasRegexValidationAdvancedFormBuilder,
		hasListValidationAdvancedFormBuilder
	);
	const parsedValidation = getValidation(
		defaultLanguageId,
		editingLanguageId,
		validations,
		getValidationFromExpression(validations, validation)
	)(value);
	const parsedDataProvider = getDataProvider(
		defaultLanguageId,
		editingLanguageId,
		dataProviders,
		getDataProviderFromParameter(dataProviders),
		getDataProviderOutputFromParameter()
	)(value);
	const localizationMode = editingLanguageId !== defaultLanguageId;

	return {
		...parsedValidation,
		dataType,
		localizationMode,
		validations,
		...parsedDataProvider,
	};
};
