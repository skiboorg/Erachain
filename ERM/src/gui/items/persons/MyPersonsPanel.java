package gui.items.persons;


import utils.ObserverMessage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.mapdb.Fun.Tuple3;

import core.item.persons.PersonCls;
import database.DBSet;
import gui.CoreRowSorter;
import gui.items.ItemsPanel;
import gui.items.persons.AllPersonsPanel;
import gui.items.persons.PersonFrame;
import gui.items.statuses.TableModelStatuses;
import gui.items.persons.IssuePersonFrame;
import gui.models.Renderer_Right;
//import gui.items.persons.MyOrdersFrame;
//import gui.items.persons.PayDividendFrame;
import gui.models.WalletItemPersonsTableModel;
import lang.Lang;

/*
@SuppressWarnings("serial")
public class PersonsPanel extends ItemsPanel
{
	public PersonsPanel(WalletItemPersonsTableModel itemsModel)
	{		
		super(itemsModel, "All Persons", "Issue Person");
	}
}
*/	
@SuppressWarnings("serial")
public class MyPersonsPanel extends JPanel
{
	public MyPersonsPanel()
	{
		GridBagConstraints gridBagConstraints;
		this.setLayout(new GridBagLayout());
		
		//PADDING
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//TABLE
		final WalletItemPersonsTableModel personsModel = new WalletItemPersonsTableModel();
		final JTable table = new JTable(personsModel);
		RowSorter sorter =   new TableRowSorter(personsModel);
		table.setRowSorter(sorter);
		table.getRowSorter();
		personsModel.fireTableDataChanged();
		
		//CHECKBOX FOR CONFIRMED
		TableColumn confirmedColumn = table.getColumnModel().getColumn(WalletItemPersonsTableModel.COLUMN_CONFIRMED);
		confirmedColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));
		
		//CHECKBOX FOR FAVORITE
		TableColumn favoriteColumn = table.getColumnModel().getColumn(WalletItemPersonsTableModel.COLUMN_FAVORITE);
		favoriteColumn.setCellRenderer(table.getDefaultRenderer(Boolean.class));

		
		TableColumnModel columnModel = table.getColumnModel(); // read column model
		columnModel.getColumn(0).setMaxWidth((100));
		
		//Custom renderer for the String column;
				table.setDefaultRenderer(Long.class, new Renderer_Right()); // set renderer
				table.setDefaultRenderer(String.class, new Renderer_Right()); // set renderer
				table.setDefaultRenderer(Boolean.class, new Renderer_Right()); // set renderer
		
		
		
	/*	
		//MENU
		JPopupMenu personsMenu = new JPopupMenu();
		JMenuItem details = new JMenuItem(Lang.getInstance().translate("Details"));
		details.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				row = table.convertRowIndexToModel(row);

				PersonCls person = (PersonCls)personsModel.getItem(row);
				new PersonFrame(person);
			}
		});
		personsMenu.add(details);
		
		
		table.setComponentPopupMenu(personsMenu);
		
		//MOUSE ADAPTER
		table.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				table.setRowSelectionInterval(row, row);
		     }
		});
		
		table.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				table.setRowSelectionInterval(row, row);
				
				if(e.getClickCount() == 2)
				{
					row = table.convertRowIndexToModel(row);
					PersonCls person = (PersonCls)personsModel.getItem(row);
					new PersonFrame(person);
				}
		     }
		});
		
	*/
		//CREATE SEARCH FIELD
				final JTextField txtSearch = new JTextField();

				// UPDATE FILTER ON TEXT CHANGE
				txtSearch.getDocument().addDocumentListener(new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						onChange();
					}

					public void removeUpdate(DocumentEvent e) {
						onChange();
					}

					public void insertUpdate(DocumentEvent e) {
						onChange();
					}

					public void onChange() {

						// GET VALUE
						String search = txtSearch.getText();

					 	// SET FILTER
				//		tableModelPersons.getSortableList().setFilter(search);
						personsModel.fireTableDataChanged();
						
						RowFilter filter = RowFilter.regexFilter(".*" + search + ".*", 1);
						((DefaultRowSorter) sorter).setRowFilter(filter);
						
						personsModel.fireTableDataChanged();
						
					}
				});
				
				// select row table persons
				
				JEditorPane Address1 = new JEditorPane();
				Address1.setContentType("text/html");
				Address1.setText("<HTML>" + Lang.getInstance().translate("Select person")); // Document text is provided below.
				Address1.setBackground(new Color(255, 255, 255, 0));
				
				 JSplitPane PersJSpline = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,new JScrollPane(table),new JScrollPane(Address1)); 
				 
				 
				// обработка изменения положения курсора в таблице
				table.getSelectionModel().addListSelectionListener(new ListSelectionListener()  {
					
					@SuppressWarnings("deprecation")
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						String Date_Acti;
						String Date_birs;
						String message;
				// TODO Auto-generated method stub
				// устанавливаем формат даты
						SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy"); // HH:mm");
				//создаем объект персоны
						PersonCls person;
						
						
							if (table.getSelectedRow() >= 0 ){
							person = personsModel.getItem(table.convertRowIndexToModel(table.getSelectedRow()));
						
						
				//читаем таблицу персон.
						Tuple3<Integer, Integer, byte[]> t3 = DBSet.getInstance().getPersonStatusMap().getItem(person.getKey()); //(Long) personsTable.getValueAt(personsTable.getSelectedRow(),0));
				// преобразование в дату
				
				
						if (t3 != null){
							if (t3.a == 0) Date_Acti = "+";
							else Date_Acti = formatDate.format( new Date(Long.valueOf(t3.a.toString())));
						} else
						{
							Date_Acti =Lang.getInstance().translate("Not found!");
						};
						if (person.isConfirmed()){
							Date_birs=  formatDate.format(new Date(Long.valueOf(person.getBirthday())));
							 message ="<html><div></div><div> <p><b>" + Lang.getInstance().translate("Key")+":"   + person.getKey()        			+ "</p>"
							+ "<p> <b> "  + Lang.getInstance().translate("Name")+":"       			  + person.getName().toString()		+ "</p>" 
					        + "<p> "  + Lang.getInstance().translate("Birthday")  +":"        	      + Date_birs			+"</p>"
					        + "<p>  "  + Lang.getInstance().translate("To Date")  +":"        		  + Date_Acti			+"</p>"
					        ;
							 // Читаем адреса клиента
							 TreeMap<String, java.util.Stack<Tuple3<Integer, Integer, byte[]>>> Addresses= DBSet.getInstance().getPersonAddressMap().getItems(person.getKey());
							 if ( !Addresses.isEmpty()){
								 message =message + "<p>"  + Lang.getInstance().translate("Account")  +":  <input type='text' size='40' value='"+ Addresses.lastKey() +"' id='iiii' name='nnnn' class= 'cccc' onchange =''><p></div>";
							 }
							 else{
								 message = message + "<p> " +  Lang.getInstance().translate("Account not found!")+ "</p";
															 }
						}else{
							
							message = "<html><p>"+ Lang.getInstance().translate("Not found!") +"</></>";	
						}
						message = message + "</html>";
						
							
				Address1.setText(message);
				PersJSpline.setDividerLocation(PersJSpline.getDividerLocation());//.setPreferredSize(new Dimension(100,100));		
			 
			
					
					}
					}
					
					});
				
				
				
				
				
				
				
				
				
				
				
				
				
		
	
	
				
				
						
		
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 8, 8);
        this.add(new JLabel(Lang.getInstance().translate("Search") + ":"), gridBagConstraints);

      
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 9, 10);
       
        this.add(txtSearch, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 6, 10);

		
        PersJSpline.setDividerLocation(700);
        this.add(PersJSpline, gridBagConstraints);

	
	
	
	
	}
	
	public void onIssueClick()
	{
		new IssuePersonFrame();
	}
	
	public void onAllClick()
	{
		new AllPersonsPanel();
	}
	
	public void onMyOrdersClick()
	{
		//new MyOrdersFrame();
	}
}
