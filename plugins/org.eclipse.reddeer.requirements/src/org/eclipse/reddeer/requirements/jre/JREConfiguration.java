/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.reddeer.requirements.jre;

import org.eclipse.reddeer.junit.requirement.configuration.RequirementConfiguration;

/**
 * Class contains JRE configuration used by JRE requirement
 * that is configurable from outside via Requirement feature. 
 *
 */
public class JREConfiguration implements RequirementConfiguration {
	
	private String name;
	
	private String path;
	
	private String version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getId() {
		return "jre-" + version;
	}
}
