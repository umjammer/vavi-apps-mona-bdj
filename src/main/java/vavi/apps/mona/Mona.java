/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.mona;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Mona.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 080902 nsano initial version <br>
 */
public class Mona {

    /** */
    private static final String boardUrl = "http://menu.2ch.net/bbsmenu.html";

    /** */
    private List boards;

    /** */
    private BbsBoardsFactory bbsBoardsFactory = new MyBbsBoardsFactory();

    /** */
    public Mona() {
        try {
            this.boards = bbsBoardsFactory.readFrom(boardUrl);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    public List getBoardsByCategory(String category) {
        List selectedBards = new ArrayList();
        for (int i = 0; i < boards.size(); i++) {
            BbsBoard board = (BbsBoard) boards.get(i);
            if (board.category.equals(category)) {
//System.err.println(board.category + ", " + board.name + ", " + board.url);
                selectedBards.add(board);
            }
        }
        return selectedBards;
    }

    /** */
    private BbsBoard targetBoard;

    /** */
    private BbsThreadsFactory bbsThreadsFactory = new MyBbsThreadsFactory();

    /** */
    public void setTargetBoardByName(String name) {
        for (int i = 0; i < boards.size(); i++) {
            BbsBoard board = (BbsBoard) boards.get(i);
            if (board.name.equals(name)) {
                targetBoard = board;
System.err.println("BOARD: current is " + targetBoard);
                return;
            }
        }
        throw new IllegalArgumentException("no such board name: " + name);
    }

    /** */
    public List getThreads() throws Exception {
        if (targetBoard == null) {
            throw new IllegalStateException("targetBoard has not set");
        }

        List threads = bbsThreadsFactory.readFrom(targetBoard);
        Collections.sort(threads);
//for (int i = 0; i < threads.size(); i++) {
// BbsThread thread = (BbsThread) threads.get(i);
// System.err.println(thread);
//}

        return threads;
    }

    /** */
    private static final long interval = 3000;

    /** */
    public class MonaEvent extends EventObject {
        private List data;
        private BbsThread thread;
        private String message;
        /** */
        public MonaEvent(Object source, String message) {
            super(source);
            this.message = message;
        }
        /** */
        public MonaEvent(Object source, BbsThread thread, List data) {
            super(source);
            this.data = data;
            this.thread = thread;
        }
        /** */
        public List getBbsData() {
            return data;
        }
        /** */
        public BbsThread getBbsThread() {
            return thread;
        }
        /** */
        public String getMessage() {
            return message;
        }
    }

    /** */
    public interface MonaListener {
        void whenThreadUpdated(MonaEvent event);
        void whenThreadEnd(MonaEvent event);
        void whenMessageUpdated(MonaEvent event);
        void debug(Throwable t);
        void debug(String message);
    }

    /** */
    private MonaListener monaListener;

    /** */
    public void addMonaListener(MonaListener monaListener) {
        this.monaListener = monaListener;
    }

    /** */
    private BbsThread targetThread;

    /** */
    public void setTargetThread(BbsThread targetThread) {
        this.targetThread = targetThread;
System.err.println("THREAD: current is " + targetThread);
    }

    /** */
    private BbsDataFactory bbsDataFactory = new MyBbsDataFactory();

    /** */
    private class MyTimerTask extends TimerTask {
        int lastIndex = 0;
        public void run() {
            try {
                List data = bbsDataFactory.readFrom(targetThread);
                List responses = new ArrayList();
                for (int i = 0; i < data.size(); i++) {
                    BbsData datum = (BbsData) data.get(i);
                    if (datum.getIndex() > lastIndex) {
                        responses.add(datum);
                        lastIndex = datum.getIndex();
                    }
                }
                monaListener.whenThreadUpdated(new MonaEvent(this, targetThread, responses));
//System.err.println("LIVE: index: " + targetThread.index);
                if (targetThread.getIndex() > 1001) {
                    timer.cancel();
                    timer = null;
                    monaListener.whenThreadEnd(new MonaEvent(this, "stopped by end"));
System.err.println("LIVE: stopped by end");
                }
            } catch (Exception e) {
monaListener.whenMessageUpdated(new MonaEvent(this, "2: " + e));
monaListener.debug(e);
e.printStackTrace(System.err);
                timer.cancel();
                timer = null;
                monaListener.whenThreadEnd(new MonaEvent(this, "stopped by error"));
System.err.println("LIVE: stopped by error");
            }
        }
    };

    private Timer timer;

    /** */
    public void startLive() {
        if (targetThread == null) {
            throw new IllegalStateException("targetBoard has not set");
        }

        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, interval);
System.err.println("LIVE: started");
    }

    /** */
    public boolean isLiveStopped() {
        return timer == null;
    }

    /** */
    public void stopLive() {
        if (timer == null) {
            throw new IllegalStateException("not started");
        }

        timer.cancel();
        monaListener.whenThreadEnd(new MonaEvent(this, "stopped by cancel"));
System.err.println("LIVE: stopped by cancel");
    }
}

/* */
