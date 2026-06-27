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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel;
import java.util.Random;

import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;
import javax.swing.plaf.metal.MetalBorders.InternalFrameBorder;
import static java.awt.RenderingHints.*;
import static java.lang.Math.*;
import static java.text.MessageFormat.format;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class JChrono extends JComponent {

    Font majorNumbersFont = new Font("Courier New", Font.BOLD,           26);
    Font minorNumbersFont = new Font("Courier New", Font.ROMAN_BASELINE, 16);
    Font secsNumbersFont  = new Font("Courier New", Font.BOLD,           20);
    double Tick200ms = 0.98,
           Tick1s    = 0.96,
           Tick5s    = 0.94,
           Tick10s   = 0.92;

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
                KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        Insets insets = getInsets();
        int width = getWidth(), height = getHeight();

        width -= insets.left + insets.right;
        height -= insets.top + insets.bottom;
        if (isOpaque()) {
            g2d.clearRect( insets.left, insets.top, width, height);
        }

        double dimension = Math.min(width, height)/2.0;
        AffineTransform map = g2d.getTransform();
        g2d.translate(insets.left + width/2, insets.top + height/2);

        /* Draw the ticks */
        for (int i = 0; i < 300; ++i) {
            double angle  = i * PI / 150.0;
            double x0     =  dimension * sin(angle),
                   y0     = -dimension * cos(angle);
            double factor = (i % 50 == 0
                    ? Tick10s
                    : (i % 25 == 0
                        ? Tick5s
                        : (i % 5 == 0
                            ? Tick1s
                            : Tick200ms)));
            double x1     = x0 * factor,
                   y1     = y0 * factor;

            Shape line = new Line2D.Double(x0, y0, x1, y1);

            g2d.draw(line);

            if (i % 25 == 0) {
                /* draw the mod 5s number */
                double pos_secs = 0.85;
                double x_center = x0, y_center = y0;

                g2d.setFont(secsNumbersFont);
                String text = format("{0}", i/5);
                FontRenderContext frc = g2d.getFontRenderContext();
                TextLayout tl = new TextLayout(
                        text,
                        secsNumbersFont,
                        frc);
                Rectangle2D bounds = tl.getBounds();
                g2d.drawString(
                        text,
                        (float)(pos_secs * x_center - bounds.getCenterX()),
                        (float)(pos_secs * y_center - bounds.getCenterY()));

            }

        }
        for (int h = 0; h < 24; ++h) {
            double angle = h * PI / 12;
            double x0 =  dimension * sin( angle ),
                   y0 = -dimension * cos( angle );
            double x_center = x0, y_center = y0;
            double pos_hour = 0.65;
            String text = format( "{0}", h );
            Font which_font = (h % 3 == 0
                    ? majorNumbersFont
                    : minorNumbersFont);
            g2d.setFont( which_font );
            Rectangle2D bounds = new TextLayout(
                    text, which_font, g2d.getFontRenderContext())
                    .getBounds();
            g2d.drawString(
                    text,
                    (float) (pos_hour * x_center - bounds.getCenterX()),
                    (float) (pos_hour * y_center - bounds.getCenterY()) );
        }

        g2d.setTransform( map );
    }

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font f : fonts) {
            System.out.println( f.getName() );
        }
        Random rnd = new Random();
        EventQueue.invokeLater( () -> {
            JFrame frame = new JFrame( "Example" );
            JComponent contentPane = (JComponent) frame.getContentPane();
            contentPane.setLayout(new BorderLayout(10, 10));
            JChrono crono = new JChrono();
            crono.setBorder( new SoftBevelBorder(SoftBevelBorder.RAISED));
            crono.setPreferredSize( new Dimension(500, 500));
            contentPane.add(crono, BorderLayout.CENTER);
            contentPane.add(
                    new JLabel("Se está quemando la serreríaaa!!!",JLabel.CENTER),
                    BorderLayout.SOUTH);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });

    }

}
