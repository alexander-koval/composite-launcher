package com.akoval.launcher.tabs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.akoval.launcher.CompositeLaunchConstants;

public class LaunchTab extends AbstractLaunchConfigurationTab implements
		ILaunchConfigurationTab {

	protected TableViewer tableViewer;
	protected Button addButton;
	protected Button editButton;
	protected Button removeButton;

	@Override
	public void createControl(Composite parent) {
		// Create main composite
		Composite mainComposite = SWTFactory.createComposite(parent, 2, 1,
				GridData.FILL_HORIZONTAL);
		setControl(mainComposite);

		createEnvironmentTable(mainComposite);
		createTableButtons(mainComposite);
		Dialog.applyDialogFont(mainComposite);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		System.out.println("SET DEFAULTS");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		try {
			List<String> list = configuration.getAttribute(
					CompositeLaunchConstants.MEMENTO, new ArrayList<String>());
			for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
				String memento = it.next();
				ILaunchConfiguration config = manager.getLaunchConfiguration(memento);
				tableViewer.add(config);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		TableItem[] items = tableViewer.getTable().getItems();
		ArrayList<String> list = new ArrayList<String>();
		for (int index = 0; index < items.length; index++) {
			TableItem item = items[index];
			ILaunchConfiguration config = (ILaunchConfiguration) item.getData();
			ILaunchConfigurationType type;
			try {
				type = config.getType();
				String memento = config.getMemento();
				list.add(memento);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		configuration.setAttribute(CompositeLaunchConstants.MEMENTO, list);
	}

	@Override
	public String getName() {
		return "Launches";
	}

	protected void createEnvironmentTable(Composite parent) {
		Font font = parent.getFont();
		// Create table composite
		Composite tableComposite = SWTFactory.createComposite(parent, font, 1,
				1, GridData.FILL_BOTH, 0, 0);
		// Create table
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayout(new GridLayout());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font);
		tableViewer
				.setContentProvider(new LaunchConfigurationContentProvider());
		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						editButton.setEnabled(true);
						removeButton.setEnabled(true);
					}
				});
		// Create columns
		final TableColumn tableColumn = new TableColumn(table, SWT.NONE, 0);
		tableColumn.setText("Name");
		final Composite comp = tableComposite;
		tableComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle area = comp.getClientArea();
				tableColumn.setWidth(area.width);
			}
		});
	}

	protected void createTableButtons(Composite parent) {
		// Create button composite
		Composite buttonComposite = SWTFactory.createComposite(parent,
				parent.getFont(), 1, 1, GridData.VERTICAL_ALIGN_BEGINNING
						| GridData.HORIZONTAL_ALIGN_END, 0, 0);

		// Create buttons
		addButton = createPushButton(buttonComposite, "Add", null);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ILaunchManager manager = DebugPlugin.getDefault()
						.getLaunchManager();
				ILaunchConfiguration[] configurations;
				Shell shell = getShell();
				try {
					configurations = manager.getLaunchConfigurations();
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(
							shell, new LaunchConfigurationLabelProvider());
					dialog.setElements(configurations);
					dialog.setTitle("Add Launch Configuration");
					if (dialog.open() != Window.CANCEL) {
						Object[] result = dialog.getResult();
						tableViewer.add(result);
						updateLaunchConfigurationDialog();
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});

		removeButton = createPushButton(buttonComposite, "Remove", null);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ISelection selection = tableViewer.getSelection();
				StructuredSelection structed = (StructuredSelection) selection;
				ILaunchConfiguration configuration = (ILaunchConfiguration) structed
						.getFirstElement();
				tableViewer.remove(configuration);
				removeButton.setEnabled(false);
				editButton.setEnabled(false);
				updateLaunchConfigurationDialog();
			}
		});
		removeButton.setEnabled(false);
		editButton = createPushButton(buttonComposite, "Edit", null);
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ILaunchManager manager = DebugPlugin.getDefault()
						.getLaunchManager();
				ILaunchConfiguration[] configurations;
				Shell shell = getShell();
				try {
					configurations = manager.getLaunchConfigurations();
					ElementListSelectionDialog dialog = new ElementListSelectionDialog(
							shell, new LaunchConfigurationLabelProvider());
					dialog.setElements(configurations);
					dialog.setTitle("Edit Launch Configuration");
					if (dialog.open() != Window.CANCEL) {
						Object result = dialog.getResult()[0];
						int selectionIndex = tableViewer.getTable()
								.getSelectionIndex();
						tableViewer.replace(result, selectionIndex);
						updateLaunchConfigurationDialog();
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected class LaunchConfigurationContentProvider implements
			IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			ILaunchConfiguration[] configurations = null;
			try {
				configurations = DebugPlugin.getDefault().getLaunchManager()
						.getLaunchConfigurations();
			} catch (CoreException e) {
				return new ILaunchConfiguration[0];
			}
			return configurations;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected class LaunchConfigurationLabelProvider implements ILabelProvider {
		@Override
		public Image getImage(Object element) {
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
}
