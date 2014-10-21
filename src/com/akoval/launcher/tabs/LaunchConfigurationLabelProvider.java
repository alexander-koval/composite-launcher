package com.akoval.launcher.tabs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

class LaunchConfigurationLabelProvider implements ILabelProvider {
	@Override
	public Image getImage(Object element) {
		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration configuration = (ILaunchConfiguration) element;
			try {
				ILaunchConfigurationType type = configuration.getType();
				Image image = DebugUITools.getSourceContainerImage(type
						.getIdentifier());
				return image;
			} catch (CoreException e) {
				return null;
			}

		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration configuration = (ILaunchConfiguration) element;
			String name = configuration.getName();
			return name;
		}
		return element.toString();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}
}