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

import java.awt.BasicStroke;
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
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Date;
import java.awt.font.TextLayout;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_BEVEL;
import static java.lang.Math.*;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Luis Colorado {@code <luiscoloradourcola@gmail.com>}
 */
public class JChrono extends JComponent {

    Font majorHoursFont = new Font("URW Bookman L", Font.BOLD,             24);
    Font minorHoursFont = new Font("URW Bookman L", Font.ITALIC,           18);
    Font minutesFont    = new Font("URW Bookman L", Font.ROMAN_BASELINE,   22);
    
    String[] hourNames = {
        "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10", 
        "11", "12", "13", "14", "15",
        "16", "17", "18", "19", "20",
        "21", "22", "23",
    };
    
    String[] minNames = {
        "0", "5", "10", "15", "20", "25",
        "30", "35", "40", "45", "50", "55",
    };
    
    double  tickStart       =  0.989, /* tick radial start positon (1.0 for all) */
            tick200msEnd    =  0.980,
            tick1sEnd       =  0.960,
            tick5sEnd       =  0.940,
            tick10sEnd      =  0.900,
            posSecsLbl      =  0.805, /* radial position of Secs label */
            tick1hStart     =  0.710,
            tick1hEnd       =  0.690,
            posHourLbl      =  0.600,
            smallCircRadius =  0.030,
            tick200msWidth  =  0.005,
            tick1sWidth     =  0.008,
            tick5sWidth     =  0.016,
            tick10sWidth    =  0.020,
            tick1hWidth     =  0.010,
            hourTail        = -0.050,
            minTail         = -0.100,
            secTail         = -0.150,
            hourHead        =  0.500,
            minHead         =  0.800,
            secHead         =  0.850,
            hourHandWidth   =  0.030,
            minHandWidth    =  0.020,
            secHandWidth    =  0.008;
    
    Color   tick200msColor  = getForeground(),
            tick1sColor     = getForeground(),
            tick5sColor     = getForeground(),
            tick10sColor    = getForeground(),
            tick1hColor     = getForeground(),
            lbl5sColor      = getForeground(),
            lbl1hMajorColor = getForeground(),
            lbl1hMinorColor = getForeground(),
            hourHandColor   = getForeground(),
            minuteHandColor = getForeground(),
            secondHandColor = getForeground();
            
    
    public static final int
                TICKS_200MS = 0,
                TICKS_1S    = 1,
                TICKS_5S    = 2,
                TICKS_10S   = 3,
                TICKS_1H    = 4;
    
    private GeneralPath[] theTicks;
    private BasicStroke[] theStrokes;
    private GeneralPath hourHand        = new GeneralPath(),
                        minuteHand      = new GeneralPath(),
                        secondHand      = new GeneralPath();
    
    public JChrono () {
        GeneralPath[] the_ticks = new GeneralPath [] {
            new GeneralPath(), /* 200ms ticks */
            new GeneralPath(), /*   1s  ticks */
            new GeneralPath(), /*   5s  ticks */
            new GeneralPath(), /*  10s  ticks */
            new GeneralPath(), /*   1h  ticks */
        };
        theTicks = the_ticks;
        double ticks_end[] = {
            tick200msEnd,
            tick1sEnd,
            tick5sEnd,
            tick10sEnd,
        };
        
        final int TOTAL = 300;
        for (int i = 0; i < TOTAL; ++i) {
            int which =
                      (i % 50 == 0)
                        ? TICKS_10S
                    : (i % 25 == 0)
                        ? TICKS_5S
                    : (i % 5 == 0)
                        ? TICKS_1S
                    : TICKS_200MS;
            
            double angle = i * 2.0 * PI / TOTAL;
            double sin_angle = sin(angle),
                   cos_angle = cos(angle);
            the_ticks[which].moveTo(
                    tickStart * sin_angle,
                    tickStart * cos_angle);
            the_ticks[which].lineTo(
                    ticks_end[which] * sin_angle,
                    ticks_end[which] * cos_angle);
        }
        final int HOURS = 24;
        for (int i = 0; i < HOURS; ++i) {
            double angle = i * 2.0 * PI / HOURS;
            double sin_angle = sin(angle),
                   cos_angle = cos(angle);
            the_ticks[TICKS_1H].moveTo(
                    tick1hStart * sin_angle,
                    tick1hStart * cos_angle );
            the_ticks[TICKS_1H].lineTo(
                    tick1hEnd * sin_angle,
                    tick1hEnd * cos_angle);
        }
        hourHand   = new GeneralPath();
        minuteHand = new GeneralPath();
        secondHand = new GeneralPath();
        hourHand  .moveTo(0.0, hourTail);
        hourHand  .lineTo(0.0, 0.0);
        hourHand  .lineTo(0.0, hourHead);
        minuteHand.moveTo(0.0, minTail);
        minuteHand.lineTo(0.0, minHead);
        secondHand.moveTo(0.0, secTail);
        secondHand.lineTo(0.0, secHead);
        
        timer.start();
    }

    Timer timer = new Timer(47,
            e -> { invalidate(); repaint(); } );

    private void drawTicks(
            Graphics2D g,
            Shape ticks,
            int cap,
            double radius,
            double width,
            Color color,
            AffineTransform transf)
    {
            g.setStroke(new BasicStroke(
                    (float) (width * radius), cap, JOIN_BEVEL ) );
            g.setColor(color);
            g.draw( transf.createTransformedShape( ticks ) );
            g.setColor(getForeground());
    }
    
    private void drawHand(
            Graphics2D g, Shape path, AffineTransform scale, double angle, float l_w, int cap, Color color)
    {
        AffineTransform scale_n_rotate = (AffineTransform) scale.clone();
        scale_n_rotate.concatenate(AffineTransform.getRotateInstance(-angle ) );
        g.setStroke( new BasicStroke(l_w, cap, JOIN_BEVEL) );
        g.setColor(color);
        g.draw( scale_n_rotate.createTransformedShape( path ) );
    }
    

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        /* get a new graphics2D so we can mangle it */
        g2d = (Graphics2D) g2d.create();
        

        g2d.setRenderingHint(
                KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        

        /* we don't paint on the insets zone */
        Insets insets = getInsets();
        int width = getWidth(), height = getHeight();

        width  -= insets.left + insets.right;
        height -= insets.top  + insets.bottom;

        
        double radius = Math.min(width, height)/2.0;
        g2d.translate(insets.left + width/2, insets.top + height/2);

        /* Draw the ticks */
 		/* 200ms ticks */
        AffineTransform scale = AffineTransform.getScaleInstance( radius, -radius );

        drawTicks(g2d, theTicks[TICKS_200MS], CAP_ROUND,
                radius, tick200msWidth, tick200msColor, scale );
        drawTicks(g2d, theTicks[TICKS_1S],    CAP_ROUND,
                radius, tick1sWidth, tick1sColor,    scale );
        drawTicks(g2d, theTicks[TICKS_5S],    CAP_SQUARE,
                radius, tick5sWidth, tick5sColor,    scale );
        drawTicks(g2d, theTicks[TICKS_10S],   CAP_SQUARE,
                radius, tick10sWidth, tick10sColor,   scale );
        drawTicks(g2d, theTicks[TICKS_1H],    CAP_SQUARE,
                radius, tick1hWidth, tick1hColor,    scale );
        g2d.fill( scale.createTransformedShape( new Ellipse2D.Double(
                -smallCircRadius, -smallCircRadius,
                2 * smallCircRadius, 2 * smallCircRadius ) ) );
        /* the figures */
        FontRenderContext frc = g2d.getFontRenderContext();
        GeneralPath       gp  = new GeneralPath();
        for (int i = 0; i < 24; ++i) {
            Font hour_font = i % 3 == 0
                    ? majorHoursFont
                    : minorHoursFont;
            Color hour_color = i % 3 == 0
                    ? lbl1hMajorColor
                    : lbl1hMinorColor;
            double      angle     = 2.0 * PI * i / 24;
            double      sin_angle = sin(angle),
                        cos_angle = cos(angle);
 
            TextLayout  tl        = new TextLayout(hourNames[i], hour_font, frc);
            Rectangle2D b         = tl.getBounds();
            double      factor    = posHourLbl * radius;
            double      x0        =  sin_angle*factor - b.getCenterX();
            double      y0        = -cos_angle*factor - b.getCenterY();
            g2d.setFont(hour_font);
            g2d.setColor(hour_color);
            g2d.drawString(hourNames[i], (float)x0, (float)y0);
            g2d.setColor(getForeground());

            if (i % 2 == 0) {
                tl = new TextLayout(minNames[i / 2], minutesFont, frc);
                b = tl.getBounds();
                factor = posSecsLbl * radius;
                x0 = sin_angle * factor - b.getCenterX();
                y0 = -cos_angle * factor - b.getCenterY();
                g2d.setFont(minutesFont);
                g2d.setColor(lbl5sColor);
                g2d.drawString(minNames[i / 2], (float) x0, (float) y0);
                g2d.setColor(getForeground());
            }
        }
        long timestamp = System.currentTimeMillis();
        timestamp -= new Date( timestamp ).getTimezoneOffset() * 60_000;
        drawHand(g2d, hourHand, scale,
                timestamp % 86_400_000L * PI / 43.2E6,
                (float) (hourHandWidth * radius), CAP_ROUND, hourHandColor );
        drawHand(g2d, minuteHand, scale,
                timestamp % 3_600_000L * PI / 1.8E6,
                (float) (minHandWidth * radius), CAP_SQUARE, minuteHandColor );
        drawHand(g2d, secondHand, scale,
                timestamp % 60_000L * PI / 30.0E3,
                (float) (secHandWidth * radius), CAP_SQUARE, secondHandColor );
    }

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font f : fonts) {
            System.out.println( f.getName() );
        }
        EventQueue.invokeLater( () -> {
            JFrame frame = new JFrame( "Example" );
            JComponent contentPane = (JComponent) frame.getContentPane();
            contentPane.setLayout(new BorderLayout(10, 10));
            JChrono crono = new JChrono();
            crono.hourHandColor   = Color.ORANGE;
            crono.minuteHandColor = Color.BLUE;
            crono.secondHandColor = Color.GREEN;
            crono.tick200msColor  = Color.RED;
            crono.setBorder( new TitledBorder(new LineBorder(Color.blue)/* , "Hola como estas?"*/));
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
