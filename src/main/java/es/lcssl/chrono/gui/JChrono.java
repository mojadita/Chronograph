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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JFrame;
import static java.awt.RenderingHints.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import javax.swing.JLabel;
import static java.lang.Math.*;
import static java.text.MessageFormat.format;
import java.util.Random;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class JChrono extends JComponent {
    
    Font majorNumbersFont = new Font("Century Schoolbook L Bold", Font.BOLD, 32);
    Font minorNumbersFont = new Font("Century Schoolbook L Italic", Font.ITALIC, 24);
    Font secsNumbersFont = new Font("CMU Typewriter Text", Font.BOLD, 14);
    double halfTick = 0.003;
    double secTick  = 0.006;
    double hourTick = 0.009;

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
                KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        
        double dimension = Math.min(getWidth(), getHeight())/2.0;
        g2d.translate(getWidth()/2, getHeight()/2);

        /* Draw the ticks */
        for (int i = 0; i < 300; ++i) {
            double angle  = i * PI / 150.0;
            double x0     =  dimension * sin(angle),
                   y0     = -dimension * cos(angle);
            double factor = (i % 50 == 0
                    ? 0.92
                    : (i % 25 == 0
                        ? 0.94
                        : (i % 5 == 0
                            ? 0.96
                            : 0.98)));
            double x1     = x0*factor,
                   y1     = y0*factor;
            
            Shape line = new Line2D.Double(x0, y0, x1, y1);
            g2d.draw(line);
            
            if (i % 25 == 0) {
                /* draw the number */
                double pos_hour = 0.70;
                double pos_secs = 0.85;
                double x_center = x0, y_center = y0;
                
                String text = format("{0}", (i / 25 + 11) % 12 + 1);
                Font which_font = (i % 75 == 0 
                        ? majorNumbersFont
                        : minorNumbersFont);
                g2d.setFont(which_font);
                FontRenderContext frc = g2d.getFontRenderContext();
                TextLayout tl = new TextLayout(
                        text, 
                        which_font,
                        frc);
                Rectangle2D bounds = tl.getBounds();
                g2d.drawString(
                        text,
                        (float) (pos_hour * x_center - bounds.getCenterX()),
                        (float) (pos_hour * y_center - bounds.getCenterY()));
                g2d.setFont(secsNumbersFont);
                text = format("{0}", i/5);
                frc = g2d.getFontRenderContext();
                tl = new TextLayout(
                        text,
                        secsNumbersFont,
                        frc);
                bounds = tl.getBounds();
                g2d.drawString(
                        text, 
                        (float)(pos_secs * x_center - bounds.getCenterX()),
                        (float)(pos_secs * y_center - bounds.getCenterY()));
                
            }
            
        }        
        
    }
    
    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font f: fonts) {
            System.out.println(f.getName());
        }
        Random rnd = new Random();
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Example");
            JComponent contentPane = (JComponent) frame.getContentPane();
            contentPane.setLayout(new BorderLayout(10, 10));
            JChrono crono = new JChrono();
            //crono.majorNumbersFont = fonts[rnd.nextInt(fonts.length)];
            //crono.minorNumbersFont = fonts[rnd.nextInt(fonts.length)];
            contentPane.add(new JChrono(), BorderLayout.CENTER);
            contentPane.add(
                    new JLabel("Se está quemando la serreríaaa!!!",JLabel.CENTER),
                    BorderLayout.SOUTH);
            frame.setSize(640, 480);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
    
}
