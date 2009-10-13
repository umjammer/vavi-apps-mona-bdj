/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.mona;

import vavi.util.bdj.Util;

import junit.framework.TestCase;


/**
 * UtilTest. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 080902 nsano initial version <br>
 */
public class UtilTest extends TestCase {

    public void test01() throws Exception {
        String a = "xxxx<a href=\"../test/read.cgi/livecx/1220339513/204\" target=\"_blank\">&gt;&gt;204</a>yyyy";
        String b = Util.toPlainText(a);
        assertEquals(b, "xxxx>>204yyyy");
    }

    public void test02() throws Exception {
        String a = "186 �F��(߃n�)�� ��/CXNAMAz8. �F2008/09/02(��) 18:24:44.95 ID:VJLyi6dX <br> <a href=\"../test/read.cgi/livecx/1220346892/172\" target=\"_blank\">&gt;&gt;172</a>  <br> �Ή��ӂ肩  <br>  <br>  <br> 187 �F�������ł����Ƃ��I�F2008/09/02(��) 18:24:49.04 ID:x+mcVm8S <br> <a href=\"../test/read.cgi/livecx/1220346892/172\" target=\"_blank\">&gt;&gt;172</a>  <br> ���܂��͐Ή�  <br>  ";
        String b = Util.toPlainText(a);
        assertEquals(b, "186 �F��(߃n�)�� ��/CXNAMAz8. �F2008/09/02(��) 18:24:44.95 ID:VJLyi6dX \n >>172  \n �Ή��ӂ肩  \n  \n  \n 187 �F�������ł����Ƃ��I�F2008/09/02(��) 18:24:49.04 ID:x+mcVm8S \n >>172  \n ���܂��͐Ή�  \n  ");
    }
}

/* */
