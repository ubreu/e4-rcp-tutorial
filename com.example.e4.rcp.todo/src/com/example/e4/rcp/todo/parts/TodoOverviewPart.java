package com.example.e4.rcp.todo.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.example.e4.rcp.todo.event.EventConstants;
import com.example.e4.rcp.todo.facade.ModelFacade;
import com.example.e4.rcp.todo.model.Todo;

public class TodoOverviewPart {

	private Button btnLoadData;
	private WritableList writableList;

	@Inject
	private ModelFacade model;

	@Inject
	private IEventBroker broker;

	@Inject
	private ESelectionService selectionService;

	private TableViewer viewer;
	protected String searchString = "";

	@PostConstruct
	public void createControls(Composite parent, final ModelFacade model,
			EMenuService service, MPart part) {
		parent.setLayout(new GridLayout(1, false));

		btnLoadData = new Button(parent, SWT.NONE);
		btnLoadData.setText("Update");

		btnLoadData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				broker.post(EventConstants.TOPIC_TODO_DATA_LOAD_REQUEST, null);
			}

		});

		Text search = new Text(parent, SWT.SEARCH | SWT.CANCEL
				| SWT.ICON_SEARCH);
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		search.setMessage("Filter");
		search.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text source = (Text) e.getSource();
				searchString = source.getText();
				viewer.refresh();
			}
		});
		search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					Text source = (Text) e.getSource();
					source.setText("");
				}
				super.widgetSelected(e);
			}
		});
		viewer = new TableViewer(parent, SWT.MULTI);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Summary");

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Description");

		viewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				Todo todo = (Todo) element;
				return todo.getSummary().contains(searchString)
						|| todo.getDescription().contains(searchString);
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer
						.getSelection();
				selectionService.setSelection(selection.getFirstElement());
			}
		});

		List<MMenu> menus = part.getMenus();
		if (menus.size() == 1) {
			service.registerContextMenu(viewer.getControl(), menus.get(0)
					.getElementId());
		}

		broker.post(EventConstants.TOPIC_TODO_DATA_LOAD_REQUEST, null);
	}

	@Inject
	@Optional
	public void onDataLoaded(
			@UIEventTopic(EventConstants.TOPIC_TODO_DATA_LOADED) List<Todo> todos) {
		writableList = new WritableList(todos, Todo.class);
		ViewerSupport.bind(
				viewer,
				writableList,
				BeanProperties.values(new String[] { Todo.FIELD_SUMMARY,
						Todo.FIELD_DESCRIPTION }));
	}

	@Inject
	public void setTodo(
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Todo todo) {
		if (viewer != null) {
			ISelection selection;
			if (todo != null) {
				selection = new StructuredSelection(todo);
			} else {
				selection = StructuredSelection.EMPTY;
			}
			viewer.setSelection(selection);
		}
	}

	@Inject
	@Optional
	public void onTodoDeleted(
			@UIEventTopic(EventConstants.TOPIC_TODO_DATA_UPDATE_DELETE) Todo todo) {
		writableList.remove(todo);
	}

	@Inject
	@Optional
	public void onTodoAdded(
			@UIEventTopic(EventConstants.TOPIC_TODO_DATA_UPDATE_NEW) Todo todo) {
		writableList.add(todo);
		setTodo(todo);
	}

	@Focus
	public void onFocus() {
		btnLoadData.setFocus();
	}

	@PreDestroy
	public void dispose() {
	}
}