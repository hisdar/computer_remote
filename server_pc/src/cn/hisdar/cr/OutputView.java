package cn.hisdar.cr;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.log.HLogInterface;
import cn.hisdar.lib.ui.TitlePanel;
import cn.hisdar.lib.ui.output.HOutputArea;

public class OutputView extends JPanel implements HLogInterface {

	private TitlePanel titlePanel = null;
	private HOutputArea outputArea = null;
	
	public OutputView() {
		setLayout(new BorderLayout());
		
		titlePanel = new TitlePanel("Êä³ö");
		outputArea = new HOutputArea();
		outputArea.setMaxLineCount(500);
		
		add(titlePanel, BorderLayout.NORTH);
		add(outputArea, BorderLayout.CENTER);
		
		HLog.addHLogInterface(this);
	}

	@Override
	public void info(String log) {
		outputArea.output(log);
	}

	@Override
	public void error(String log) {
		outputArea.output(log);
	}

	@Override
	public void debug(String log) {
	}
}
