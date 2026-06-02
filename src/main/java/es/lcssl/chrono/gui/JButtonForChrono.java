/* JButtonForChrono.java -- JButton para un cronometro.
 * Author: Luis Colorado <luiscoloradourcola@gmail.com>
 * Date: Tue Jun  2 09:43:13 -05 2026
 * Copyright: (c) 2026 Luis Colorado.  All rights reserved.
 * License: BSD
 */

package es.lcssl.chrono.gui;

import javax.swing.JButton;
import javax.swing.Action;

public class JButtonForChrono extends JButton {

		long buttonPressTimeStamp;

		public JButtonForChrono(Action action) {
			super(action);
			addMouseListener(Button1Press, e -> {
				buttonPressTimeStamp = e.getWhen();
			});
		}

		long getButtonPressTimestamp() {
			return buttonPressTimestamp;
		}
}
