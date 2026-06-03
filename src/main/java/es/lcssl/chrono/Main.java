/*
 * Copyright (c) 2026, Luis Colorado <luiscoloradourcola@gmail.com>.
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
package es.lcssl.chrono;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import es.lcssl.chrono.gui.JChronograph;
import es.lcssl.chrono.gui.ChronographModel;
import es.lcssl.chrono.gui.DefaultChronographModel;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class Main {

    public static final void main(String[] args) {
        if (args.length == 0) {
            System.err.println(
                    "Must specify parameters to name the chronographs.");
        }
        EventQueue.invokeLater(() -> {
            for (String title : args) {
                JFrame frame = new JFrame("Chronograph V1.0 - " + title);
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                JChronograph crono = new JChronograph();
                crono.getModel().setName(title);
                frame.setContentPane(crono);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
