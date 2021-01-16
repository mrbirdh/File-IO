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
		super(owner, "진행상황", true);
		this.owner = owner;
		this.target = this.owner.getTarget();
		this.copyFile = this.owner.getCopyFile();
		this.bufferSize = owner.getBufferSize(); //이거!
		
		this.pBar = new JProgressBar(JProgressBar.HORIZONTAL,1,100);
		
		this.pBar.setPreferredSize(new Dimension(300,20));
		this.pBar.setStringPainted(true);
		JPanel pnl = new JPanel();
		pnl.add(this.pBar);
		this.add(pnl);
		
		// 파일 복사 Thread
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
							// 진행 중 중지되면 예외발생
							throw new Exception();
						}
						fos.write(buffer,0,count);
						fos.flush();
						// 진행 상황 계산
						double calc =
								(double)copyFile.length()
								/(double)target.length();
						setPBarValue((int)(calc*100));
					}
					term = System.currentTimeMillis()-term;
					
					dispose();
					ProcessDlg.this.owner.setResult(term);
				} catch(Exception e) {
					// 무시
				} finally {
					closeAll(fos,fis);
					// 작업 취소되면 작업중인 파일 삭제
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
						ProcessDlg.this, "복사 취소됨",
						"알림", JOptionPane.INFORMATION_MESSAGE
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
	 * 	프로그레스바 설정
	 */
	public void setPBarValue(int value){
		this.pBar.setValue(value);
	}
}




















