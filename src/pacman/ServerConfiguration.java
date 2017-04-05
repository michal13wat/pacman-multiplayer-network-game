package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by User on 2017-02-28.
 */
public class ServerConfiguration extends JFrame implements ActionListener {
    static String port = "80";                       // default Port
    static String adresIP = "192.168.0.101"; ;       // default IP

    private JButton okButtonn;
    private JButton cancelButton;
    private JTextArea textArea = new JTextArea();
    private JTextArea textPort = new JTextArea();
    private JLabel serverName = new JLabel();
    private JLabel serverPort = new JLabel();
    private JPanel textInputs = new JPanel();
    private JPanel butt = new JPanel();


    public ServerConfiguration (){
        super("Konfiguracja serwera");
        setPreferredSize(new Dimension(250, 120));
        //setSize(new Dimension(300, 100));
        setLocation(400, 200);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        okButtonn = new JButton("OK");
        cancelButton = new JButton("Anuluj");

        serverName.setPreferredSize(new Dimension(60, 20));
        serverName.setText("Adres IP: ");
        textInputs.add(serverName);

        textArea.addKeyListener(new keyListener());
        textArea.setBackground(Color.white);
        textArea.setPreferredSize(new Dimension(100, 20));
        textArea.insert(adresIP, 0);
        textInputs.add(textArea);

        serverPort.setPreferredSize(new Dimension(60, 20));
        serverPort.setText("PORT: ");
        textInputs.add(serverPort);

        textPort.addKeyListener(new keyListener());
        textPort.setBackground(Color.white);
        textPort.setPreferredSize(new Dimension(60, 20));
        textPort.insert(port, 0);
        textInputs.add(textPort);

        textInputs.setLayout(new GridLayout(2, 2, 0, 10));
        //textInputs.setBackground(Color.red);
        add(textInputs);

        cancelButton.addActionListener(this);
        okButtonn.addActionListener(this);

//        GridLayout testsLayout = new GridLayout(3, 2, 10, 10);
//        add(serverName);
//        add(textArea);
//        add(serverPort);
//        add(textPort);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        butt.add(cancelButton);
        butt.add(okButtonn);
        butt.setLayout(new FlowLayout());
        //butt.setBackground(Color.green);
        add(butt);

        setLayout(new GridLayout(2, 1));

    }

//    public void SetIpAndPORT (IPAddressName numerIP, int port){
//        this.numerIP = numerIP;
//        this.port = port;
//        System.out.print("Konfiguracja serwera przebiegła pomyślnie!\n");
//    }

    public void connect (){
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == okButtonn){
            adresIP = textArea.getText();
            port = textPort.getText();
            super.hide();
        }
        else if(source == cancelButton)
            super.hide();
    }

    class keyListener implements KeyListener {
        keyListener(){
            addKeyListener(this);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
        }

        @Override
        public void keyReleased(KeyEvent evt) {
        }

        @Override
        public void keyTyped(KeyEvent evt) {
        }
    }

}
