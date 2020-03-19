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

import './SelectRegister.soy';

import ClayDropDown from '@clayui/drop-down';
import React, {forwardRef, useRef, useState} from 'react';

import {FieldBaseProxy} from '../FieldBase/ReactFieldBase.es';
import getConnectedReactComponentAdapter from '../util/ReactComponentAdapter.es';
import {connectStore} from '../util/connectStore.es';
import templates from './SelectAdapter.soy';
import HiddenSelectInput from './components/HiddenSelectInput.es';
import VisibleSelectInput from './components/VisibleSelectInput.es';

/**
 * Appends a new value on the current value state
 * @param options {Object}
 * @param options.value {Array|String}
 * @param options.valueToBeAppended {Array|String}
 * @returns {Array}
 */
function appendValue({value, valueToBeAppended}) {
	const currentValue = toArray(value);
	const newValue = [...currentValue];

	if (value) {
		newValue.push(valueToBeAppended);
	}

	return newValue;
}

/**
 * Removes a value from the value array.
 * @param options {Object}
 * @param options.value {Array|String}
 * @param options.valueToBeRemoved {Array|String}
 * @returns {Array}
 */
function removeValue({value, valueToBeRemoved}) {
	const currentValue = toArray(value);

	return currentValue.filter(v => v !== valueToBeRemoved);
}

/**
 * Wraps the given argument into an array.
 * @param value {Array|String}
 */
function toArray(value = '') {
	let newValue = value;

	if (!Array.isArray(newValue)) {
		newValue = [newValue];
	}

	return newValue;
}

/**
 * Checks if the array is empty
 * @param arr argument to be checked if it's empty or not.
 * @returns {Boolean}
 */
function isArrayEmpty(arr) {
	if (!Array.isArray(arr) || arr.length === 0) {
		return false;
	}

	return arr.some(value => value !== '') === false;
}

function normalizeValue({
	multiple,
	normalizedOptions,
	predefinedValueArray,
	valueArray,
}) {
	const assertValue = isArrayEmpty(valueArray)
		? predefinedValueArray
		: valueArray;

	const valueWithoutMultiple = assertValue.filter((_, index) => {
		return multiple ? true : index === 0;
	});

	return valueWithoutMultiple.filter(value =>
		normalizedOptions.some(option => value === option.value)
	);
}

/**
 * Some parameters on each option
 * needs to be prepared in case of
 * multiple selected values(when the value state is an array).
 *
 */
function assertOptionParameters({multiple, option, valueArray}) {
	const included = valueArray.includes(option.value);

	return {
		...option,
		active: !multiple && included,
		checked: multiple && included,
		type: multiple ? 'checkbox' : 'item',
	};
}

function normalizeOptions({fixedOptions, multiple, options, valueArray}) {
	const emptyOption = {
		label: Liferay.Language.get('choose-an-option'),
		value: '',
	};

	const newOptions = [...options]
		.map((option, index) => {
			return {
				...assertOptionParameters({multiple, option, valueArray}),
				separator:
					Array.isArray(fixedOptions) &&
					fixedOptions.length > 0 &&
					index === options.length - 1,
			};
		})
		.concat(
			fixedOptions.map(option =>
				assertOptionParameters({multiple, option, valueArray})
			)
		)
		.filter(({value}) => value !== '');

	if (!multiple) {
		return [emptyOption, ...newOptions];
	}

	return newOptions;
}

function handleDropdownItemClick({currentValue, multiple, option}) {
	const itemValue = option.value;

	let newValue;

	if (multiple) {
		if (currentValue.includes(itemValue)) {
			newValue = removeValue({
				value: currentValue,
				valueToBeRemoved: itemValue,
			});

			// Forces the active element to be blurred.
			if (document.activeElement) {
				document.activeElement.blur();
			}
		}
		else {
			newValue = appendValue({
				value: currentValue,
				valueToBeAppended: itemValue,
			});
		}
	}
	else {
		newValue = toArray(itemValue);
	}

	return newValue;
}

const Trigger = forwardRef(
	(
		{
			onCloseButtonClicked,
			onTriggerClicked,
			readOnly,
			value,
			...otherProps
		},
		ref
	) => {
		return (
			<>
				{!readOnly && (
					<HiddenSelectInput value={value} {...otherProps} />
				)}
				<VisibleSelectInput
					onClick={onTriggerClicked}
					onCloseButtonClicked={onCloseButtonClicked}
					ref={ref}
					value={value}
					{...otherProps}
				/>
			</>
		);
	}
);

const Select = ({
	multiple,
	onCloseButtonClicked,
	onDropdownItemClicked,
	onExpand,
	options,
	predefinedValue,
	readOnly,
	value,
	...otherProps
}) => {
	const menuElementRef = useRef(null);
	const triggerElementRef = useRef(null);

	const [currentValue, setCurrentValue] = useState(value);
	const [expand, setExpand] = useState(false);

	return (
		<div>
			<Trigger
				onCloseButtonClicked={({event, value}) => {
					const newValue = removeValue({
						value: currentValue,
						valueToBeRemoved: value,
					});

					setCurrentValue(newValue);

					onCloseButtonClicked({event, value: newValue});
				}}
				onTriggerClicked={event => {
					if (readOnly) {
						return;
					}

					setExpand(!expand);
					onExpand({event, expand: !expand});
				}}
				options={options}
				predefinedValue={predefinedValue}
				readOnly={readOnly}
				ref={triggerElementRef}
				value={currentValue}
				{...otherProps}
			/>
			<ClayDropDown.Menu
				active={expand}
				alignElementRef={triggerElementRef}
				className="ddm-btn-full ddm-select-dropdown"
				onSetActive={setExpand}
				ref={menuElementRef}
			>
				<ClayDropDown.ItemList>
					{options.map((option, index) => (
						<ClayDropDown.Item
							active={expand && currentValue === option.label}
							className="ddm-btn-full ddm-select-dropdown"
							data-testid={`dropdownItem-${index}`}
							key={`dropdown-option-${index}`}
							label={option.label}
							onClick={event => {
								const newValue = handleDropdownItemClick({
									currentValue,
									multiple,
									option,
								});

								setCurrentValue(newValue);

								event.preventDefault();

								onDropdownItemClicked({event, value: newValue});
							}}
							value={options.value}
						>
							{option.label}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.ItemList>
			</ClayDropDown.Menu>
		</div>
	);
};

const SelectProxy = connectStore(
	({
		emit,
		fixedOptions = [],
		label,
		localizedValue = {},
		multiple,
		name,
		options = [],
		predefinedValue = [],
		readOnly,
		value = [],
		...otherProps
	}) => {
		const predefinedValueArray = toArray(predefinedValue);
		const valueArray = toArray(value);

		const normalizedOptions = normalizeOptions({
			fixedOptions,
			multiple,
			options,
			valueArray,
		});

		value = normalizeValue({
			multiple,
			normalizedOptions,
			predefinedValueArray,
			valueArray,
		});

		return (
			<FieldBaseProxy
				label={label}
				localizedValue={localizedValue}
				name={name}
				readOnly={readOnly}
				{...otherProps}
			>
				<Select
					multiple={multiple}
					name={name}
					onCloseButtonClicked={({event, value}) =>
						emit('fieldEdited', event, value)
					}
					onDropdownItemClicked={({event, value}) =>
						emit('fieldEdited', event, value)
					}
					onExpand={({event, expand}) => {
						if (expand) {
							emit('fieldFocused', event, event.target.value);
						}
						else {
							emit('fieldBlurred', event, event.target.value);
						}
					}}
					options={normalizedOptions}
					predefinedValue={predefinedValueArray}
					readOnly={readOnly}
					value={value}
					{...otherProps}
				/>
			</FieldBaseProxy>
		);
	}
);

const ReactSelectAdapter = getConnectedReactComponentAdapter(
	SelectProxy,
	templates
);

export {ReactSelectAdapter};

export default ReactSelectAdapter;
