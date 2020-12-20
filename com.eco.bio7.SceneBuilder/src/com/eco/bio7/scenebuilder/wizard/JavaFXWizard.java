/*******************************************************************************
 * Copyright (c) 2015 M. Austenfeld
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     M. Austenfeld
 *******************************************************************************/
package com.eco.bio7.scenebuilder.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

public class JavaFXWizard extends Wizard implements INewWizard {
	private JavaFXWizardPage page;
	private ISelection selection;

	public JavaFXWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		page = new JavaFXWizardPage(selection);
		addPage(page);
	}

	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}

	private void doFinish(String containerName, String fileName,
			IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating " + fileName, 2);
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName
					+ "\" does not exist.");
		}
		
		//System.out.println(resource.getProject().getFullPath());
		
		IContainer container = (IContainer) resource;
		
		final IFile file = container.getFile(new Path(fileName));
		
		
		String an = "Main";
		int index = file.getName().lastIndexOf('.');
	      if (index>0&& index <= file.getName().length() - 2 ) {
	      an=file.getName().substring(0, index);
	      }  
	    final IFile fileFxml = container.getFile(new Path(an+".fxml"));
	  
		try {
			InputStream stream = openContentStreamJavaFile(an,resource);
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		
		String an2 = "main";
		int index2 = fileFxml.getName().lastIndexOf('.');
	      if (index2>0&& index2 <= fileFxml.getName().length() - 2 ) {
	      an2=fileFxml.getName().substring(0, index);
	      }  
		/*Create*.fxml file with the same filename!*/
		try {
			InputStream stream = openContentStreamFXMLFile(an2);
			if (fileFxml.exists()) {
				fileFxml.setContents(stream, true, true, monitor);
			} else {
				fileFxml.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	private InputStream openContentStreamJavaFile(String filename, IResource resource) {
		String linefeed = "\r\n";
		
		String head="package application;"+linefeed+linefeed+
	    "import javafx.application.Application;"+linefeed+
		"import javafx.fxml.FXMLLoader;"+linefeed+
		"import javafx.scene.Parent;"+linefeed+
		"import javafx.scene.Scene;"+linefeed+
		"import javafx.stage.Stage;"+linefeed+
		"public class " + filename + " extends Application {"+linefeed+
		"	public static void main(String[] args) {"+linefeed+
		"		Application.launch(" + filename + ".class, args);"+linefeed+
		"	}"+linefeed+
		"	@Override"+linefeed+
		"	public void start(Stage stage) throws Exception {"+linefeed+
		"		Parent root = FXMLLoader.load(getClass().getResource(\"" + filename + ".fxml\"));"+linefeed+
		"		stage.setTitle(\"" + filename + "\");"+linefeed+linefeed+
		"		stage.setScene(new Scene(root, 300, 275));"+linefeed+
		"		stage.show();"+linefeed+
		"	}"+linefeed+
		"}";

		String contents = head;

		return new ByteArrayInputStream(contents.getBytes());
	}
	private InputStream openContentStreamFXMLFile(String filename) {
		String linefeed = "\r\n";
		String head="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<?import java.lang.*?>" +linefeed+
				"<?import java.util.*?>" +linefeed+
				"<?import javafx.scene.layout.*?>" +linefeed+
				"<?import javafx.scene.paint.*?>" +linefeed+
				"<AnchorPane id=\"AnchorPane\" maxHeight=\"-Infinity\" maxWidth=\"-Infinity\" minHeight=\"-Infinity\" minWidth=\"-Infinity\" prefHeight=\"400.0\" prefWidth=\"600.0\" xmlns:fx=\"http://javafx.com/fxml\" />";


		String contents = head;

		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "com.eco.bio7", IStatus.OK,
				message, null);
		throw new CoreException(status);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}