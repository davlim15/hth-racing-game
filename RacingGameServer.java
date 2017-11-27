import java.net.*; //ServerSocket, Socket
import java.io.*; //BufferedReader, IOException, InputStreamReader, OutputStream, PrintWriter
import java.util.*; //TESTING ONLY
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import sun.audio.*;

public class RacingGameServer extends JFrame
{
   private Container contents;
   private JLabel test, pictest;
   private JPanel bottompanel;
   private JButton[] button = new JButton[8];
   private Image background = new ImageIcon("track.png").getImage();
   private Image blue = new ImageIcon("BlueCar.png").getImage();
   private Image red = new ImageIcon("RedCar.png").getImage();
   private Image duck = new ImageIcon("duck.png").getImage();
   private Image thomas = new ImageIcon("thomas.png").getImage();
   private int startp1x = 0, startp2x = 0, startbx = 0, index = 0, index2 = 0; //background will become negative
   private int[] presses = new int[29];
   private String name, name2;
   private boolean loop = true;
   
   public RacingGameServer() throws IOException
   {
      ServerSocket listener = new ServerSocket(49173);
      System.out.println("Waiting for client to connect...");
      Socket socket = listener.accept();
      System.out.println("Connected!");
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream())); //reads data sent from client
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      
      name = JOptionPane.showInputDialog(null, "Player 1: Enter your name:");
      out.println(name); //send name to server
      name2 = br.readLine(); //save name sent from server
      
      Random random = new Random();
      
      //Array of buttons to press to move car
      for (int i = 0; i < 29; i++)
      {
         presses[i] = random.nextInt(8);
      }
      
      contents = getContentPane();
      contents.setLayout(new BorderLayout());
      bottompanel = new JPanel();
      bottompanel.setLayout(new GridLayout(1,8));
      bottompanel.setPreferredSize(new Dimension(500,95));
      button[0] = new JButton("1");
      button[1] = new JButton("2");
      button[2] = new JButton("3");
      button[3] = new JButton("4");
      button[4] = new JButton("5");
      button[5] = new JButton("6");
      button[6] = new JButton("7");
      button[7] = new JButton("8");
      
      //Button Listener
      ButtonHandler bh = new ButtonHandler();
      for (int i = 0; i < 8; i++)
      {
         button[i].addActionListener(bh);
      }
      
      //Highlight first button to press
      button[presses[index]].setBackground(Color.YELLOW);
      
      for (int i = 0; i < 8; i++)
      {
         bottompanel.add(button[i]);
      }

      contents.add(bottompanel, BorderLayout.SOUTH);
      
      Timer timer = new Timer(100, new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            //read in player 2 index
            out.println(index); //keeps track of how many buttons P1 pressed
            try
            {
               index2 = Integer.parseInt(br.readLine());
            }
            catch (IOException io) {}
            //modify P2 position using index value
            startp2x = startbx + (166 * index2);
            repaint();
            
            if (index2 == 29 && loop == true)
            {
               for (int i = 0; i < 8; i++)
               {
                  button[i].setEnabled(false);
               }
               JOptionPane.showMessageDialog(null, name2 + " wins!!!");
               loop = false; //if condition can only be true once
            }
         }
      });
      timer.start();
      
      setSize(500,600);
      setResizable(false);
      setTitle("Player 1: " + name);
      setVisible(true);
   }
   
   public void paint(Graphics g)
   {
      super.paint(g);
      g.drawImage(background, startbx, 0, null);
      if (name.equals("duck"))
         g.drawImage(duck, startp1x, 164, null);
      else
         g.drawImage(blue, startp1x, 164, null);
      if (name2.equals("train"))
         g.drawImage(thomas, startp2x, 260, null);
      else
         g.drawImage(red, startp2x, 260, null);
   }
   
   //Player has to be first to click correct buttons 29 times to win
   //Last 2 clicks, the player's car will move forward
   private class ButtonHandler implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         if (e.getSource()==button[presses[index]])
         {
            if (index >= 27)
            {
               startp1x = startp1x + 166;
            }
            else
            {
               startbx = startbx - 166;
               startp2x = startp2x - 166; //server is P1
            }
            //Checks for the end of the 29 clicks. Changes button colors
            if (index+1 < presses.length)
            {
               button[presses[index]].setBackground(null);
               button[presses[index+1]].setBackground(Color.YELLOW);
               index++;
               //change x value for other car through the network
               //and constantly update that with minimal delay
            }
            else
            {
               for (int i = 0; i < 8; i++)
               {
                  button[i].setEnabled(false);
               }
               index++;
               JOptionPane.showMessageDialog(null, name + " wins!!!");
            }
         }
      }
   }
   
   public static void main(String[] args) throws IOException
   {
      RacingGameServer app = new RacingGameServer();
      app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
}

/*
   Functionality:
   When P1 presses a correct button
      //P1 Window: Both the background and other car move back the same number of pixels behind the P1 car
      //P2 Window: P1 car will move forward a certain number of pixels ahead of the P2 car
   Same with P2
   
   When the x position of the background reaches about -4500, the player's car will move instead of the background.
*/

//Whoever reaches index of 28 first wins.
//when P1 wins, P1 will be notified through the button press listener.
//P2 will be notified that P1 won through the listener reading in data
//Buttons disabled from both listeners