import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class Atlas extends Frame {
    Panel northPanel,centerPanel,westPanel,southPanel;
    Label lbl1, lbl2, lbl3, lbl4;
    Button addButton,removeButton,updateButton,confirmButton,firstButton,lastButton,nextButton,backButton,cancelButton;
    TextField input1,input2,input3,input4;

    ResultSet data;
    String mode = "";
    Connection connection;

    public void fetchFromDatabase(){
        try{
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            data = st.executeQuery("select * from pays");
            data.next();
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public void createConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/atlas";
            String username = "amine";
            String password = "AMIN@ben1234";
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updatePaysDetails() {
        try{
            input1.setText(data.getString(2));
            input2.setText(data.getString(3));
            input3.setText(data.getString(4));
            input4.setText(data.getString(5));
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

    public void toggleInputs(boolean state) {
        input1.setEditable(state);
        input2.setEditable(state);
        input3.setEditable(state);
        input4.setEditable(state);
    }

    public void toggleButtons(boolean state){
        addButton.setEnabled(state);
        updateButton.setEnabled(state);
        removeButton.setEnabled(state);
        firstButton.setEnabled(state);
        lastButton.setEnabled(state);
        nextButton.setEnabled(state);
        backButton.setEnabled(state);
        confirmButton.setEnabled(!state);
    }

    public void showErrorDialog(Frame parent, String message) {
        Dialog dialog = new Dialog(parent, "Input Error", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);

        Label errorLabel = new Label(message, Label.CENTER);
        dialog.add(errorLabel, BorderLayout.CENTER);

        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public boolean showConfirmDialog(Frame parent, String message, String title) {
        final boolean[] res = {false};
        Dialog dialog = new Dialog(parent, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);

        Label lblMessage = new Label(message, Label.CENTER);
        dialog.add(lblMessage, BorderLayout.CENTER);

        Panel buttonPanel = new Panel();
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                res [0]= true;
                dialog.setVisible(false);
            }
        });

        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        dialog.setVisible(true);
        return res[0];
    }

    public void createCountry(){
        try {
            PreparedStatement st = connection.prepareStatement("insert into pays(nom,capitale,pupilation,cantinent) value(?,?,?,?)");
            st.setString(1, input1.getText());
            st.setString(2, input2.getText());
            st.setInt(3, Integer.parseInt(input3.getText()));
            st.setString(4, input4.getText());
            int res = st.executeUpdate();
            if (res == 1) {
                System.out.println("Country added");
                return;
            }
            System.out.println("country not added");
            fetchFromDatabase();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (NumberFormatException ex){
            showErrorDialog(Atlas.this,"Please provide a valid integer for the pupilation");
        }
    }

    public void updateCountry(){
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE pays SET nom = ?, capitale = ?, pupilation = ?, cantinent = ? WHERE id = ?");
            st.setString(1, input1.getText());
            st.setString(2, input2.getText());
            st.setInt(3, Integer.parseInt(input3.getText()));
            st.setString(4, input4.getText());
            st.setString(5, data.getString(1));
            int res = st.executeUpdate();
            if (res == 1) {
                System.out.println("Country updated");
                return;
            }
            System.out.println("country not updated");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (NumberFormatException ex){
            showErrorDialog(Atlas.this,"Please provide a valid integer for the pupilation");
        }
    }

    public void deleteCountry(){
        try {
            PreparedStatement st = connection.prepareStatement("delete from pays WHERE id = ?");
            st.setString(1, data.getString(1));
            int res = st.executeUpdate();
            if (res == 1) {
                System.out.println("Country deleted");
                return;
            }
            System.out.println("country not deleted");
            fetchFromDatabase();
            data.first();
            updatePaysDetails();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public Atlas(){
        super("Atlas");
        setSize(400,400);
        this.setLayout(new BorderLayout());
        createConnection();
        fetchFromDatabase();

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                boolean confirmed = showConfirmDialog(Atlas.this,
                        "Are you sure you want to exit?", "Exit Confirmation");
                    System.exit(0);
            }
        });

        northPanel = new Panel(new GridLayout(1,5));

        cancelButton = new Button("annuler");
        cancelButton.setBackground(Color.CYAN);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        cancelButton.setEnabled(false);

        addButton = new Button("ajouter");
        addButton.setBackground(Color.BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent actionEvent) {
            input1.setText("");
            input2.setText("");
            input3.setText("");
            input4.setText("");
            toggleButtons(false);
            toggleInputs(true);
            mode = "add";
          }
        });
        addButton.setPreferredSize(new Dimension(100, 30));
        northPanel.add(addButton);

        updateButton = new Button("modifier");
        updateButton.setBackground(Color.YELLOW);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {;
                addButton.setEnabled(false);
                removeButton.setEnabled(false);
                confirmButton.setEnabled(true);
                toggleButtons(false);
                toggleInputs(true);
                mode = "update";
            }
        });
        updateButton.setPreferredSize(new Dimension(100, 30));
        northPanel.add(updateButton);

        confirmButton = new Button("valider");
        confirmButton.setEnabled(false);
    confirmButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent actionEvent) {
            if (mode.equals("add")) {
              createCountry();
            }
            if (mode.equals("update")) {
              updateCountry();
            }
            toggleButtons(true);
            toggleInputs(false);
            updatePaysDetails();
            mode = "";
          }
        });
        confirmButton.setPreferredSize(new Dimension(100, 30));
        northPanel.add(confirmButton);

        removeButton = new Button("supprimer");
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                boolean confirmed = showConfirmDialog(Atlas.this,
                        "Are you sure you want to delete this country?", "Delete Confirmation");
                if (confirmed) {
                    deleteCountry();
                }
            }
        });
        removeButton.setPreferredSize(new Dimension(100, 30));
        northPanel.add(removeButton);

        this.add(northPanel,BorderLayout.NORTH);

        westPanel = new Panel(new GridLayout(4,1));

        lbl1 = new Label("Nom");
        lbl1.setPreferredSize(new Dimension(100, 30));
        westPanel.add(lbl1);

        lbl2 = new Label("Capitale");
        lbl2.setPreferredSize(new Dimension(100, 30));
        westPanel.add(lbl2);

        lbl3 = new Label("Population");
        lbl3.setPreferredSize(new Dimension(100, 30));
        westPanel.add(lbl3);

        lbl4 = new Label("Continent");
        lbl4.setPreferredSize(new Dimension(100, 30));
        westPanel.add(lbl4);

        this.add(westPanel,BorderLayout.WEST);

        try{
            centerPanel = new Panel(new GridLayout(4,1));

            input1 = new TextField(data != null ? data.getString(2) : "",10);
            input1.setSize(400,180);
            centerPanel.add(input1);

            input2 = new TextField(data != null ? data.getString(3) : "",10);
            centerPanel.add(input2);

            input3 = new TextField(data != null ?data.getString(4):"",10);
            centerPanel.add(input3);

            input4 = new TextField(data != null ?data.getString(5): "",10);
            centerPanel.add(input4);
            toggleInputs(false);

            this.add(centerPanel,BorderLayout.CENTER);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    southPanel = new Panel(new GridBagLayout());

        firstButton = new Button("|<");
        firstButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    data.first();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                updatePaysDetails();
            }
        });
        firstButton.setPreferredSize(new Dimension(100, 30));
        southPanel.add(firstButton);

        backButton = new Button("<<");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (data.isBeforeFirst()){
                        data.previous();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                updatePaysDetails();
            }
        });
        backButton.setPreferredSize(new Dimension(100, 30));
        southPanel.add(backButton);

        nextButton = new Button(">>");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                   if (data.isAfterLast()){
                       data.next();
                   }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                updatePaysDetails();
            }
        });
        nextButton.setPreferredSize(new Dimension(100, 30));
        southPanel.add(nextButton);

        lastButton = new Button(">|");
    lastButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent actionEvent) {
            try {
              data.last();
            } catch (SQLException e) {
              System.out.println(e.getMessage());
            }
            updatePaysDetails();
          }
        });
        lastButton.setPreferredSize(new Dimension(100, 30));
        southPanel.add(lastButton);

        this.add(southPanel,BorderLayout.SOUTH);

        this.setVisible(true);
    }
}
