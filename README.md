# Centralized P2P Simulation with Java RMI

## Overview
This project is a simulation of Peer-to-Peer (P2P) network with made with the purpose of studying Java's Remote Method Invocation (RMI). It models the intricate dynamics of peer communication and server interaction within a P2P environment, leveraging the power of Java RMI for distributed computing. 

## Features
- **Peer Communication**: Peers to establish connections and communicate directly with each other and the server.
- **Server Interaction**: Just like in a centralized P2P environment, a central server is deployed to handle basic user request and that facilitate peer discovery and management.
- **Educational Value**: This project was made to practice RMI in Java, and could therefore serve as a good example to teach the basics of RMI. Moreover, the simulation can be used to teach the principles of centralized P2P networks.
- **Fault Tolerance**: Incorporates mechanisms to handle potential network / IO failures.
- **Local Deployment**: Unlike traditional P2P networks where peers run on different machines, our simulation focuses on a local environment. Both the central server and the peers operate within the same system on your machine, making it ideal for learning and experimentation.

## Getting Started
To run the simulation:
1. Clone the repository.
2. Run the Coordinator class (Server).
3. Run peer instances and interact with the network.
4. Check the "peers" folder in the project folder to see how files are exchanged between peers.

## Example:
Here is a basic example on the input:
1. Server:
   Server ready

2. Peer instance 1:
   Ahoy!
   Enter 1 to login or 2 to sign up: 1
   Enter your reference number: 1
   Enter your password: 1
   Aye aye! Successfully logged in!
   Peer ready
   Enter 1 to add a file, 2 to request file, or 3 to exit: 1
   Enter the full path of the file you want to add: C:\Users\User\test.txt
   You've already added a file with the same name!
   Enter 1 to add a file, 2 to request file, or 3 to exit: 1
   Enter the full path of the file you want to add: C:\Users\User\Desktop\test.txt
   File added successfully!
   Enter 1 to add a file, 2 to request file, or 3 to exit: 3
   Farewell, old friend

3. Peer instance 2:
  Ahoy!
  Enter 1 to login or 2 to sign up: 2
  Enter your new reference number: 50
  Enter your new password: 50
  Account created successfully! Welcome aboard!
  Enter 1 to add a file, 2 to request file, or 3 to exit: Peer ready
  2
  Enter the name of the file you want to look up: test.txt
  File found with with reference 1
  Grabbing the file from peer 1...
  File downloaded successfully!
  Enter 1 to add a file, 2 to request file, or 3 to exit: 3
  Farewell, old friend

Note: Peer inctances 1 and 2 must be running at the same time in order for them to exchange files.
