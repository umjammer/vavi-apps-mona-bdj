/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.mona;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;

import org.dvb.ui.FontFactory;
import org.havi.ui.HScene;
import org.havi.ui.HSceneFactory;

import vavi.apps.mona.Mona.MonaEvent;
import vavi.apps.mona.Mona.MonaListener;


/**
 * Mona.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 080829 nsano initial version <br>
 * @see "http://www.monazilla.org/"
 */
public class MonaApp implements Xlet {

    /** */
    private HScene scene;
    /** */
    private MyView gui;
    /** */
//    private XletContext context;

    /* */
    public void initXlet(XletContext context) {
//        this.context = context;

        this.scene = HSceneFactory.getInstance().getDefaultHScene();
        try {
            this.gui = new MyView();

            gui.setBounds(scene.getBounds());
            scene.add(gui, BorderLayout.CENTER);
        } catch (Exception e) {
e.printStackTrace(System.err);
        }

        scene.validate();
    }

    /* */
    public void startXlet() {
        gui.setVisible(true);
        scene.setVisible(true);
        gui.requestFocus();

        new Thread(gui).start();
    }

    /* */
    public void pauseXlet() {
        gui.setVisible(false);
    }

    /* */
    public void destroyXlet(boolean unconditional) {
        scene.remove(gui);
        scene = null;
    }

    /**
     * <li> {@link #addNotify()} �ŃR���|�[�l���g���A�N�e�B�u�ɂȂ����̂�����݂��� 
     */
    private class MyView extends Container implements Runnable {

        /** */
        private KeyListener keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                switch (mode) {
                case MODE_BOARD:
                    boardVC.keyPressed(event);
                    break;
                case MODE_THREAD:
                    threadVC.keyPressed(event);
                    break;
                case MODE_VIEW:
                    viewVC.keyPressed(event);
                    break;
                }
                repaint();
            }
        };

        /** */
        class BoardVC {

            /** model */
            private List boards;

            void setModel(List boards) {
                this.boards = boards;
                line = 0;
                page = 0;
            }

            private static final int LINES = 10;

            private int line = 0;
            private int page = 0;

            int getLineCount() {
                return Math.min(LINES, boards.size() - page * LINES);
            }
            
            int getPageCount() {
                return (boards.size() + LINES - 1) / LINES;
            }

            int getCurrentIndex() {
                return page * LINES + line;
            }

            int getIndex(int i) {
                return page * LINES + i;
            }

            void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_UP:
                    line--;
                    if (line < 0) {
                        line = getLineCount() - 1;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    page--;
                    if (page < 0) {
                        page = getPageCount() - 1;
                    }
                    if (line > getLineCount() - 1) {
                        line = getLineCount() - 1;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    page++;
                    if (page >= getPageCount()) {
                        page = 0;
                    }
                    if (line > getLineCount() - 1) {
                        line = getLineCount() - 1;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    line++;
                    if (line >= getLineCount()) {
                        line = 0;
                    }
                    break;
                case KeyEvent.VK_ENTER: // ��
                    try {
                        String boardName = ((BbsBoard) boards.get(getCurrentIndex())).name;
                        mona.setTargetBoardByName(boardName);
                        threadVC.setModel(mona.getThreads());
                        mode = MODE_THREAD;
System.err.println("MODE: THREAD");
                    } catch (Exception e) {
e.printStackTrace(System.err);
status = "5:" + e;                        
                    }
                    break;
                case 461:               // ��
                case KeyEvent.VK_SPACE:
                    break;
                }    
//System.err.println("boardLine: " + boardLine + "/" + (getLineCount() - 1) + ", " + boardPage + "/" + (getPageCount() - 1));
            }

            /** */
            void paint(Graphics g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setFont(font);

                g.setColor(Color.yellow);
                String boardMode = "�� �I�� " + (page + 1) + "/" + getPageCount();
                g.drawString(boardMode, fontSize * 3, fontSize * 3); 

                for (int i = 0; i < getLineCount(); i++) {
                    String cursor;
                    if (line == i) {
                        g.setColor(Color.blue);
                        cursor = "��";
                    } else {
                        g.setColor(Color.white);
                        cursor = "�@";
                    }
                    BbsBoard board = (BbsBoard) boards.get(getIndex(i));
                    String boardName = cursor + board.name;
                    g.drawString(boardName, fontSize * 3, i * fontSize * 2 + fontSize * 5); 
                }
            }
        }

        class ThreadVC {

            /** model */
            private List threads;

            private static final int LINES = 10;

            private int line = 0;
            private int page = 0;

            int getLineCount() {
                return Math.min(LINES, threads.size() - page * LINES);
            }
            
            int getPageCount() {
                return (threads.size() + LINES - 1) / LINES;
            }

            int getCurrentIndex() {
                return page * LINES + line;
            }

            int getIndex(int i) {
                return page * LINES + i;
            }

            void setModel(List threads) {
                this.threads = threads;
                line = 0;
                page = 0;
            }

            void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_UP:
                    line--;
                    if (line < 0) {
                        line = getLineCount() - 1;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    page--;
                    if (page < 0) {
                        page = getPageCount() - 1;
                    }
                    if (line > getLineCount() - 1) {
                        line = getLineCount() - 1;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    page++;
                    if (page >= getPageCount()) {
                        page = 0;
                    }
                    if (line > getLineCount() - 1) {
                        line = getLineCount() - 1;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    line++;
                    if (line >= getLineCount()) {
                        line = 0;
                    }
                    break;
                case KeyEvent.VK_ENTER: // ��
                    try {
                        BbsThread thread = (BbsThread) threads.get(getCurrentIndex());
                        mona.setTargetThread(thread);
                        mona.startLive();
    
                        Thread viewThread = new Thread(viewVC);
                        viewThread.start();
    
                        mode = MODE_VIEW;
System.err.println("MODE: VIEW");
                    } catch (Exception e) {
status = "4:" + e;
                    }
                    break;
                case 461:               // ��
                case KeyEvent.VK_SPACE:
                    mode = MODE_BOARD;
System.err.println("MODE: BOARD");
                    break;
                }    
//System.err.println("threadLine: " + threadLine + "/" + (getLineCount() - 1) + ", " + threadPage + "/" + (getPageCount() - 1));
            }

            void paint(Graphics g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setFont(font);

                g.setColor(Color.yellow);
                String threadMode = "�� �X���I�� " + (page + 1) + "/" + getPageCount();
                g.drawString(threadMode, fontSize * 3, fontSize * 3); 

                for (int i = 0; i < getLineCount(); i++) {
                    String cursor;
                    if (line == i) {
                        g.setColor(Color.blue);
                        cursor = "��";
                    } else {
                        g.setColor(Color.white);
                        cursor = "�@";
                    }
                    BbsThread thread = (BbsThread) threads.get(getIndex(i));
//                    String dateFormat = "yyyy/MM/dd HH:mm:ss";
                    String threadName = cursor + thread.getTitle() + " (" + thread.getResponses() + ")" /* + "         " + new SimpleDateFormat(dateFormat).format(new Date(thread.sinse)) */;
                    g.drawString(threadName, fontSize * 3, i * fontSize * 2 + fontSize * 5); 
                }
            }
        }

        /** */
        class ViewVC implements MonaListener, Runnable {

            private boolean viewLoop;

            void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_UP:
                    // back scroll �Ƃ�
                    break;
                case KeyEvent.VK_LEFT:
                    if (wait <= 950) {
                        wait += 50;
                    }
//System.err.println("wait: " + wait);
status = "wait: " + wait;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (wait > 50) {
                        wait -= 50;
                    }
//System.err.println("wait: " + wait);
status = "wait: " + wait;
                    break;
                case KeyEvent.VK_DOWN:
                    break;
                case KeyEvent.VK_ENTER: // ��
                    break;
                case 461:               // ��
                case KeyEvent.VK_SPACE:
                    try {
                        if (!mona.isLiveStopped()) {
                            mona.stopLive();
                        }
                        threadVC.setModel(mona.getThreads());
                        viewLoop = false;
                        mode = MODE_THREAD;
System.err.println("MODE: THREAD");
                    } catch (Exception e) {
e.printStackTrace(System.err);
status = "3:" + e;                        
                    }
                    break;
                }    
            }

            /** model */
            private List lines = new Vector();

            /* */
            public void whenThreadUpdated(MonaEvent event) {
                List datum = event.getBbsDatum();
                for (int i = 0; i < datum.size(); i++) {
                    BbsData data = (BbsData) datum.get(i);
//System.err.println(data.toStringAsFormated() + "\n");
                    StringTokenizer lines = new StringTokenizer(data.toStringAsFormated(), "\n");
                    while (lines.hasMoreTokens()) {
                        String line = lines.nextToken();
                        this.lines.add(line);
                    }
                }
            }

            /* */
            public void whenMessageUpdated(MonaEvent event) {
                status = event.getMessage();
            }

            /* */
            public void whenThreadEnd(MonaEvent event) {
            }

            /* */
            public void debug(Throwable t) {
                throwable = t;
            }

            /* */
            public void debug(String message) {
                status = message;
            }

            /** off screen image */
            private Image image;
            /** graphics for off screen image */
            private Graphics graphics;

            /** */
            int currentLine = 0;
            /** */
            long wait = 500;

            /* */
            public void run() {
System.err.println("SCROLL: start"/* + getHeight() / (fontSize * 2) */);
                viewLoop = true;
                lines.clear();
                int page = getHeight() / (fontSize * 2);

                graphics.setColor(Color.black);
                graphics.fillRect(0, 0, getWidth(), getHeight());
                repaint();

                while (viewLoop) {
                    try {
    
                        graphics.setFont(font);
    
                        while (viewLoop && lines.size() > 0) {
                            String line = (String) lines.get(0);
                            lines.remove(0);

                            if (currentLine < page - 1) {
                                currentLine++;
                            } else {
                                graphics.copyArea(fontSize * 2, fontSize * 2, getWidth(), getHeight(), 0, -fontSize * 2);
                            }
                            graphics.setColor(Color.black);
                            graphics.fillRect(fontSize * 2, currentLine * fontSize * 2, getWidth(), fontSize * 2);
                            graphics.setColor(Color.white);
                            graphics.drawString(line, fontSize * 2, currentLine * fontSize * 2);
                            if (lines.size() < 100) {
                                repaint();
                                Thread.sleep(wait);
                            }
                        }

                        Thread.sleep(1000);
                    } catch (Exception e) {
e.printStackTrace(System.err);
status = "1:" + e;                        
                    }
                }
System.err.println("SCROLL: stop");
            }

            /** */
            void paint(Graphics g) {
                g.drawImage(image, 0, 0, MyView.this);
            }

            /** */
            void initImage(int w, int h) {
                this.image = createImage(w, h);
                this.graphics = image.getGraphics();
            }
        }

        /** engine */
        private Mona mona;

        // models

        private static final int MODE_INIT = 0;
        private static final int MODE_BOARD = 1;
        private static final int MODE_THREAD = 2;
        private static final int MODE_VIEW = 3;

        /** */
        private int mode = MODE_INIT;

        // view/controllers

        /** */
        private BoardVC boardVC;
        /** */
        private ThreadVC threadVC;
        /** */
        private ViewVC viewVC;

        // view attributes

        /** */
        private int fontSize;
        /** */
        private Font font;

        /** */
        private String status = "";
        Throwable throwable;

        /** */
        MyView() {
            boardVC = new BoardVC();
            threadVC = new ThreadVC();
            viewVC = new ViewVC();

            addKeyListener(keyListener);
        }

        /** */
        boolean notified;

        /* */
        public void addNotify() {
            super.addNotify();
            notified = true;
        }

        /* */
        public void run() {
            // 1.
            try {
                mona = new Mona();
                boardVC.setModel(mona.getBoardsByCategory("����ch"));

                mona.addMonaListener(viewVC);
            } catch (Exception e) {
e.printStackTrace(System.err);
status = e.toString();
            }

            // 2.
            while (!notified) {
                Thread.yield();
            }

            // 3.
//          if (Util.getPlatform().equals(Util.BDJ_PS3)) {
            fontSize = getHeight() > 720 ? 36 : 12;
try {
            FontFactory fontFactory = new FontFactory();
            font = fontFactory.createFont("mikachan", Font.PLAIN, 36);
System.err.println("fontSize: " + fontSize);
} catch (Exception e) {
status = e.toString();
}
//System.err.println("PLATFORM: " + Util.getPlatform());
//        } else {
//            fontSize = 12;
//System.err.println("PLATFORM: unknown: " + Util.getPlatform());
//        }

            viewVC.initImage(getWidth(), getHeight());

            // 3.
//try { Thread.sleep(2000); } catch (Exception e) {}
            mode = MODE_BOARD;
System.err.println("MODE: BOARD");
repaint();
        }

        /** */
//        public void update(Graphics g) {
//            if (mode == MODE_VIEW) {
//                viewVC.paint(g);
//            } else {
//                super.update(g);
//            }
//        }

        /* */
        public void paint(Graphics g) {
            switch (mode) {
//case MODE_INIT:
// g.setColor(Color.pink);
// g.fillRect(0, 0, getWidth(), getHeight());
// g.setColor(Color.blue);
// g.drawString("BD-J Mona (c) 2008 VAVI", 36, 36); 
// g.drawString(status, 36, 60); 
// break;
            case MODE_BOARD:
                boardVC.paint(g);
                break;
            case MODE_THREAD:
                threadVC.paint(g);
                break;
            case MODE_VIEW:
                viewVC.paint(g);
                break;
            }

g.setColor(Color.lightGray);
if (throwable != null) {
 try {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  PrintStream ps = new PrintStream(baos);
  throwable.printStackTrace(ps);
  String stackTraces = baos.toString();
  StringTokenizer st = new StringTokenizer(stackTraces, "\n");
  int i = 0;
  g.setColor(Color.red);
  while (st.hasMoreTokens()) {
   g.drawString(st.nextToken(), 0, fontSize * i + 36);
   i++;
  }
 } catch (Exception e) {
  String x = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L + "KB/" + Runtime.getRuntime().totalMemory() / 1024L + "KB" + ": 6: " + e;
  g.drawString(x, 0, getHeight() - 36);
 }
 throwable = null;
} else {
 String x = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L + "KB/" + Runtime.getRuntime().totalMemory() / 1024L + "KB" + ": " + status;
 g.drawString(x, 0, getHeight() - 36);
}
        }
    }
}

/* */
