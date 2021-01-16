package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

public class CopyFile extends JFrame implements ActionListener{
	private Dimension BTN_SIZE = new Dimension(70,23);
	
	private JTextField tfTarget;
	private JTextField tfBufferSize;
	private JButton btnSelect;
	private JButton btnCopy;
	private JTextArea taResult;
	
	private File target;
	private int bufferSize;
	private File copyFile;
	private JFileChooser chooser;
	
	public CopyFile(){
		super("File copy");
		this.init();
		this.setLayout();
		this.addListener();
		this.showFrame();
	}
	private void init(){
		this.tfTarget = new JTextField(20);
		this.tfTarget.setEditable(false);
		
		this.tfBufferSize = new JTextField(27);
		this.btnSelect = new JButton("선택");
		this.btnSelect.setPreferredSize(BTN_SIZE);
		this.btnCopy = new JButton("복사");
		this.btnCopy.setPreferredSize(BTN_SIZE);
		this.taResult = new JTextArea(10,25);
		this.taResult.setEditable(false);
		
		this.chooser = new JFileChooser("c:/");
	}
	private void setLayout(){
		JPanel pnlCenter = new JPanel(new GridLayout(0,1));
		JPanel pnlSouth = new JPanel();
		
		JPanel pnlTarget = new JPanel();
		pnlTarget.add(this.tfTarget);
		pnlTarget.add(this.btnSelect);
		setBorder(pnlTarget,"Target");
		
		JPanel pnlBufferSize = new JPanel();
		pnlBufferSize.add(this.tfBufferSize);
		setBorder(pnlBufferSize, "BufferSize");
		
		JPanel pnlCopy = new JPanel();
		pnlCopy.add(this.btnCopy);
		
		pnlCenter.add(pnlTarget);
		pnlCenter.add(pnlBufferSize);
		pnlCenter.add(pnlCopy);
		
		JScrollPane pane = new JScrollPane(this.taResult);
		pnlSouth.add(pane);
		setBorder(pnlSouth, "Result");
		
		this.add(pnlCenter,BorderLayout.CENTER);
		this.add(pnlSouth,BorderLayout.SOUTH);
	}
	private void showFrame(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		this.setLocation(200,200);
		this.setVisible(true);
	}
	
	private void addListener(){
		this.btnSelect.addActionListener(this);
		this.btnCopy.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae){
		Object o = ae.getSource();
		if(o == this.btnSelect){
			
			int choice = chooser.showOpenDialog(this);
			if(choice == JFileChooser.APPROVE_OPTION){
				this.target = chooser.getSelectedFile();
				this.tfTarget.setText(this.target.getPath());
			}
			// btnCopy 일 경우, 
		}else{
			// length = 0 일 경우, 
			if(this.hasContents(tfTarget)){
				if(this.hasContents(this.tfBufferSize)){
					
					String temp = this.tfBufferSize.getText();
					this.bufferSize = Integer.parseInt(temp);
					this.fileCopy();
				}
			}
		}
	}
	/*
	 * 	보더 설정
	 */
	public static void setBorder(JComponent comp, String title){
		comp.setBorder(new TitledBorder(
				new EtchedBorder(),title)
		);
	}
	/*
	 * 	복사할 대상 및 복사본 설정
	 */
	public void fileCopy(){
		String tempName = this.target.getPath();
		int idx = tempName.lastIndexOf(".");
		// 파일이름
		String name = tempName.substring(0,idx);
		// 파일 확장자
		String ext = tempName.substring(idx);
		// 초기값
		boolean flag = true;
		int num = 0;
		
		do {
			this.copyFile =
					new File(name+"_copy["+num+"]"+ext);
			if(!copyFile.exists()){
				flag = false;
			}
		}while(flag);
		this.setCopyEnabled(false);
		new ProcessDlg(this);
	}
	/*
	 * 	결과창 셋팅
	 */
	public void setResult(long term){
		if(term < 0){
			this.taResult.append("---- 작업취소 ---\n\n");
		}else{
			JOptionPane.showMessageDialog(
					this, "완료", "알림", 
					JOptionPane.INFORMATION_MESSAGE
			);
			this.setCopyEnabled(true);
			this.taResult.append(
					"---- 연산결과 ---\n\n"+
					"생성된 파일 : "+
					this.copyFile.getName()+
					"\n파일크기 : "+
					this.copyFile.length()+
					"byte\n"+
					"버퍼사이즈 : "+
					this.tfBufferSize.getText()+
					"byte\n"+
					"\n소요시간 : "+
					term+"ms\n\n"
			);
			this.taResult.setCaretPosition(
					this.taResult.getText().length()
			);
			this.tfBufferSize.selectAll();
			this.tfBufferSize.requestFocus();
		}
	}
	/*
	 * 	동작여부 설정
	 */
	public void setCopyEnabled(boolean flag){
		this.btnCopy.setEnabled(flag);
		this.btnSelect.setEnabled(flag);
	}
	/*
	 * 	공백 체크
	 */
	public boolean hasContents(JTextComponent comp){
		String str = comp.getText();
		// 뭐라도 있으면 괜찮지만,
		if(str.length() > 0){
			return true;
		// 텅 비었을때
		}else{
			// 포커스를 어디에 두냐.
			JComponent focusTo = this.btnSelect;
			String msg = "복사할 대상 선택하셈";
			if(comp == this.tfBufferSize){
				msg = "버퍼사이즈 입력하셈";
				focusTo = comp;
			}
			JOptionPane.showMessageDialog(
					this, msg,"알림",
					JOptionPane.INFORMATION_MESSAGE
			);
			focusTo.requestFocus();
			return false;
		}
	}
	/*
	 * 	getters
	 */
	public File getTarget() {
		return target;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public File getCopyFile() {
		return copyFile;
	}
	
	public static void main(String[] args) {
		new CopyFile();
	}
	
}

