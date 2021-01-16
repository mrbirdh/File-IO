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
		this.btnSelect = new JButton("����");
		this.btnSelect.setPreferredSize(BTN_SIZE);
		this.btnCopy = new JButton("����");
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
			// btnCopy �� ���, 
		}else{
			// length = 0 �� ���, 
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
	 * 	���� ����
	 */
	public static void setBorder(JComponent comp, String title){
		comp.setBorder(new TitledBorder(
				new EtchedBorder(),title)
		);
	}
	/*
	 * 	������ ��� �� ���纻 ����
	 */
	public void fileCopy(){
		String tempName = this.target.getPath();
		int idx = tempName.lastIndexOf(".");
		// �����̸�
		String name = tempName.substring(0,idx);
		// ���� Ȯ����
		String ext = tempName.substring(idx);
		// �ʱⰪ
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
	 * 	���â ����
	 */
	public void setResult(long term){
		if(term < 0){
			this.taResult.append("---- �۾���� ---\n\n");
		}else{
			JOptionPane.showMessageDialog(
					this, "�Ϸ�", "�˸�", 
					JOptionPane.INFORMATION_MESSAGE
			);
			this.setCopyEnabled(true);
			this.taResult.append(
					"---- ������ ---\n\n"+
					"������ ���� : "+
					this.copyFile.getName()+
					"\n����ũ�� : "+
					this.copyFile.length()+
					"byte\n"+
					"���ۻ����� : "+
					this.tfBufferSize.getText()+
					"byte\n"+
					"\n�ҿ�ð� : "+
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
	 * 	���ۿ��� ����
	 */
	public void setCopyEnabled(boolean flag){
		this.btnCopy.setEnabled(flag);
		this.btnSelect.setEnabled(flag);
	}
	/*
	 * 	���� üũ
	 */
	public boolean hasContents(JTextComponent comp){
		String str = comp.getText();
		// ���� ������ ��������,
		if(str.length() > 0){
			return true;
		// �� �������
		}else{
			// ��Ŀ���� ��� �γ�.
			JComponent focusTo = this.btnSelect;
			String msg = "������ ��� �����ϼ�";
			if(comp == this.tfBufferSize){
				msg = "���ۻ����� �Է��ϼ�";
				focusTo = comp;
			}
			JOptionPane.showMessageDialog(
					this, msg,"�˸�",
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

