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

import ClayForm from '@clayui/form';
import {usePrevious} from 'frontend-js-react-web';
import React, {useEffect, useState} from 'react';

import Checkbox from '../Checkbox/Checkbox.es';
import Numeric from '../Numeric/Numeric.es';
import Select from '../Select/Select.es';
import Text from '../Text/Text.es';
import {subWords} from '../util/strings.es';
import {
	getSelectedDataProvider,
	getSelectedDataProviderOutput,
	getSelectedValidation,
	transformData,
} from './transform.es';

const Validation = ({
	dataProviders,
	dataType,
	defaultLanguageId,
	editingLanguageId,
	enableValidation: initialEnableValidation,
	errorMessage: initialErrorMessage,
	label,
	localizationMode,
	name,
	onChange,
	parameter: initialParameter,
	parameterMessage,
	readOnly,
	selectedDataProvider: initialSelectedDataProvider,
	selectedDataProviderOutput: initialSelectedDataProviderOutput,
	selectedValidation: initialSelectedValidation,
	spritemap,
	validation,
	validations,
	value,
	visible,
}) => {
	const [
		{
			enableValidation,
			errorMessage,
			parameter,
			selectedDataProvider,
			selectedDataProviderOutput,
			selectedValidation,
		},
		setState,
	] = useState({
		enableValidation: initialEnableValidation,
		errorMessage: initialErrorMessage,
		parameter: initialParameter,
		selectedDataProvider: initialSelectedDataProvider,
		selectedDataProviderOutput: initialSelectedDataProviderOutput,
		selectedValidation: initialSelectedValidation,
	});

	const DynamicComponent =
		selectedValidation &&
		selectedValidation.parameterMessage &&
		dataType === 'string'
			? Text
			: Numeric;

	const handleChange = (key, newValue) => {
		setState((prevState) => {
			const newState = {
				...prevState,
				[key]: newValue,
			};

			let expression = {};

			if (newState.enableValidation) {
				expression = {
					name: newState.selectedValidation.name,
					value: subWords(newState.selectedValidation.template, {
						name: validation.fieldName,
					}),
				};
			}

			let parm = {
				...value.parameter,
				[editingLanguageId]: !value.expression
					? parameterMessage
					: newState.parameter,
			};

			if (
				!(dataProviders === undefined || dataProviders.length === 0) &&
				newState.selectedValidation.name === 'dataprovider'
			) {
				parm = {
					...parm,
					[editingLanguageId]:
						newState.selectedDataProvider.value +
						'_$_$_' +
						newState.selectedDataProviderOutput.value,
				};
			}

			onChange({
				enableValidation: newState.enableValidation,
				errorMessage: {
					...value.errorMessage,
					[editingLanguageId]: newState.errorMessage,
				},
				expression,
				parameter: {
					...parm,
				},
			});

			return newState;
		});
	};

	const transformSelectedValidation = getSelectedValidation(validations);

	const transformSelectedDataProvider = getSelectedDataProvider(
		dataProviders
	);

	const transformSelectedDataProviderOutput = getSelectedDataProviderOutput();

	const prevEditingLanguageId = usePrevious(editingLanguageId);

	useEffect(() => {
		if (prevEditingLanguageId !== editingLanguageId) {
			setState((prevState) => {
				const {errorMessage = {}, parameter = {}} = value;

				return {
					...prevState,
					errorMessage:
						errorMessage[editingLanguageId] !== undefined
							? errorMessage[editingLanguageId]
							: errorMessage[defaultLanguageId],
					parameter:
						parameter[editingLanguageId] !== undefined
							? parameter[editingLanguageId]
							: parameter[defaultLanguageId],
				};
			});
		}
	}, [defaultLanguageId, editingLanguageId, prevEditingLanguageId, value]);

	return (
		<ClayForm.Group className="lfr-ddm-form-field-validation">
			<Checkbox
				disabled={readOnly}
				label={label}
				name="enableValidation"
				onChange={(event, value) =>
					handleChange('enableValidation', value)
				}
				showAsSwitcher
				spritemap={spritemap}
				value={enableValidation}
				visible={visible}
			/>

			{enableValidation && (
				<>
					<Select
						amountValues={''}
						disableEmptyOption
						label={Liferay.Language.get('if-input')}
						name="selectedValidation"
						onChange={(event, value) =>
							handleChange(
								'selectedValidation',
								transformSelectedValidation(value)
							)
						}
						options={validations}
						placeholder={Liferay.Language.get('choose-an-option')}
						portletNamespace={''}
						priceField={''}
						readOnly={readOnly || localizationMode}
						spritemap={spritemap}
						value={[selectedValidation.name]}
						visible={visible}
					/>
					{!(
						dataProviders === undefined ||
						dataProviders.length === 0
					) &&
						selectedValidation.name === 'dataprovider' && (
							<Select
								disableEmptyOption
								label={Liferay.Language.get(
									'select-data-provider'
								)}
								name="selectedDataProvider"
								onChange={(event, value) =>
									handleChange(
										'selectedDataProvider',
										transformSelectedDataProvider(value)
									)
								}
								options={dataProviders}
								placeholder={Liferay.Language.get(
									'choose-a-data-provider'
								)}
								readOnly={readOnly || localizationMode}
								spritemap={spritemap}
								value={[selectedDataProvider.value]}
								visible={visible}
							/>
						)}
					{!(
						dataProviders === undefined ||
						dataProviders.length === 0
					) &&
						selectedValidation.name === 'dataprovider' && (
							<Select
								disableEmptyOption
								label={Liferay.Language.get(
									'select-data-provider-output'
								)}
								name="selectedDataProviderOutput"
								onChange={(event, value) =>
									handleChange(
										'selectedDataProviderOutput',
										transformSelectedDataProviderOutput(
											selectedDataProvider.outputs,
											value
										)
									)
								}
								options={selectedDataProvider.outputs}
								placeholder={Liferay.Language.get(
									'choose-a-data-provider-output'
								)}
								readOnly={readOnly || localizationMode}
								spritemap={spritemap}
								value={[selectedDataProviderOutput.value]}
								visible={visible}
							/>
						)}
					{selectedValidation.parameterMessage && (
						<DynamicComponent
							dataType={dataType}
							displayStyle={
								selectedValidation.label ===
								Liferay.Language.get('is-not-in-list')
									? 'multiline'
									: 'singleline'
							}
							label={Liferay.Language.get('the-value')}
							name={`${name}_parameter`}
							onChange={(event) =>
								handleChange('parameter', event.target.value)
							}
							placeholder={selectedValidation.parameterMessage}
							portletNamespace={''}
							priceField={''}
							readOnly={readOnly}
							required={false}
							spritemap={spritemap}
							value={parameter}
							visible={visible}
						/>
					)}
					<Text
						label={Liferay.Language.get('show-error-message')}
						name={`${name}_errorMessage`}
						onChange={(event) =>
							handleChange('errorMessage', event.target.value)
						}
						placeholder={Liferay.Language.get('show-error-message')}
						portletNamespace={''}
						priceField={''}
						readOnly={readOnly}
						required={false}
						spritemap={spritemap}
						value={errorMessage}
						visible={visible}
					/>
				</>
			)}
		</ClayForm.Group>
	);
};

const Main = ({
	dataType: initialDataType,
	defaultLanguageId,
	editingLanguageId,
	label,
	name,
	onChange,
	readOnly,
	spritemap,
	validation,
	validations: initialValidations,
	value = {},
	visible,
	dataProviders,
	hasAdvancedFormBuilder,
}) => {
	const data = transformData({
		dataProviders,
		defaultLanguageId,
		editingLanguageId,
		initialDataType,
		initialValidations,
		validation,
		value,
		hasAdvancedFormBuilder,
	});

	return (
		<Validation
			{...data}
			dataProviders={dataProviders}
			defaultLanguageId={defaultLanguageId}
			editingLanguageId={editingLanguageId}
			label={label}
			name={name}
			onChange={(value) => onChange({}, value)}
			readOnly={readOnly}
			spritemap={spritemap}
			validation={validation}
			value={value}
			visible={visible}
		/>
	);
};

export default Main;
