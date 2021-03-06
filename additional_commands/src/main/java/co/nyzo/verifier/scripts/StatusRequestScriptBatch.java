package co.nyzo.verifier.scripts;

import co.nyzo.verifier.*;
import co.nyzo.verifier.messages.StatusResponse;
import co.nyzo.verifier.messages.debug.NewVerifierTallyStatusResponse;
import co.nyzo.verifier.util.IpUtil;
import co.nyzo.verifier.util.UpdateUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class StatusRequestScriptBatch {

    public static void main(String[] args) {

        String filename = "/root/ips.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
          
  
          for(String line : lines) {
                      
              // Get the IP address of the verifier from the argument.
              byte[] ipAddress = IpUtil.addressFromString(line);
              if (ByteUtil.isAllZeros(ipAddress)) {
                  System.out.println("please provide a valid IP address");
                  return;
              }
      
              // Send the request to our verifier.
              AtomicBoolean receivedResponse = new AtomicBoolean(false);
              Message message = new Message(MessageType.StatusRequest17, null);
              Message.fetch(IpUtil.addressAsString(ipAddress), MeshListener.standardPort, message, new MessageCallback() {
                  @Override
                  public void responseReceived(Message message) {
      
                      if (message == null) {
                          System.out.println("response message is null");
                      } else {
      
                          // Get the response object from the message.
                          StatusResponse response = (StatusResponse) message.getContent();
      
                          // Print the response.
                          for (String line : response.getLines()) {
                              System.out.println(line);
                          }
                      }
                      receivedResponse.set(true);
                  }
              });
      
              // Wait for the response to return.
              while (!receivedResponse.get()) {
                  try {
                      Thread.sleep(300L);
                  } catch (Exception ignored) { }
              }
          }
        } catch (Exception ignored) { }
        // Terminate the application.
        UpdateUtil.terminate();
    }
}
