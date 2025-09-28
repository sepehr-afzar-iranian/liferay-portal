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

import ClayAutocomplete from '@clayui/autocomplete';
import {ClayInput} from '@clayui/form';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {useSyncValue} from '../hooks/useSyncValue.es';

const ALLOWED_LETTERS = [
	'\u0627\u0644\u0641',
	'\u0628',
	'\u067e',
	'\u062a',
	'\u062b',
	'\u062c',
	'\u0686',
	'\u062d',
	'\u062e',
	'\u062f',
	'\u0630',
	'\u0631',
	'\u0632',
	'\u0698',
	'\u0633',
	'\u0634',
	'\u0635',
	'\u0636',
	'\u0637',
	'\u0638',
	'\u0639',
	'\u063a',
	'\u0641',
	'\u0642',
	'\u06a9',
	'\u06af',
	'\u0644',
	'\u0645',
	'\u0646',
	'\u0648',
	'\u0647',
	'\u06cc',
];
const IRANIAN_LICENSE_PLATE_PATTERN = /^-(\d{2})-(__|\u0627\u0644\u0641|\u0628|\u067e|\u062a|\u062b|\u062c|\u0686|\u062d|\u062e|\u062f|\u0630|\u0631|\u0632|\u0698|\u0633|\u0634|\u0635|\u0636|\u0637|\u0638|\u0639|\u063a|\u0641|\u0642|\u06a9|\u06af|\u0644|\u0645|\u0646|\u0648|\u0647|\u06cc)-(\d{3})-(\d{2})-$/;
const pathThemeImages = Liferay.ThemeDisplay.getPathThemeImages();

const LicensePlateField = ({
	disabled,
	id,
	name,
	onBlur,
	onChange,
	onFocus,
	syncDelay,
	validateIranianFormat,
	value: initialValue,
}) => {
	const [value, setValue] = useSyncValue(initialValue, syncDelay);
	const [firstNumbers, setFirstNumbers] = useState('');
	const [letter, setLetter] = useState('');
	const [secondNumbers, setSecondNumbers] = useState('');
	const [regionCode, setRegionCode] = useState('');
	const [showLetterSuggestions, setShowLetterSuggestions] = useState(false);

	const firstNumbersRef = useRef(null);
	const letterRef = useRef(null);
	const secondNumbersRef = useRef(null);
	const regionRef = useRef(null);

	const handleChange = useCallback(
		(newValue) => {
			onChange({target: {value: newValue}});
		},
		[onChange]
	);

	const stableSetValue = useCallback(
		(newValue) => {
			setValue(newValue);
		},
		[setValue]
	);

	useEffect(() => {
		if (value && typeof value === 'string') {
			const match = value.match(IRANIAN_LICENSE_PLATE_PATTERN);
			if (match) {
				setFirstNumbers(match[1]);
				setLetter(match[2]);
				setSecondNumbers(match[3]);
				setRegionCode(match[4]);
			}
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		const hasAnyInput =
			firstNumbers || letter || secondNumbers || regionCode;
		if (hasAnyInput) {
			const newValue = `-${firstNumbers || '00'}-${letter || '__'}-${
				secondNumbers || '000'
			}-${regionCode || '00'}-`;
			if (newValue !== value) {
				stableSetValue(newValue);
				handleChange(newValue);
			}
		}
		else if (value) {
			stableSetValue('');
			handleChange('');
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [firstNumbers, letter, secondNumbers, regionCode]);

	const handleFirstNumbersChange = (event) => {
		const newValue = event.target.value.replace(/[^\d]/g, '').slice(0, 2);
		setFirstNumbers(newValue);
		if (newValue.length === 2) {
			letterRef.current?.focus();
		}
	};

	const handleSecondNumbersChange = (event) => {
		const newValue = event.target.value.replace(/[^\d]/g, '').slice(0, 3);
		setSecondNumbers(newValue);
		if (newValue.length === 3) {
			regionRef.current?.focus();
		}
	};

	const handleLetterChange = (event) => {
		const inputValue = event.target.value;
		const lastChar = inputValue.slice(-1);
		if (!inputValue || ALLOWED_LETTERS.includes(lastChar)) {
			setLetter(lastChar);
			setShowLetterSuggestions(
				!!inputValue && !ALLOWED_LETTERS.includes(lastChar)
			);
			if (ALLOWED_LETTERS.includes(lastChar)) {
				setShowLetterSuggestions(false);
				secondNumbersRef.current?.focus();
			}
		}
	};

	const handleRegionCodeChange = (event) => {
		const newValue = event.target.value.replace(/[^\d]/g, '').slice(0, 2);
		setRegionCode(newValue);
	};

	const handleLetterSelect = (selectedLetter) => {
		setLetter(selectedLetter);
		setShowLetterSuggestions(false);
		secondNumbersRef.current?.focus();
	};

	const handleKeyDown = (event, nextRef, prevRef) => {
		if (event.key === 'ArrowRight' && nextRef?.current) {
			nextRef.current.focus();
		}
		else if (event.key === 'ArrowLeft' && prevRef?.current) {
			prevRef.current.focus();
		}
	};

	const filteredLetters = ALLOWED_LETTERS.filter(
		(l) => l.includes(letter) || letter === ''
	);

	return (
		<div className="license-plate-container">
			<div
				className="license-plate-field"
				style={{
					alignItems: 'end',
					display: 'flex',
					gap: '8px',
				}}
			>
				<div
					style={{
						alignItems: 'center',
						display: 'flex',
						flexDirection: 'column',
						gap: '5px',
					}}
				>
					<span>
						{Liferay.Language.get('license-plate-ir-country-label')}
					</span>
					<ClayInput
						disabled={disabled}
						maxLength={2}
						onChange={handleRegionCodeChange}
						onKeyDown={(e) => handleKeyDown(e, null, letterRef)}
						placeholder="10"
						ref={regionRef}
						sizing={'sm'}
						style={{
							textAlign: 'center',
							width: '50px',
						}}
						value={regionCode}
					/>
				</div>
				<ClayInput
					disabled={disabled}
					maxLength={3}
					onChange={handleSecondNumbersChange}
					onKeyDown={(e) => handleKeyDown(e, regionRef, letterRef)}
					placeholder="123"
					ref={secondNumbersRef}
					sizing={'sm'}
					style={{
						textAlign: 'center',
						width: '60px',
					}}
					value={secondNumbers}
				/>
				<div style={{position: 'relative'}}>
					<ClayAutocomplete>
						<ClayAutocomplete.Input
							disabled={disabled}
							maxLength={3}
							onChange={handleLetterChange}
							onFocus={() => setShowLetterSuggestions(true)}
							onKeyDown={(e) =>
								handleKeyDown(
									e,
									secondNumbersRef,
									firstNumbersRef
								)
							}
							placeholder="الف"
							ref={letterRef}
							sizing={'sm'}
							style={{
								textAlign: 'center',
								width: '70px',
							}}
							value={letter}
						/>
						<ClayAutocomplete.DropDown
							active={
								showLetterSuggestions &&
								!disabled &&
								filteredLetters.length > 0
							}
							onSetActive={setShowLetterSuggestions}
						>
							<ul className="list-unstyled">
								{filteredLetters.map((l) => (
									<ClayAutocomplete.Item
										key={l}
										onClick={() => handleLetterSelect(l)}
										style={{
											fontSize: '16px',
											textAlign: 'center',
										}}
										value={l}
									>
										{l}
									</ClayAutocomplete.Item>
								))}
							</ul>
						</ClayAutocomplete.DropDown>
					</ClayAutocomplete>
				</div>
				<ClayInput
					disabled={disabled}
					maxLength={2}
					onChange={handleFirstNumbersChange}
					onKeyDown={(e) => handleKeyDown(e, letterRef, null)}
					placeholder="12"
					ref={firstNumbersRef}
					sizing={'sm'}
					style={{
						textAlign: 'center',
						width: '50px',
					}}
					value={firstNumbers}
				/>
				<div
					style={{
						alignItems: 'center',
						display: 'flex',
						flexDirection: 'column',
						marginBottom: '5px',
					}}
				>
					<div
						style={{
							borderRadius: '2px',
							height: '20px',
							marginBottom: '6px',
							width: '30px',
						}}
					>
						<img
							alt={pathThemeImages}
							src={`${pathThemeImages}/clay/flags-fa-IR.svg`}
							title="flag"
						/>
					</div>
				</div>
			</div>
			{validateIranianFormat &&
				value &&
				!IRANIAN_LICENSE_PLATE_PATTERN.test(value) && (
					<div
						style={{
							color: '#dc3545',
							fontSize: '12px',
							marginTop: '4px',
							textAlign: 'right',
						}}
					>
						{Liferay.Language.get('license-plate-error-message')}
					</div>
				)}
			<input
				id={id}
				name={name}
				onBlur={onBlur}
				onFocus={onFocus}
				type="hidden"
				value={value}
			/>
		</div>
	);
};

const Main = ({
	id,
	localizedValue = {},
	name,
	onBlur,
	onChange,
	onFocus,
	placeholder,
	predefinedValue = '',
	readOnly,
	syncDelay = true,
	value,
	validateIranianFormat,
	...otherProps
}) => {
	const fieldDetailsId = id ? id + '_fieldDetails' : name + '_fieldDetails';

	return (
		<FieldBase
			{...otherProps}
			id={id}
			localizedValue={localizedValue}
			name={name}
			readOnly={readOnly}
		>
			<LicensePlateField
				disabled={readOnly}
				id={fieldDetailsId}
				name={name}
				onBlur={onBlur}
				onChange={onChange}
				onFocus={onFocus}
				placeholder={placeholder}
				syncDelay={syncDelay}
				validateIranianFormat={validateIranianFormat}
				value={value || predefinedValue}
			/>
		</FieldBase>
	);
};

Main.displayName = 'LicensePlate';

export default Main;
