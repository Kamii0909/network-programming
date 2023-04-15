package edu.hust.it4060.common.blocking;

import static edu.hust.it4060.common.DelimiterConstants.CONNECTION_CLOSE_ACCEPTED;
import static edu.hust.it4060.common.DelimiterConstants.CONNECTION_CLOSE_REQUEST;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public abstract class AbstractBlockingSocketAdapter extends BlockingSocketAdapter
        implements SocketStateAware, BlockingTextIOSocketAdapter {
    private String delimiter;
    private PrintWriter out;
    private boolean closing;

    protected AbstractBlockingSocketAdapter(Socket socket, String delimiter) {
        super(socket);
        this.delimiter = delimiter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle() {
        try (var in = new Scanner(socket.getInputStream(), UTF_8)) {
            in.useDelimiter(delimiter);
            out = new PrintWriter(socket.getOutputStream());

            beforeSocketRead();

            String s;
            while ((s = in.next()) != null) {
                if (shouldCloseSocket(s)) {
                    beforeSocketClosed();
                    socket.close();
                    break;
                }
                newLineRead(s);
            }
            afterSocketClosed();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @Override
    public final void sendString(String string) {
        out.print(string);
        out.print(delimiter);
        out.flush();
    }

    @Override
    public final void closeSocket() {
        sendString(CONNECTION_CLOSE_REQUEST);
        this.closing = true;
    }

    private boolean shouldCloseSocket(String s) {
        if (s.equals(CONNECTION_CLOSE_REQUEST)) {
            sendString(CONNECTION_CLOSE_ACCEPTED);
            return true;
        }
        return s.equals(CONNECTION_CLOSE_ACCEPTED) && closing;
    }

}
