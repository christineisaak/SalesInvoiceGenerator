package com.sales.controller;

import com.sales.model.SalesInvoice;
import com.sales.model.SalesInvoicesTableModel;
import com.sales.model.SalesLine;
import com.sales.model.SalesLineTableModel;
import com.sales.view.SalesInvoiceDialog;
import com.sales.view.SalesLineDialog;
import com.sales.view.ViewFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener, ListSelectionListener {

    private ViewFrame frame;
    private SalesInvoiceDialog invoiceDialog;
    private SalesLineDialog lineDialog;
    
    public Controller(ViewFrame frame) {
        this.frame = frame;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Action: " + actionCommand);
        switch (actionCommand) {
            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Create New Item":
                createNewItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceOK":
                createInvoiceOK();
                break;
            case "createLineOK":
                createLineOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
                
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.getInvoiceTable().getSelectedRow();
        if(selectedIndex != -1) {
           System.out.println("You Have Selected row: " + selectedIndex);
           SalesInvoice currentInvoice = frame.getInvoices().get(selectedIndex);
           frame.getInvoiceNumLabel().setText(""+currentInvoice.getNum());
           frame.getInvoiceDateLabel().setText(currentInvoice.getDate());
           frame.getCustomerNameLabel().setText(currentInvoice.getCustomer());
           frame.getInvoiceTotalLabel().setText(""+currentInvoice.getInvoiceTotal());
           SalesLineTableModel linesTableModel = new SalesLineTableModel(currentInvoice.getLines());
           frame.getLineTable().setModel(linesTableModel);
           linesTableModel.fireTableDataChanged();
        }
    
    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();
        try {
            int result = fc.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                Path headerPath = Paths.get(headerFile.getAbsolutePath());
                List<String> headerLines = Files.readAllLines(headerPath);
                System.out.println("Invoices have been read");
                // 1,22-11-2020,Ali
                // 2,13-10-2021,Saleh
                // 3,09-01-2019,Ibrahim
                ArrayList<SalesInvoice> invoicesArray = new ArrayList<>();
                for (String headerLine : headerLines) {
                    String[] headerParts = headerLine.split(",");
                    int invoiceNum = Integer.parseInt(headerParts[0]);
                    String invoiceDate = headerParts[1];
                    String customerName = headerParts[2];
                    
                    SalesInvoice invoice = new SalesInvoice(invoiceNum, invoiceDate, customerName);
                    invoicesArray.add(invoice);
                }
                System.out.println("Check point");
                result = fc.showOpenDialog(frame);
                if(result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    Path linePath = Paths.get(lineFile.getAbsolutePath());
                    List<String> lineLines = Files.readAllLines(linePath);
                    System.out.println("Lines have been read");
                    for (String lineLine : lineLines) {
                        String lineParts[] = lineLine.split(",");
                        int invoiceNum = Integer.parseInt(lineParts[0]);
                        String itemName = lineParts[1];
                        double itemPrice = Double.parseDouble(lineParts[2]);
                        int count = Integer.parseInt(lineParts[3]);
                        SalesInvoice inv = null;
                        for (SalesInvoice invoice : invoicesArray) {
                            if (invoice.getNum() == invoiceNum) {
                                inv = invoice;
                                break;
                            }
                        }
                        
                        SalesLine line = new SalesLine(itemName, itemPrice, count, inv);
                        inv.getLines().add(line);
                    }
                    System.out.println("Check point");
                }
                frame.setInvoices(invoicesArray);
                SalesInvoicesTableModel invoicesTableModel = new SalesInvoicesTableModel(invoicesArray);
                frame.setInvoicesTableModel(invoicesTableModel);
                frame.getInvoiceTable().setModel(invoicesTableModel);
                frame.getInvoicesTableModel().fireTableDataChanged();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveFile() {
        ArrayList<SalesInvoice> invoices = frame.getInvoices();
        String headers = "";
        String lines = "";
        for (SalesInvoice invoice : invoices) {
            String invCSV = invoice.getToScv();
            headers += invCSV;
            headers += "\n";

            for (SalesLine line : invoice.getLines()) {
                String lineCSV = line.getToScv();
                lines += lineCSV;
                lines += "\n";
            }
        }
     System.out.println("Check point");
      try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter hfw = new FileWriter(headerFile);
                hfw.write(headers);
                hfw.flush();
                hfw.close();
                result = fc.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter lfw = new FileWriter(lineFile);
                    lfw.write(lines);
                    lfw.flush();
                    lfw.close();
                }
            }
        } catch (Exception ex) {

        }
    }

    private void createNewInvoice() {
        invoiceDialog = new SalesInvoiceDialog(frame);
        invoiceDialog.setVisible(true);
    }

    private void deleteInvoice() {
        int selectedRow = frame.getInvoiceTable().getSelectedRow();
        if (selectedRow != -1) {
            frame.getInvoices().remove(selectedRow);
            frame.getInvoicesTableModel().fireTableDataChanged();
                  
        }
        
    }

    private void createNewItem() {
       lineDialog = new SalesLineDialog(frame);
       lineDialog.setVisible(true);
    }

     private void deleteItem() {
         int selectedInv = frame.getInvoiceTable().getSelectedRow();
        int selectedRow = frame.getLineTable().getSelectedRow();
        
        if (selectedInv !=-1 && selectedRow != -1) {
            SalesInvoice invoice = frame.getInvoices().get(selectedInv);
            invoice.getLines().remove(selectedRow);
            SalesLineTableModel lineTableModel = new SalesLineTableModel(invoice.getLines());
            frame.getLineTable().setModel(lineTableModel);
           lineTableModel.fireTableDataChanged();
         }
    }

    private void createInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }

    private void createInvoiceOK() {
        String date = invoiceDialog.getInvDateField().getText();
        String customer = invoiceDialog.getCustNameField().getText();
        int num = frame.getNextInvoiceNum();
        
        SalesInvoice invoice = new SalesInvoice(num, date, customer);
        frame.getInvoices().add(invoice);
        frame.getInvoicesTableModel().fireTableDataChanged();
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
        
    }

    private void createLineOK() {
        String item = lineDialog.getItemNameField().getText();
        String countstr = lineDialog.getItemCountField().getText();
        String pricestr = lineDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countstr);
        double price = Double.parseDouble(pricestr);
        int selectedInvoice = frame.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            SalesInvoice invoice = frame.getInvoices().get(selectedInvoice);
            SalesLine line = new SalesLine(item, price, count, invoice);
            invoice.getLines().add(line);
            SalesLineTableModel linesTableModel = (SalesLineTableModel) frame.getLineTable().getModel();
            linesTableModel.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();
                     }
      lineDialog.setVisible(false);
      lineDialog.dispose();
      lineDialog = null;
    }

    private void createLineCancel() {
      lineDialog.setVisible(false);
      lineDialog.dispose();
      lineDialog = null;
    }
}
