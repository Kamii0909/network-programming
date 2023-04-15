package edu.hust.it4060.common.blocking;

public interface BlockingTextIOSocketAdapter {
    /**
     * This method is always called when a new line is read from the socket input.
     */
    public void newLineRead(String line);

    /**
     * This method is called to write to the TCP Connection a line. An endline
     * (`CRLF`) is automatically added at the end.
     */
    public void sendString(String line);
}
