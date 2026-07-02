/*
 * Copyright (c) 2026, Luis Colorado {@code <luiscoloradourcola@gmail.com>}.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.lcssl.chrono.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
import org.junit.jupiter.api.BeforeEach;
public class TestDefaultChronographModel {
    static Logger LOG = LogManager.getLogger(TestDefaultChronographModel.class);

    static final String UUT_NAME = "test";
    static final long UUT_RESET_VALUE = 253_458L;
    static final long UUT_TIME_AT_RESET = 260_328L;


    DefaultChronographModel uut;

    //@BeforeEach
    public void instantiate() {
        uut = new DefaultChronographModel(UUT_NAME);
    }

    //@Test
    public void testNameOk() {
        assertThat(uut.getName(), equalTo(UUT_NAME));
    }

    //@Test
    public void testResetSetsStartTimeToUUT_RESET_VALUE() {
        uut.reset(UUT_RESET_VALUE);
        assertThat(uut.getStartTime(),
                equalTo(UUT_RESET_VALUE));
    }

    //@Test
    public void testResetSetsTotalAndLapseToZero() {
        uut.reset(UUT_RESET_VALUE);
        assertThat(uut.getIntervals(UUT_RESET_VALUE),
                equalTo(new long[] {0L, 0L}));
    }

    //@Test
    public void testResetFiresPropertyChangeEvent() {
        //fail();
    }

}
