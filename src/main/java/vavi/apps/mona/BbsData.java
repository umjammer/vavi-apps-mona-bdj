/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.mona;

import vavi.util.bdj.Util;


/**
 * BbsData. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 080901 nsano initial version <br>
 */
public class BbsData {

    /** �u���X�v�ԍ� */
    private int index;
    /** ���O */
    private String name;
    /** ���[�� */
    private String email;
    /** ���t ID BE-ID */
//    private Date date;
    private String id;
//    private String beid;
    /** �{�� */
    private String text;
    /** �X���b�h�^�C�g�� */
    private String title;

    /** */
    public BbsData(int index, String name, String email, String id, String text, String title) {
        this.index = index;
        this.name = name;
        this.email = email;
        this.id = id;
        this.text = text;
        this.title = title;
//System.err.println(this);
    }

    public int getIndex() {
        return index;
    }

    public String getTextAsPlainText() {
        return Util.toPlainText(text);
    }

    public String getEmailAsFormated() {
        if ("sage".equals(email)) {
            return "��";
        } else {
            return email;
        }
    }

    /* */
    public String toString() {
        return name + ", " + email + ", " + id + ", " + text + ", " + title;
    }

    /** */
    public String toStringAsFormated() {
        return index + " :" + name + " [" + getEmailAsFormated() + "]" + id + "\n\n " + getTextAsPlainText();
    }

    /** */
    String raw;
}

/* */
