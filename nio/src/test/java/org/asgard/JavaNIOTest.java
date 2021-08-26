package org.asgard;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class JavaNIOTest {

  @Test
  public void readFile() throws IOException {
    RandomAccessFile file = new RandomAccessFile("src/test/resources/test.txt", "r");

    FileChannel channel = file.getChannel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    while (buffer.hasRemaining()) {
      channel.read(buffer);
      buffer.flip();
      System.out.println(new String(buffer.array()));
    }
    channel.close();
    file.close();
  }

  @Test
  public void writeDataToFile() throws IOException {
    RandomAccessFile file = new RandomAccessFile("src/test/resources/test2.txt", "rw");

    String src = "new string to write it to file";
    FileChannel channel = file.getChannel();

    ByteBuffer buffer = ByteBuffer.allocate(8);

    byte[] bytes = src.getBytes();
    for (byte aByte : bytes) {
      System.out.println(new String(new byte[]{aByte}));
      if (buffer.limit() == buffer.position()) {
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
      }
      buffer.put(aByte);
    }
    buffer.flip();
    channel.write(buffer);
    buffer.clear();

    channel.close();
    file.close();
  }
}
