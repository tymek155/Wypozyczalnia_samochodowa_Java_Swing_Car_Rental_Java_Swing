import javax.swing.*;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;


public class CarShowroomGUI {
    JFrame frame;
    JTable cTab;
    JTable vTab;
    CarShowroomGUIFacade facade;

    public CarShowroomGUI(CarShowroomContainer showroomContainer){
        if(showroomContainer == null || showroomContainer.showrooms == null){
            JOptionPane.showMessageDialog(null, "Błędna zawartość", "Błąd", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Błędne dane");
        }
        facade = new CarShowroomGUIFacade(showroomContainer);

        //Tworzenie okna
        frame = new JFrame("Zarządzaj wypożyczalnią");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1,2));

        //Tworzenie modelu dla salonow
        DefaultTableModel cTabModel = new DefaultTableModel();
        cTabModel.addColumn("Nazwa salonu samochodowego");
        cTabModel.addColumn("Lokalizacja");
        cTabModel.addColumn("Maksymalna pojemność");
        for(Map.Entry<String, CarShowroom> showroom : facade.getMap()) {
            cTabModel.addRow(new Object[]{showroom.getKey(), showroom.getValue().location, showroom.getValue().maxCapacity});
        }

        cTab = new JTable(cTabModel);
        cTab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cTab.getSelectionModel().addListSelectionListener(e -> {
            int selRow = cTab.getSelectedRow();
            if(selRow != -1){
                String selCenter = (String) cTab.getValueAt(selRow,0);
                updateVehicleTable(selCenter);
            }
        });

        //Panel przyciskow
        JScrollPane cScrollPane = new JScrollPane(cTab);
        frame.add(cScrollPane, BorderLayout.CENTER);

        JPanel bPanel = new JPanel(new GridLayout(7,1));

        //Dodaj salon
        JButton addCenterButton = new JButton("Dodaj salon");
        addCenterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(frame, "Wprowadź nazwę salonu");
                if(name!= null && !name.isEmpty()){
                    String maxInput = JOptionPane.showInputDialog("Podaj pojemnosc salonu");
                    int max = 0;
                    try{
                        max = Integer.parseInt(maxInput);
                        if(max <= 0){
                            JOptionPane.showMessageDialog(frame, "Pojemność salonu musi być >0!");
                            return;
                        }
                    }catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(frame, "Nieprawidłowe dane", "Błąd", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String loc = JOptionPane.showInputDialog(frame, "Podaj lokalizację salonu");
                    facade.addCenter(name, facade.createCarShowroom(name, max, loc));
                    cTabModel.addRow(new Object[]{name, loc, max});
                    ((AbstractTableModel) cTab.getModel()).fireTableDataChanged();
                }
            }
        });
        bPanel.add(addCenterButton);

        //Usun salon
        JButton removeContainerButton = new JButton("Usuń centrum");
        removeContainerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selecRow = cTab.getSelectedRow();
                if(selecRow != -1){
                    String selecCenter = (String) cTab.getValueAt(selecRow,0);
                    facade.removeCenter(selecCenter);
                    ((DefaultTableModel) cTab.getModel()).removeRow(selecRow);
                }else{
                    JOptionPane.showMessageDialog(frame, "Wybierz salon do usunięcia!");
                }
            }
        });
        bPanel.add(removeContainerButton);
        frame.add(bPanel, BorderLayout.EAST);

        vTab = new JTable(new DefaultTableModel());
        JScrollPane vehScrollPane = new JScrollPane(vTab);
        frame.add(vehScrollPane, BorderLayout.SOUTH);

        //Dodaj samochod
        JButton addVehButton = new JButton("Dodaj samochód");
        addVehButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selecCenterRow = cTab.getSelectedRow();
                if(selecCenterRow != -1){
                    String selecCenter = (String) cTab.getValueAt(selecCenterRow, 0);
                    if(selecCenter != null && !selecCenter.isEmpty()){
                        String mark = JOptionPane.showInputDialog(frame, "Podaj markę samochodu");
                        String model = JOptionPane.showInputDialog(frame, "Podaj model samochodu");

                        JComboBox<ItemCondition> conditionJComboBox = new JComboBox<>(ItemCondition.values());
                        int option = JOptionPane.showOptionDialog(frame, conditionJComboBox, "Wybierz stan samochodu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null ,null);

                        if (option == JOptionPane.OK_OPTION){
                            ItemCondition condition = (ItemCondition) conditionJComboBox.getSelectedItem();
                            double price = Double.parseDouble(JOptionPane.showInputDialog(frame, "Podaj cenę pojazdu"));
                            String production = JOptionPane.showInputDialog(frame, "Podaj rok produkcji");
                            int productionYear = 0;
                            try{
                                productionYear = Integer.parseInt(production);
                            }catch(NumberFormatException e2){
                                JOptionPane.showMessageDialog(frame, "Nieprawidłowa wartość");
                                return;
                            }
                            double mileage = Double.parseDouble(JOptionPane.showInputDialog(frame, "Podaj przebieg"));
                            double capacity = Double.parseDouble(JOptionPane.showInputDialog(frame, "Podaj pojemność silnika"));
                            if (price <=0 || productionYear <=0 || mileage <= 0 || capacity <= 0){
                                JOptionPane.showMessageDialog(frame,"Wartość musi być >0!");
                                return;
                            }
                            facade.addProduct(selecCenter, mark, model, condition, price, productionYear, mileage, capacity);
                            updateVehicleTable(selecCenter);
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(frame, "Wybierz salon do którego chcesz dodać samochód!");
                }
            }
        });
        bPanel.add(addVehButton);

        //Usun samochod
        JButton removeVehicleButton = new JButton("Usuń pojazd");
        removeVehicleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selecRow = vTab.getSelectedRow();
                if(selecRow!=-1){
                    int selecCenterRow = cTab.getSelectedRow();
                    if(selecCenterRow != -1){
                        String selecCenter = (String) cTab.getValueAt(selecCenterRow,0);
                        facade.removeVehicle(selecCenter, selecRow);
                        ((DefaultTableModel) vTab.getModel()).removeRow(selecRow);
                        ((AbstractTableModel) vTab.getModel()).fireTableDataChanged();
                    }else{
                        JOptionPane.showMessageDialog(frame, "Wybierz salon, z którego chcesz usunąć!");
                        return;
                    }
                }else{
                    JOptionPane.showMessageDialog(frame, "Wybierz samochód, który chcesz usunąć!");
                    return;
                }
            }
        });
        bPanel.add(removeVehicleButton);

        //Sortuj salony przez zapelnienie
        JButton sortCentersButton = new JButton("Sortuj salony przez zapełnienie");
        sortCentersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Map.Entry<String, CarShowroom>> sortCenters = facade.getSortedMap();
                if (!sortCenters.isEmpty()){
                    int rowCount = cTabModel.getRowCount();
                    for (int i = 0; i < rowCount; i++){
                        cTabModel.removeRow(0);
                    }

                    for(Map.Entry<String, CarShowroom> entry: sortCenters){
                        cTabModel.addRow(new Object[]{entry.getKey(), entry.getValue().location, entry.getValue().maxCapacity});
                    }
                    cTab.setModel(cTabModel);
                }

//                List<Map.Entry<String, CarShowroom>> sortCenters = showroomContainer.sortCentersByLd();
//                StringBuilder sortedCentInf = new StringBuilder("Posortowane salony poprzez maksymalną pojemność\n");
//                for(Map.Entry<String, CarShowroom> entry : sortCenters){
//                    sortedCentInf.append(entry.getKey()).append(": ").append(entry.getValue().maxCapacity).append("\n");
//                }
//                JOptionPane.showMessageDialog(frame, sortedCentInf.toString());
            }
        });
        bPanel.add(sortCentersButton);

        JTextField filterTextField = new JTextField();
        filterTextField.setToolTipText("Filtruj samochody");
        filterTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTxt = filterTextField.getText().toLowerCase();
                filterTable(searchTxt);
            }
        });
        bPanel.add((filterTextField));

        JComboBox<String> stateComboBox = new JComboBox<>();
        stateComboBox.addItem("Wszystkie samochody");
        for(ItemCondition condition: ItemCondition.values()){
            stateComboBox.addItem(condition.toString());
        }
        stateComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selecCondtion = (String) stateComboBox.getSelectedItem();
                filterTableByState(selecCondtion);
            }
        });
        bPanel.add(stateComboBox);

        frame.pack();
        frame.setVisible(true);
    }

    void filterTableByState(String selCondition){
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) vTab.getModel());
        vTab.setRowSorter(sorter);
        if("Wszystkie samochody".equals(selCondition)){
            sorter.setRowFilter(null);
        }else{
            sorter.setRowFilter(RowFilter.regexFilter(selCondition.toString(), 2));
        }
    }

    void filterTable(String searchTxt){
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) vTab.getModel());
        vTab.setRowSorter(sorter);
        if(searchTxt.length()==0){
            sorter.setRowFilter(null);
        }else{
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTxt));
        }
    }

    void updateVehicleTable(String Name){
        CarShowroom showroom = facade.getCarShowroom(Name);
        if(showroom != null){
            DefaultTableModel vehTabModel = new DefaultTableModel();
            vehTabModel.addColumn("Marka");
            vehTabModel.addColumn("Model");
            vehTabModel.addColumn("Stan");
            vehTabModel.addColumn("Cena");
            vehTabModel.addColumn("Rok produkcji");
            vehTabModel.addColumn("Przebieg");
            vehTabModel.addColumn("Pojemność silnika");
            for(Vehicle v: showroom.base){
                vehTabModel.addRow(new Object[]{
                        v.mark,
                        v.model,
                        v.state,
                        v.price,
                        v.productionYear,
                        v.mileage,
                        v.capacity
                });
            }
            vTab.setModel(vehTabModel);
        }
    }

}
