/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.mona;

import java.io.IOException;
import java.util.List;


/**
 * BbsDatumFactory.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 081221 nsano initial version <br>
 */
public interface BbsDatumFactory {

    /**
     * @param thread lastModified, size
     */
    List readFrom(BbsThread thread) throws IOException;
}

/* */
