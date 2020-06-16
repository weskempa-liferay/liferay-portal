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

import React from 'react';

import lang from '../utils/lang.es';

export default ({text}) => (
	<>
		{text && text.length < 23 && (
			<p className="float-right small text-secondary">
				{lang.sub(Liferay.Language.get('x-characters-left'), [
					15 - (text.length ? text.length - 8 : text.length),
				])}
			</p>
		)}
	</>
);
