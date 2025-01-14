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

package com.liferay.portal.plugin;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jorge Ferrer
 */
public class RepositoryReport implements Serializable {

	public static final String SUCCESS = "success";

	public void addError(
		String repositoryURL, PluginPackageException pluginPackageException) {

		StringBundler sb = new StringBundler(2);

		if (Validator.isNotNull(pluginPackageException.getMessage())) {
			sb.append(pluginPackageException.getMessage());
		}

		if (pluginPackageException.getCause() != null) {
			Throwable throwable = pluginPackageException.getCause();

			if (Validator.isNotNull(throwable.getMessage())) {
				sb.append(throwable.getMessage());
			}
		}

		if (sb.index() == 0) {
			sb.append(pluginPackageException.toString());
		}

		_reportMap.put(repositoryURL, sb.toString());
	}

	public void addSuccess(String repositoryURL) {
		_reportMap.put(repositoryURL, SUCCESS);
	}

	public Set<String> getRepositoryURLs() {
		return _reportMap.keySet();
	}

	public String getState(String repositoryURL) {
		return _reportMap.get(repositoryURL);
	}

	@Override
	public String toString() {
		Set<String> repositoryURLs = getRepositoryURLs();

		if (repositoryURLs.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(repositoryURLs.size() * 3);

		for (String repositoryURL : repositoryURLs) {
			sb.append(repositoryURL);
			sb.append(": ");
			sb.append(_reportMap.get(repositoryURL));
		}

		return sb.toString();
	}

	private final Map<String, String> _reportMap = new TreeMap<>();

}