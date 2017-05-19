/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package dayton.ellwanger.ecfchat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;
import org.eclipse.ecf.provider.xmpp.XMPPContainer;

public class MyXMPPContainerInstantiator extends GenericContainerInstantiator {
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(
	 * ContainerDescription, java.lang.Object[])
	 */
	public IContainer createInstance(ContainerTypeDescription description,
			Object[] args) throws ContainerCreateException {
		try {
			return new MyXMPPContainer();
		} catch (Exception e) {
			throw new ContainerCreateException(
					"Exception creating generic container", e);
		}
	}

	private static final String XMPP_CONFIG = "ecf.xmpp.smack"; //$NON-NLS-1$

	public String[] getImportedConfigs(ContainerTypeDescription description,
			String[] exporterSupportedConfigs) {
		if (exporterSupportedConfigs == null)
			return null;
		List results = new ArrayList();
		List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
		if (XMPP_CONFIG.equals(description.getName())) {
			if (supportedConfigs.contains(XMPP_CONFIG))
				results.add(XMPP_CONFIG);
		}
		if (supportedConfigs.size() == 0)
			return null;
		return (String[]) results.toArray(new String[] {});
	}

	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return new String[] { XMPP_CONFIG };
	}

}
