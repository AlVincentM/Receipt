import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class RTCalc extends JFrame {

	JFrame mainWindow;
	JTextField titleHeader;
	JLabel addItemLabel;
	JTextField inputItemName;
	JLabel quantityLabel;
	JSpinner quantity;
	JButton addItemButton;
	JTable itemTable;
	DefaultTableModel model;
	JTextArea totalText;
	JFrame dialogBoxMSg;
	JButton removeItemButton;
	JLabel cashRecievedLabel;
	JTextField inputCashReceived;
	JButton cashOutButton;

	double total = 0;
	private Item item[] = new Item[10];

	// error handling
	final int NOITEMS = 0;
	final int NOCASHRECIEVED = 1;
	final int INSUFFICIENTPAYMENT = 2;

	RTCalc() throws Exception{
		mainWindow = new JFrame("TeslaTech");

		titleHeader = new JTextField("TeslaTech");
		titleHeader.setHorizontalAlignment(JTextField.CENTER);
		titleHeader.setFont(new Font("Sans Serif", Font.BOLD, 32));
		titleHeader.setEditable(false);
		titleHeader.setBounds(0, 0, 600, 50);

		addItemLabel = new JLabel("Add Item:");
		addItemLabel.setBounds(5, 60, 150, 30);

		inputItemName = new JTextField("Search ...");
		inputItemName.setBounds(5, 95, 150, 30);
		// clear text on click
		inputItemName.addMouseListener(new MouseAdapter(){
         @Override
         	public void mouseClicked(MouseEvent e){
            	inputItemName.setText("");
  	       	}
        });

		quantityLabel = new JLabel("Quantity (pcs):");
		quantityLabel.setBounds(5, 130, 150, 30);

		quantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
		quantity.setBounds(5, 165, 150, 30);

		addItemButton = new JButton("Add Item");
		addItemButton.setBounds(5, 200, 150, 30);

		removeItemButton = new JButton("Remove item");
		removeItemButton.setBounds(5, 235, 150, 30);

		cashRecievedLabel = new JLabel("Cash Recieved:");
		cashRecievedLabel.setBounds(5, 300, 150, 30);

		inputCashReceived= new JTextField("Enter Amount...");
		inputCashReceived.setBounds(5, 335, 150, 30);
		// clear text on click
		inputCashReceived.addMouseListener(new MouseAdapter(){
         @Override
         	public void mouseClicked(MouseEvent e){
            	inputCashReceived.setText("");
  	       	}
        });

		// give an error if input is not a number
        inputCashReceived.addKeyListener(new KeyAdapter() {
	        public void keyPressed(KeyEvent ke) {
	            String value = inputCashReceived.getText();
	            String totalToDisplay = Double.toString(total);
	            int l = value.length();
	            if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9' || ke.getKeyChar() == '.' || ke.getKeyCode() == 8 || ke.getKeyCode() == 144) {
	            }
	            else if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
	            	inputCashReceived.addActionListener(new ListenToCashOut());
	            }
	            else {
	               JOptionPane.showMessageDialog(null, "Invalid input");
	            }
	        }
      	});

		cashOutButton = new JButton("CASH OUT");
		cashOutButton.setBounds(5, 370, 150, 30);

		model = new DefaultTableModel();
		itemTable = new JTable(model);
		model.addColumn("Item Code");
		model.addColumn("Item Name");
		model.addColumn("Quantity");
		model.addColumn("Unit Price");
		JScrollPane tableContainer  = new JScrollPane(itemTable);
		tableContainer.setBounds(160, 60, 425, 300);
		itemTable.setFillsViewportHeight(true);

		totalText = new JTextArea("Total: ");
		totalText.setBounds(325, 360, 262, 100);
		totalText.setFont(new Font("Sans Serif", Font.PLAIN, 18));
		totalText.setEditable(false);

		mainWindow.add(titleHeader);
		mainWindow.add(addItemLabel);
		mainWindow.add(inputItemName);
		mainWindow.add(quantityLabel);
		mainWindow.add(quantity);
		mainWindow.add(addItemButton);
		mainWindow.add(removeItemButton);
		mainWindow.add(cashRecievedLabel);
		mainWindow.add(inputCashReceived);
		mainWindow.add(cashOutButton);
		mainWindow.add(tableContainer);
		mainWindow.add(totalText);

		addItemButton.addActionListener(new ListenToAddItem());
		removeItemButton.addActionListener(new ListenToRemoveItem());
		cashOutButton.addActionListener(new ListenToCashOut());
		inputItemName.addKeyListener(new pressedEnter());

		new ItemRecord();

		mainWindow.setSize(605, 505);
		mainWindow.setResizable(false);
		mainWindow.setLocation(300, 100);
	    mainWindow.setLayout(null);
	    mainWindow.setVisible(true);
	    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// invoked if Add Item button is clicked
	public void addItem() {
		String searchQuery = inputItemName.getText();
        int itemCode = 0;
       	String itemName = "";
       	int itemQuantity = (Integer) quantity.getValue();
       	double itemPrice= 0.0;
       	boolean itemIsFound = false;

       	for(int i = 0; i < 5; i++) {
       		if(searchQuery.equalsIgnoreCase(item[i].getItemName()) || searchQuery.equals(Integer.toString(item[i].getItemCode()))) {
       			itemIsFound= true;
       			itemCode = item[i].getItemCode();
        		itemName = item[i].getItemName();
        		itemPrice = item[i].getItemPrice();
        	}
       	}

        if(itemIsFound) {
      		model.addRow(new Object[] {new Integer(itemCode), new String(itemName), new Integer(itemQuantity), new Double(itemPrice)});
       		total = total + (itemPrice * itemQuantity);
       		String totalToDisplay = Double.toString(total);
       		totalText.setText("Total: " + totalToDisplay);
       		//printReceipt();
       	}
       	else if (searchQuery.equals("") || searchQuery.equals("Search ...")) {
       		JOptionPane.showMessageDialog(dialogBoxMSg, "Input is empty!");
       	}
        else {
       		JOptionPane.showMessageDialog(dialogBoxMSg, "Item does not exist!");
       	}
	}

	// invoked if Remove Item is clicked
	public void removeItem() {
		int numRows = itemTable.getSelectedRows().length;
		int selectedRow = itemTable.getSelectedRow();

		if(selectedRow < 0) {
			JOptionPane.showMessageDialog(dialogBoxMSg, "No item selected, select an item to remove.");
		}
		else {
			for(int i=0; i<numRows ; i++ ) {
				selectedRow = itemTable.getSelectedRow();
				String itemQuantity =  itemTable.getModel().getValueAt(selectedRow, 2).toString();
				String itemPrice =  itemTable.getModel().getValueAt(selectedRow, 3).toString();
				total = total - (Double.parseDouble(itemPrice) * Integer.parseInt(itemQuantity));
				String totalToDisplay = Double.toString(total);
	       		totalText.setText("Total: " + totalToDisplay);
		    	model.removeRow(itemTable.getSelectedRow());
			}
		}
	}

	// print a reciept at every successul transaction
	public void printReceipt() {
		try {
			BufferedWriter writer1  = new BufferedWriter(new FileWriter("receipt.txt"));
			int rowCount = itemTable.getRowCount();
			int row = 0;
			int column = 0;

			String cash = inputCashReceived.getText();
			double cashRecieved = Double.parseDouble(cash);
			double change = cashRecieved - total;

			writer1.write("TeslaTech\n" + "The best tech store in town!\n\n");
			writer1.close();
			BufferedWriter writer2 = new BufferedWriter(new  FileWriter("receipt.txt", true));

			for(int i = 0; i < rowCount; i++) {
				String itemCode =  itemTable.getModel().getValueAt(row, 0).toString();
				String itemName =  itemTable.getModel().getValueAt(row, 1).toString();
				String itemQuantity =  itemTable.getModel().getValueAt(row, 2).toString();
				String itemPrice =  itemTable.getModel().getValueAt(row, 3).toString();
				row++;
				writer2.append("item_code:" + itemCode + " item_name:" + itemName + " item_quantity:" + itemQuantity + " unit_ price:" + itemPrice + "\n");
			}
			writer2.append("\nTotal: " + total + "\nCash recieved: " + inputCashReceived.getText() + "\nChange: " + change);
			writer2.close();

		}
		catch(IOException e) {
		}
		
	}

	// invoked if Cash Out button is clicked
	public void cashOut() throws TransactionException {
		try {
			String cash = inputCashReceived.getText();
			double cashRecieved = Double.parseDouble(cash);
			double change = cashRecieved - total;

			if(itemTable.getRowCount() == 0) {
				handleErr(NOITEMS);
				return;
			}
			else if(change < 0.0) {
				handleErr(INSUFFICIENTPAYMENT);
				return;
			}
			else {
				String totalToDisplay = Double.toString(total);
				totalText.setText("Total: " + totalToDisplay + "\nCash recieved: " + inputCashReceived.getText() + "\nChange: " + change);
				printReceipt();
				int rows = itemTable.getRowCount();

				JOptionPane.showMessageDialog(dialogBoxMSg, "Transaction Successful!");
				for(int i = rows - 1; i >= 0; i--) {
					model.removeRow(i);
				}
				total = 0;
		       	totalText.setText("Total: ");
		    }
		}
		catch(NumberFormatException numberFormatErr) {
			handleErr(NOCASHRECIEVED);
		}
	}

	class ListenToCashOut implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	try {
        		cashOut();
        	}
        	catch(TransactionException transanctionE) {
        		JOptionPane.showMessageDialog(null, transanctionE);
        	}
        }
    }

	class ListenToRemoveItem implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	removeItem();
        }
    }

	class ListenToAddItem implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	addItem();
        }
    }

    public class pressedEnter  implements KeyListener {
	    public void keyTyped(KeyEvent e) {
	        // Invoked when a key has been typed.
	    }

	    public void keyPressed(KeyEvent e) {
	        // Invoked when a key has been pressed.
	        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	        	addItem();
	        }
	    }

	    public void keyReleased(KeyEvent e) {
	        // Invoked when a key has been released.
	    }
	}

    class ItemRecord {
	    
	    ItemRecord() throws FileNotFoundException {
	    	int i = 0;
	    	Scanner scan = new Scanner(new FileReader("itemRecords.txt"));

	    	String tempString = "";

	    	for(i = 0; i < 10; i++) {
	    		item[i] = new Item();
	    	}
	    	i = 0;
	    	while(scan.hasNextLine()) {
	    		tempString = scan.nextLine();
	    		String temp[] = tempString.split(";");

	    		int itemCode = Integer.parseInt(temp[0]);
	    		String itemName = temp[1];
	    		double itemPrice = Double.parseDouble(temp[2]);

	    		item[i].setItemCode(itemCode);
	    		item[i].setItemName(itemName);
	    		item[i].setItemPrice(itemPrice);
	    		i++;
	    	}
	    }
	 }


	class TransactionException extends Exception {
		String errStr; // describes the error 
		public TransactionException(String str) {
			errStr = str;
		}
		public String toString() {
			return errStr;
		}
	}

	private void handleErr(int error) throws TransactionException {
		String[] err = { "No items purchased", "No cash recieved",
			"Insufficient payment"};
		throw new TransactionException(err[error]);
	}
}
