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

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import es.lcssl.chrono.gui.JChronograph;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main class to hold a main() method to execute this as an independent program.
 *
 * This class stays out from the {@link es.lcssl.chrono.gui} package, as it
 * will be evicted in the future making the Chronograph stuff a library module,
 * instead of an application.
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class Main {
    
    /**
     * private constructor so no instance of this class can be created.
     */
    private Main() {}

    /**
     * {@link ResourceBundle} instance to hold localization resources.
     */
    static final ResourceBundle INTL =
            ResourceBundle.getBundle(Main.class.getName());


    /**
     * Simple main method to display as many {@link JChronograph}s as needed to
     * satisfy the list of names passed through the {@code args} parameter list.
     *
     * @param args are the names of the {@link JChronograph}s that will be
     * created to show on the screen.
     */
    public static final void main(String[] args) {
        if (args.length == 0) {
            System.err.println(
                    INTL.getString("HELP_STRING"));
        }
        EventQueue.invokeLater(() -> {
            for (String title : args) {
                JFrame frame = new JFrame(INTL.getString(
                        "PROGRAM_NAME_AND_VERSION") + " - " + title);
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
