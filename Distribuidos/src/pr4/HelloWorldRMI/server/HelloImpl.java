package pr4.HelloWorldRMI.server;

import java.rmi.*;
import java.rmi.server.*;

import pr4.HelloWorldRMI.common.HelloInterface;

/**
 * This class implements the remote interface HelloInterface.
 * @author M. L. Liu
 */

public class HelloImpl extends UnicastRemoteObject
     implements HelloInterface {
  
   public HelloImpl() throws RemoteException {
      super( );
   }
  
   public String sayHello() throws RemoteException {
      return "Hello, World!";
   }
} // end class
