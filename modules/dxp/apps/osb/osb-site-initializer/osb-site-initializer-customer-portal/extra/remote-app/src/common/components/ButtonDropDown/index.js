/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useState} from 'react';

const ButtonDropDown = ({
	label,
	align = Align.BottomRight,
	items,
	...props
}) => {
	const [active, setActive] = useState(false);

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={align}
			onActiveChange={setActive}
			trigger={
				<button className="btn btn-primary">
					{label}

					<ClayIcon className="ml-2" symbol="caret-bottom" />
				</button>
			}
			{...props}
		>
			<ClayDropDown.ItemList>
				{items.map(({disabled, icon, label, onClick}) => (
					<ClayDropDown.Item
						className={classNames(
							'font-weight-semi-bold px-3 rounded-xs',
							{
								'common-drop-down-item text-neutral-8': !disabled,
								'text-neutral-5': disabled,
							}
						)}
						disabled={disabled}
						key={label}
						onClick={onClick}
					>
						<div className="d-flex">
							{icon && <div className="mr-1">{icon}</div>}

							{label}
						</div>
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
};

export default ButtonDropDown;
