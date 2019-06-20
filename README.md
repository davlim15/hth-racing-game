# hth-racing-game
Objective:
Press the correct highlighted buttons to reach the finish line before the other player.

How it works:
The server program is opened first to open the socket for the client to connect to. The client program is then opened and connects to the server. The server is Player 1 and the client is Player 2.
Once the server and client are opened, a JOptionPane opens for both players, asking the player for their name. The game begins once both players have entered their names.
The GUI consists of the two players’ cars lined up on a racetrack background and a row of 8 buttons on the bottom of the window where the specified button to press is highlighted yellow.
In order to move forward, the player must press the highlighted buttons that show up. The buttons that have to be pressed are predetermined by an array of random numbers. The first person to hit 29 correct buttons wins. A JMessageDialog will pop up with the name of the person who won, and the buttons will be disabled.
The position of each car is relative to the position of the background and how many buttons each player has pressed. For example, if P1 has pressed one button more than P2, P1’s car will show to be ahead of P2’s car by one unit on both windows.

Problems Enountered:
Network Communication – One of the main issues we faced was how to update P2’s window whenever P1 moved. We resolved this using an ActionListener with a timer that repainted the coordinates of every image on the screen every few milliseconds. The button presses were handled using a separate listener, simply updating the values of their windows locally. The timer listener handles the duties of sending the necessary data to the other program to reflect the number of button presses that it has made. In order for the two programs to sync, both programs will send and receive data at the same time. If the values happen to change during that exchange, the changes will show on the next repaint.
Encapsulation – Sine the timer listener was initially a separate class, we had issues with the listener finding the networking objects located in the constructor. To circumvent this, we wrote the timer listener inside the constructor where we declare and instantiate the object for the first time.
