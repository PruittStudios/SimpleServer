/*******************************************************************************
 * Open Source Initiative OSI - The MIT License:Licensing
 * The MIT License
 * Copyright (c) 2010 Charles Wagner Jr. (spiegalpwns@gmail.com)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package simpleserver.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SystemInputQueue {
  private final BlockingQueue<String> queue;
  private final BufferedReader input;
  private final Reader reader;

  private boolean run = true;

  public SystemInputQueue() {
    queue = new LinkedBlockingQueue<String>();
    input = new BufferedReader(new InputStreamReader(System.in));

    reader = new Reader();
    reader.start();
    reader.setName("SystemInputQueue");
  }

  public String nextLine() throws InterruptedException {
    return queue.take();
  }

  public void appendLine(String line) {
    queue.add(line);
  }

  public void stop() {
    run = false;
    reader.interrupt();
  }

  private final class Reader extends Thread {
    @Override
    public void run() {
      while (run) {
        try {
          StringBuffer buffer = new StringBuffer();
          while (input.ready()) {
            int character = input.read();
            if (character == -1) {
              run = false;
              break;
            }
            else if ((char) character != '\n') {
              buffer.append((char) character);
            }
            else {
              String line = buffer.toString();
              buffer = new StringBuffer();

              if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
              }

              try {
                queue.put(line);
              }
              catch (InterruptedException e) {
                continue;
              }
            }
          }
        }
        catch (IOException e) {
          break;
        }

        try {
          Thread.sleep(50);
        }
        catch (InterruptedException e) {
        }
      }
    }
  }
}