/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.mona;

import vavi.util.bdj.Util;


/**
 * HtmlUtil.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/01/22 nsano initial version <br>
 */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    /** */
    public static String toPlainText(String text) {
//System.err.println("++++++++++++++++ text: " + text.length() + " ++++++++++++++++");
//System.err.println("TEXT: " + text);
        text = Util.replace(text, "<br>", "\n");
        int p;
        int q = 0;
        while ((p = text.indexOf("<a href=", q)) != -1) {
            q = text.indexOf(">", p + 8);
            if (q != -1) {
//System.err.println("★ 1: " + text.substring(0, p) + "\n★ 2: " + text.substring(q + 1));
                text = text.substring(0, p) + text.substring(q + 1);
                q++;
            }
        }
        text = Util.replace(text, "</a>", "");
        text = Util.replace(text, "&gt;", ">");
        text = Util.replace(text, "&lt;", "<");
        text = Util.replace(text, "&nbsp;", " ");
        return text;
    }
}

/* */
