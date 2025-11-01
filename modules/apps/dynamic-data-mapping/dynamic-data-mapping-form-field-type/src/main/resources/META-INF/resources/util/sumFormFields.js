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

// const sumFormFieldsValues = (portletNamespace) =>{

export function sumFormFieldsValues(portletNamespace) {
	try {
		if (!portletNamespace.includes('Admin')) {
			let showSumOfPayment = false;
			const elements = document.getElementById(portletNamespace + 'fm')
				.elements;
			const elements2 = document.getElementById(portletNamespace + 'fm')
				.elements;
			let sum = 0;
			for (let i = 0; i < elements.length; i++) {
				const dataPriceField = elements[i].getAttribute(
					'data-price-field'
				);
				const elementType = elements[i].getAttribute('type');
				if (elementType === 'radio') {
					for (let j = 0; j < elements2.length; j++) {
						const jElementType = elements2[j].getAttribute('type');
						const jElementValue = elements2[j].getAttribute(
							'value'
						);
						if (
							jElementType === 'hidden' &&
							jElementValue === elements[i].value &&
							elements[i].getAttribute('data-price-value') !== ''
						) {
							sum += parseInt(
								!isNaN(
									elements[i].getAttribute('data-price-value')
								)
									? elements[i].getAttribute(
											'data-price-value'
									  )
									: '0',
								10
							);
							showSumOfPayment = true;
						}
					}
				}
				if (
					elementType === 'text' &&
					dataPriceField === 'price-field' &&
					elements[i].getAttribute('value') !== ''
				) {
					sum += parseInt(
						!isNaN(elements[i].getAttribute('value'))
							? elements[i].getAttribute('value')
							: '0',
						10
					);
					showSumOfPayment = true;
				}
				if (
					elementType === 'hidden' &&
					dataPriceField === 'price-field' &&
					elements[i].getAttribute('data-price-value') !== ''
				) {
					sum += parseInt(
						!isNaN(elements[i].getAttribute('data-price-value'))
							? elements[i].getAttribute('data-price-value')
							: '0',
						10
					);
					showSumOfPayment = true;
				}
			}
			if (showSumOfPayment) {
				const nfFa = new Intl.NumberFormat('fa-IR');
				document.getElementById(
					portletNamespace + 'sumOfFields'
				).innerHTML =
					Liferay.Language.get('sum-of-payment') +
					': ' +
					nfFa.format(sum) +
					'<br/>' +
					numberToTomanWords(sum);
			}
		}
	}
	catch (e) {}
}

function numberToTomanWords(rialValue) {
	if (typeof rialValue === 'string') {
		rialValue = parseInt(rialValue.replace(/[^\d]/g, ''), 10);
	}
	if (isNaN(rialValue)) {
		return '';
	}

	const toman = Math.floor(rialValue / 10); // تبدیل ریال به تومان

	const yekan = [
		'',
		'یک',
		'دو',
		'سه',
		'چهار',
		'پنج',
		'شش',
		'هفت',
		'هشت',
		'نه',
	];
	const dahgan = [
		'',
		'',
		'بیست',
		'سی',
		'چهل',
		'پنجاه',
		'شصت',
		'هفتاد',
		'هشتاد',
		'نود',
	];
	const sadgan = [
		'',
		'یکصد',
		'دویست',
		'سیصد',
		'چهارصد',
		'پانصد',
		'ششصد',
		'هفتصد',
		'هشتصد',
		'نهصد',
	];
	const dah = [
		'ده',
		'یازده',
		'دوازده',
		'سیزده',
		'چهارده',
		'پانزده',
		'شانزده',
		'هفده',
		'هجده',
		'نوزده',
	];
	const groups = ['', 'هزار', 'میلیون', 'میلیارد', 'تریلیون'];

	function threeDigitsToWord(n) {
		let word = '';
		const s = Math.floor(n / 100);
		const d = Math.floor((n % 100) / 10);
		const y = n % 10;

		if (s !== 0) {
			word += sadgan[s];
		}
		if (word && (d !== 0 || y !== 0)) {
			word += ' و ';
		}

		if (d === 1) {
			word += dah[y];
		}
		else {
			if (d > 1) {
				word += dahgan[d];
			}
			if (d > 1 && y !== 0) {
				word += ' و ';
			}
			if (d !== 1 && y !== 0) {
				word += yekan[y];
			}
		}

		return word;
	}

	const numStr = toman.toString();
	const groupsArr = [];
	for (let i = 0; i < Math.ceil(numStr.length / 3); i++) {
		const end = numStr.length - i * 3;
		const start = Math.max(0, end - 3);
		groupsArr.push(parseInt(numStr.substring(start, end), 10));
	}

	let result = '';
	for (let j = groupsArr.length - 1; j >= 0; j--) {
		const part = groupsArr[j];
		if (part !== 0) {
			if (result !== '') {
				result += ' و ';
			}
			result +=
				threeDigitsToWord(part) + (groups[j] ? ' ' + groups[j] : '');
		}
	}

	return result ? result + ' تومان' : 'صفر تومان';
}
