package Main;

import Util.AppTheme;
import javax.swing.SwingUtilities;

public final class AppMain {
	private AppMain() {}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			AppTheme.setupTheme(AppTheme.ThemeMode.FLAT_CARBON);

			GameBuilder builder = new GameBuilder();
			builder.launch();
		});
	}
}