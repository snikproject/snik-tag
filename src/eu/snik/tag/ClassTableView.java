package eu.snik.tag;

import java.util.Collection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class ClassTableView extends TableView<Clazz>
{
	
	public ClassTableView(Collection<Clazz> classes, Runnable update)
	{
		this.setEditable(true);		
	
		var labelCol = new TableColumn<Clazz,String>("Label");
		labelCol.setCellValueFactory(new PropertyValueFactory<>("label"));
		labelCol.setCellFactory(TextFieldTableCell.<Clazz>forTableColumn());
		labelCol.setMinWidth(300);
		labelCol.setOnEditCommit(e->
		{
			e.getRowValue().setLabel(e.getNewValue());
			update.run();;
		});
	
		var localNameCol = new TableColumn<Clazz,String>("Local Name");
		localNameCol.setCellValueFactory(new PropertyValueFactory<>("localName"));
		localNameCol.setMinWidth(300);
		localNameCol.setOnEditCommit(e->
		{
			e.getRowValue().setLocalName(e.getNewValue());
			update.run();
		});
	
	
		var subtopCol = new TableColumn<Clazz,Subtop>("Type");
		subtopCol.setCellValueFactory(new PropertyValueFactory<>("subtop"));
		subtopCol.setMinWidth(300);		
	
		var removeCol = new TableColumn<Clazz,Clazz>("Entfernen");
		removeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
	
		removeCol.setCellFactory(param -> new TableCell<Clazz,Clazz>()
		{
			final Button deleteButton = new Button("X");
	
			@Override
			protected void updateItem(Clazz clazz, boolean empty)
			{
				super.updateItem(clazz, empty);
	
				if (clazz == null) {
					setGraphic(null);
					return;
				}
	
				setGraphic(deleteButton);
				deleteButton.setOnAction(
						event -> 
						{
							getTableView().getItems().remove(clazz);
							classes.remove(clazz);
							update.run();
						}
						);
			}
		});
		
		var relationCol = new TableColumn<Clazz,Collection<Triple>>("Relations");
		relationCol.setCellValueFactory(new PropertyValueFactory<>("triples"));
		relationCol.setMinWidth(300);		
		
		this.getColumns().addAll(labelCol,localNameCol,subtopCol,removeCol,relationCol);
	}
	
}

