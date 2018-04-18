package com.mervin;

import com.mervin.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Mervin
 * @Description:
 * @date 2018-04-02 21:46
 */
public class MainFrame1 extends JFrame implements ActionListener {


    private JLabel srcLabel;
    private JTextField srcTextField;
    private JButton srcBtn;

    private JLabel desLabel;
    private JTextField descTextField;
    private JButton descBtn;

    private JLabel offWidthLabel;
    private JTextField offWidthTextField;

    private JLabel offHeightLabel;
    private JTextField offHeightTextField;




    private JButton sureBtn;

    private Image icon;

    public MainFrame1(){
        this.setTitle("订单合成小工具");
        FlowLayout layout = new FlowLayout();// 布局
        srcLabel = new JLabel("请选择文件夹（PDF订单文件所在目录）：");// 标签
        srcTextField = new JTextField(30);// 文本域
        srcTextField.setPreferredSize(new Dimension(400,30));
        srcBtn = new JButton("浏览");//
        srcBtn.setPreferredSize(new Dimension(70, 30));

        desLabel = new JLabel("请选择文件夹（合成订单图片存放目录）：");// 标签
        descTextField = new JTextField(30);// 文本域
        descTextField.setPreferredSize(new Dimension(400,30));
        descBtn = new JButton("浏览");//
        descBtn.setPreferredSize(new Dimension(70, 30));

        JLabel notice = new JLabel("订单信息间距调整：");

        offWidthLabel = new JLabel("横向间距:");
        offWidthTextField = new JTextField("25");
        offWidthTextField.setPreferredSize(new Dimension(70,30));

        offHeightLabel = new JLabel("竖向间距:");
        offHeightTextField = new JTextField("25");
        offHeightTextField.setPreferredSize(new Dimension(70,30));

        sureBtn = new JButton("开始转换");
        sureBtn.setPreferredSize(new Dimension(100, 40));
        icon = Toolkit.getDefaultToolkit().getImage(MainFrame1.class.getClassLoader().getResource("images/mdy.png"));  // 图片的具体位置


       // this.getImage(com.mervin.MainFrame.class.getClassLoader().getResource("images/tankL.gif")),
        //Image icon = Toolkit.getDefaultToolkit().getImage("\\mdy.png");  // 图片的具体位置

        this.setIconImage(icon);   //设置窗口的logo

        ImageIcon img = new ImageIcon(MainFrame1.class.getClassLoader().getResource("images/bg.jpg"));
        //要设置的背景图片
        JLabel imgLabel = new JLabel(img);
        //将背景图放在标签里。
        this.getLayeredPane().add(imgLabel, new Integer(Integer.MIN_VALUE));
        //将背景标签添加到jfram的LayeredPane面板里。
        imgLabel.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
        // 设置背景标签的位置
        Container contain = this.getContentPane();
        // 将内容面板设为透明。将LayeredPane面板中的背景显示出来。
        ((JPanel) contain).setOpaque(false);

        // 设置布局
        layout.setAlignment(FlowLayout.CENTER);// 居中对齐
        layout.setVgap(15);
        contain.setLayout(layout);

        // 选择文件部分添加到面板
        srcBtn.addActionListener(this);
        contain.add(srcLabel);
        contain.add(srcTextField);
        contain.add(srcBtn);

        // 选择文件部分添加到面板
        descBtn.addActionListener(this);
        contain.add(desLabel);
        contain.add(descTextField);
        contain.add(descBtn);

        contain.add(notice);

        contain.add(offWidthLabel);
        contain.add(offWidthTextField);
        contain.add(offHeightLabel);
        contain.add(offHeightTextField);

        sureBtn.addActionListener(this);
        contain.add(sureBtn);

       // UIManager.put("Button.font", new java.awt.Font("宋体", 0, 24));
        //UIManager.put("Label.font", new java.awt.Font("宋体", 0, 24));

        this.setBounds(700, 400, 455, 350);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
       new MainFrame1();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == srcBtn){
            openChooserFileDialog(srcTextField);
        }else if(e.getSource() == descBtn){
            openChooserFileDialog(descTextField);
        }else if(e.getSource() == sureBtn){
            String srcPath = srcTextField.getText();
            if(srcPath == null || "".equals(srcPath.trim())){
                JOptionPane.showMessageDialog(null, "请选择PDF订单文件夹!","温馨提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String descPath = descTextField.getText();
            if(descPath == null || "".equals(descPath.trim())){
                JOptionPane.showMessageDialog(null, "请选择订单图片存放路径!","温馨提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String offWidthStr = offWidthTextField.getText();
            if(!Utils.isNumeric(offWidthStr)){
                JOptionPane.showMessageDialog(null, "横向间距只能为整数!","温馨提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String offHeightStr = offHeightTextField.getText();
            if(!Utils.isNumeric(offHeightStr)){
                JOptionPane.showMessageDialog(null, "竖向间距只能为整数!","温馨提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            int offWidth = Integer.parseInt(offWidthStr);
            int offHeight = Integer.parseInt(offHeightStr);
            if(offHeight < 0 || offWidth < 0 || offWidth > 100 || offHeight > 100){
                JOptionPane.showMessageDialog(null, "间距只能在0-100之间!","温馨提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                sureBtn.setText("正在转换...");
                //sureBtn.setEnabled(false);
                sureBtn.removeActionListener(this);
                int result = PdfToJpgUtil.pdf2multiImage(srcPath, descPath,offWidth,offHeight);
                sureBtn.setText("开始转换");
                sureBtn.setEnabled(true);
                sureBtn.removeActionListener(this);
                if(result == 0){
                    JOptionPane.showMessageDialog(null, "转换成功!","温馨提示",JOptionPane.INFORMATION_MESSAGE);
                }else if(result == 2){
                    JOptionPane.showMessageDialog(null, "选择路径不存在PDF订单文件，请检查!","温馨提示",JOptionPane.WARNING_MESSAGE);
                }else if(result == -1){
                    JOptionPane.showMessageDialog(null, "转换异常!请联系系统管理员","温馨提示",JOptionPane.ERROR_MESSAGE);
                }
            }catch (Exception ex){
               // ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "转换异常!请联系系统管理员","温馨提示",JOptionPane.ERROR_MESSAGE);
            }
        }


    }

    private void openChooserFileDialog(JTextField descTextField){
        JFileChooser chooser = new JFileChooser();
       // chooser.get
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showDialog(new JLabel(), "选择");
        File file = chooser.getSelectedFile();
        if(file != null){
            String path = file.getAbsoluteFile().toString();
            descTextField.setText(path);
           // System.out.println("获取到的路径是：" + path);
        }
    }
}
