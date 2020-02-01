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

package com.liferay.jenkins.results.parser.spira;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class SpiraRelease {

	public int getID() {
		return _jsonObject.getInt("ReleaseId");
	}

	public String getName() {
		return _jsonObject.getString("Name");
	}

	public String getPath() {
		String name = getName();

		name = name.replace("/", "\\/");

		if (_parentSpiraRelease == null) {
			return "/" + name;
		}

		return JenkinsResultsParserUtil.combine(
			_parentSpiraRelease.getPath(), "/", name.replace("/", "\\/"));
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject(_jsonObject.toString());

		jsonObject.put("Path", getPath());

		return jsonObject;
	}

	@Override
	public String toString() {
		JSONObject jsonObject = toJSONObject();

		return jsonObject.toString();
	}

	protected static List<SpiraRelease> getSpiraReleases(
			SpiraProject spiraProject, SearchParameter... searchParameters)
		throws IOException {

		List<SpiraRelease> spiraReleases = new ArrayList<>();

		for (SpiraRelease spiraRelease : _spiraReleases.values()) {
			if (spiraRelease.matches(searchParameters)) {
				spiraReleases.add(spiraRelease);
			}
		}

		if (!spiraReleases.isEmpty()) {
			return spiraReleases;
		}

		Map<String, String> urlParameters = new HashMap<>();

		urlParameters.put("number_rows", String.valueOf(_NUMBER_ROWS));
		urlParameters.put("start_row", String.valueOf(_START_ROW));

		Map<String, String> urlPathReplacements = new HashMap<>();

		urlPathReplacements.put(
			"project_id", String.valueOf(spiraProject.getID()));

		JSONArray requestJSONArray = new JSONArray();

		for (SearchParameter searchParameter : searchParameters) {
			requestJSONArray.put(searchParameter.toFilterJSONObject());
		}

		JSONArray responseJSONArray = SpiraRestAPIUtil.requestJSONArray(
			"projects/{project_id}/releases/search", urlParameters,
			urlPathReplacements, HttpRequestMethod.POST,
			requestJSONArray.toString());

		for (int i = 0; i < responseJSONArray.length(); i++) {
			SpiraRelease spiraRelease = new SpiraRelease(
				responseJSONArray.getJSONObject(i));

			_spiraReleases.put(
				_createSpiraReleaseKey(
					spiraProject.getID(), spiraRelease.getID()),
				spiraRelease);

			if (spiraRelease.matches(searchParameters)) {
				spiraReleases.add(spiraRelease);
			}
		}

		return spiraReleases;
	}

	protected SpiraRelease(JSONObject jsonObject) {
		_jsonObject = jsonObject;
		_spiraProject = SpiraProject.getSpiraProjectById(
			jsonObject.getInt("ProjectId"));

		SpiraRelease parentSpiraRelease = null;

		String indentLevel = getIndentLevel();

		if (indentLevel.length() > 3) {
			String parentIndentLevel = indentLevel.substring(
				0, indentLevel.length() - 3);

			try {
				parentSpiraRelease = _spiraProject.getSpiraReleaseByIndentLevel(
					parentIndentLevel);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		_parentSpiraRelease = parentSpiraRelease;
	}

	protected String getIndentLevel() {
		return _jsonObject.getString("IndentLevel");
	}

	protected boolean matches(SearchParameter... searchParameters) {
		return SearchParameter.matches(toJSONObject(), searchParameters);
	}

	private static String _createSpiraReleaseKey(int projectID, int releaseID) {
		return projectID + "-" + releaseID;
	}

	private static final int _NUMBER_ROWS = 15000;

	private static final int _START_ROW = 1;

	private static final Map<String, SpiraRelease> _spiraReleases =
		new HashMap<>();

	private final JSONObject _jsonObject;
	private final SpiraRelease _parentSpiraRelease;
	private final SpiraProject _spiraProject;

}