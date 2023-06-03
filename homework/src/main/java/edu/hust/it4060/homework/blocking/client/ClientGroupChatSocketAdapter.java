package edu.hust.it4060.homework.blocking.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.kien.network.core.support.adapter.AbstractLineBasedBlockingSocketAdapter;

class ClientGroupChatSocketAdapter extends AbstractLineBasedBlockingSocketAdapter {
    class AllowOnlyLastLineChanged extends DocumentFilter {
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (allowChange(offset)) {
                super.remove(fb, offset, length);
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
            if (allowChange(offset)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
            if (allowChange(offset)) {
                super.insertString(fb, offset, string, attr);
            }
        }
        
        private boolean allowChange(int offset) {
            int lineCount = textArea.getLineCount();
            if (lineCount == 0) {
                return true;
            }
            try {
                return offset >= textArea.getLineStartOffset(lineCount - 1);
            } catch (BadLocationException e) {
                e.printStackTrace();
                throw new AssertionError("Internal error", e);
            }
        }
    }
    
    class SendLineWhenEnter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                return;
            }
            
            try {
                int currentPosition = textArea.getLineOfOffset(textArea.getCaretPosition()) - 1;
                int lineCount = textArea.getLineCount() - 2;
                if (currentPosition != lineCount) {
                    return;
                }
                String toBeSent = textArea.getText(
                    textArea.getLineStartOffset(lineCount),
                    textArea.getLineEndOffset(lineCount) - textArea.getLineStartOffset(lineCount));
                sendLine(toBeSent);
            } catch (BadLocationException e1) {
                // Won't happen
                e1.printStackTrace();
            }
        }
    }
    
    private JTextArea textArea;
    
    public ClientGroupChatSocketAdapter() {
        textArea = new JTextArea(20, 50);
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(textArea);
        frame.setVisible(true);
        ((AbstractDocument) textArea.getDocument()).setDocumentFilter(new AllowOnlyLastLineChanged());
        textArea.addKeyListener(new SendLineWhenEnter());
        
    }
    
    protected void newLineRead(String newLine) {
        textArea.append(newLine);
        textArea.append(System.lineSeparator());
    }
    
}
