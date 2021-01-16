package kr.ac.green;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProcessDlg extends JDialog{
	private JProgressBar pBar;
	private CopyFile owner;
	private File target;
	private File copyFile;
	private int bufferSize;
	private boolean isStop;
	public ProcessDlg(CopyFile owner){
		super(owner, "�����Ȳ", true);
		this.owner = owner;
		this.target = this.owner.getTarget();
		this.copyFile = this.owner.getCopyFile();
		this.bufferSize = owner.getBufferSize(); //�̰�!
		
		this.pBar = new JProgressBar(JProgressBar.HORIZONTAL,1,100);
		
		this.pBar.setPreferredSize(new Dimension(300,20));
		this.pBar.setStringPainted(true);
		JPanel pnl = new JPanel();
		pnl.add(this.pBar);
		this.add(pnl);
		
		// ���� ���� Thread
		new Thread(){
			int i = 0;
			@Override
			public void run(){
				FileInputStream fis = null;
				FileOutputStream fos = null;
				try{
					fis = new FileInputStream(target);
					fos = new FileOutputStream(copyFile);
					
					byte[] buffer = new byte[bufferSize];
					int count = -1;
					
					long term = System.currentTimeMillis();
					while((count=fis.read(buffer)) != -1){
						if(isStop){
							// ���� �� �����Ǹ� ���ܹ߻�
							throw new Exception();
						}
						fos.write(buffer,0,count);
						fos.flush();
						// ���� ��Ȳ ���
						double calc =
								(double)copyFile.length()
								/(double)target.length();
						setPBarValue((int)(calc*100));
					}
					term = System.currentTimeMillis()-term;
					
					dispose();
					ProcessDlg.this.owner.setResult(term);
				} catch(Exception e) {
					// ����
				} finally {
					closeAll(fos,fis);
					// �۾� ��ҵǸ� �۾����� ���� ����
					if(isStop){
						copyFile.delete();
					}
				}
			}
			private void closeAll(Closeable...c){
				for(Closeable temp : c){
					try{
						if(temp != null) temp.close();
					}catch(Exception e){}
				}
			}
		}.start();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we){
				isStop = true;
				JOptionPane.showMessageDialog(
						ProcessDlg.this, "���� ��ҵ�",
						"�˸�", JOptionPane.INFORMATION_MESSAGE
				);
				dispose();
				ProcessDlg.this.owner.setCopyEnabled(true);
				ProcessDlg.this.owner.setResult(-1);
			}
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setLocation(300,300);
		this.setVisible(true);
	}
	/*
	 * 	���α׷����� ����
	 */
	public void setPBarValue(int value){
		this.pBar.setValue(value);
	}
}




















