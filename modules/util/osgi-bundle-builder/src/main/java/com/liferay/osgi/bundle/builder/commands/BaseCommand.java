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

package com.liferay.osgi.bundle.builder.commands;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;

import aQute.lib.spring.SpringComponent;
import aQute.lib.strings.Strings;

import com.liferay.ant.bnd.jsp.JspAnalyzerPlugin;
import com.liferay.ant.bnd.npm.NpmAnalyzerPlugin;
import com.liferay.ant.bnd.resource.bundle.ResourceBundleLoaderAnalyzerPlugin;
import com.liferay.ant.bnd.sass.SassAnalyzerPlugin;
import com.liferay.ant.bnd.service.ServiceAnalyzerPlugin;
import com.liferay.ant.bnd.social.SocialAnalyzerPlugin;
import com.liferay.ant.bnd.spring.SpringDependencyAnalyzerPlugin;
import com.liferay.osgi.bundle.builder.OSGiBundleBuilderArgs;
import com.liferay.osgi.bundle.builder.internal.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;

/**
 * @author David Truong
 */
public abstract class BaseCommand implements Command {

	@Override
	public void build(OSGiBundleBuilderArgs osgiBundleBuilderArgs)
		throws Exception {

		Properties properties = FileUtil.readProperties(
			osgiBundleBuilderArgs.getBndFile());

		properties.setProperty("-plugin", String.join(",", _ANT_BND_PLUGINS));

		try (Builder builder = new Builder(new Processor(properties, false))) {
			builder.setBase(osgiBundleBuilderArgs.getBaseDir());

			File classesDir = osgiBundleBuilderArgs.getClassesDir();

			if ((classesDir != null) && classesDir.exists()) {
				Jar classesJar = new Jar(classesDir);

				classesJar.setManifest(new Manifest());

				File resourcesDir = osgiBundleBuilderArgs.getResourcesDir();

				if ((resourcesDir != null) && resourcesDir.exists()) {
					Jar resourcesJar = new Jar(resourcesDir);

					classesJar.addAll(resourcesJar);
				}

				builder.setJar(classesJar);
			}

			List<File> classpath = osgiBundleBuilderArgs.getClasspath();

			if ((classpath != null) && !classpath.isEmpty()) {
				List<Object> buildPath = new ArrayList<>(classpath.size());

				for (File file : classpath) {
					if (!file.exists()) {
						continue;
					}

					if (file.isDirectory()) {
						Jar jar = new Jar(file);

						builder.addClose(jar);

						builder.updateModified(
							jar.lastModified(), file.getPath());

						buildPath.add(jar);
					}
					else {
						builder.updateModified(
							file.lastModified(), file.getPath());

						buildPath.add(file);
					}
				}

				builder.setClasspath(buildPath);
				builder.setProperty(
					"project.buildpath",
					Strings.join(File.pathSeparator, buildPath));
			}

			Jar jar = builder.build();

			writeOutput(jar, osgiBundleBuilderArgs);
		}
	}

	protected abstract void writeOutput(
			Jar jar, OSGiBundleBuilderArgs osgiBundleBuilderArgs)
		throws Exception;

	private static final String[] _ANT_BND_PLUGINS = {
		SpringComponent.class.getName(), JspAnalyzerPlugin.class.getName(),
		NpmAnalyzerPlugin.class.getName(),
		ResourceBundleLoaderAnalyzerPlugin.class.getName(),
		SassAnalyzerPlugin.class.getName(),
		ServiceAnalyzerPlugin.class.getName(),
		SocialAnalyzerPlugin.class.getName(),
		SpringDependencyAnalyzerPlugin.class.getName()
	};

}