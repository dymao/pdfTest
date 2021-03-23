package com.mervin;

import com.mervin.eum.ResultEnum;
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
public class MainFrame extends JFrame implements ActionListener {

    public static final int WIN_WIDTH_SIZE = 455;
    public static final int WIN_HEIGHT_SIZE = 350;

    private JLabel srcLabel;
    private JTextField srcTextField;
    private JButton srcBtn;

    private JLabel desLabel;
    private JTextField descTextField;
    private JButton descBtn;

    private JLabel intervalWidthLabel;
    private JTextField intervalWidthTextField;

    private JLabel intervalHeightLabel;
    private JTextField intervalHeightTextField;




    private JButton sureBtn;

    private Image icon;

    public MainFrame(){
        this.setTitle("订单合成小工具V1.2");
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

        intervalWidthLabel = new JLabel("横向间距:");
        intervalWidthTextField = new JTextField("25");
        intervalWidthTextField.setPreferredSize(new Dimension(70,30));

        intervalHeightLabel = new JLabel("竖向间距:");
        intervalHeightTextField = new JTextField("25");
        intervalHeightTextField.setPreferredSize(new Dimension(70,30));

        sureBtn = new JButton("开始转换");
        sureBtn.setPreferredSize(new Dimension(100, 40));
        icon = Toolkit.getDefaultToolkit().getImage(MainFrame.class.getClassLoader().getResource("images/mdy.png"));  // 图片的具体位置

        this.setIconImage(icon);   //设置窗口的logo

        ImageIcon img = new ImageIcon(MainFrame.class.getClassLoader().getResource("images/bg.jpg"));
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

        contain.add(intervalWidthLabel);
        contain.add(intervalWidthTextField);
        contain.add(intervalHeightLabel);
        contain.add(intervalHeightTextField);

        sureBtn.addActionListener(this);
        contain.add(sureBtn);

        this.setBounds(getLocalX(), getLocalY(), WIN_WIDTH_SIZE, WIN_HEIGHT_SIZE);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
       new MainFrame();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == srcBtn){
            openChooserFileDialog(srcTextField);
        }else if(e.getSource() == descBtn){
            openChooserFileDialog(descTextField);
        }else if(e.getSource() == sureBtn){
            if(!check()){
                return;
            }
            sureBtn.setText("正在转换...");
            sureBtn.setEnabled(false);
            sureBtn.removeActionListener(this);
            ErrorInfoCache.clear();
            try {
                int result = PdfToImageUtil.pdf2multiImage(srcTextField.getText(),
                        descTextField.getText(),
                        Integer.parseInt(intervalWidthTextField.getText()),
                        Integer.parseInt(intervalHeightTextField.getText()));
                sureBtn.setText("开始转换");
                sureBtn.setEnabled(true);
                sureBtn.addActionListener(this);
                String resultDesc = ErrorInfoCache.getInfoMsg(result);
                JOptionPane.showMessageDialog(null, resultDesc,"温馨提示",JOptionPane.INFORMATION_MESSAGE);
            }catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "转换异常!请联系系统管理员","温馨提示",JOptionPane.ERROR_MESSAGE);
            }
            ErrorInfoCache.clear();
        }
    }

    /**
     * 打开文件选择框
     * @param descTextField
     */
    private void openChooserFileDialog(JTextField descTextField){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showDialog(new JLabel(), "选择");
        File file = chooser.getSelectedFile();
        if(file != null){
            String path = file.getAbsoluteFile().toString();
            descTextField.setText(path);
        }
    }

    /**
     * 获取X坐标
     * @return
     */
    private int getLocalX(){
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        return (int) screensize.getWidth() / 2 - WIN_WIDTH_SIZE / 2;
    }

    /**
     * 获取Y坐标
     * @return
     */
    private int getLocalY(){
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        return (int) screensize.getHeight() / 2 - WIN_HEIGHT_SIZE / 2;
    }


    /**
     * 输入相关校验逻辑
     * @return
     */
    private boolean check(){
        String srcPath = srcTextField.getText();
        if(srcPath == null || "".equals(srcPath.trim())){
            JOptionPane.showMessageDialog(null, "请选择PDF订单文件夹!","温馨提示",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String descPath = descTextField.getText();
        if(descPath == null || "".equals(descPath.trim())){
            JOptionPane.showMessageDialog(null, "请选择订单图片存放路径!","温馨提示",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String offWidthStr = intervalWidthTextField.getText();
        if(!Utils.isNumeric(offWidthStr)){
            JOptionPane.showMessageDialog(null, "横向间距只能为整数!","温馨提示",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String offHeightStr = intervalHeightTextField.getText();
        if(!Utils.isNumeric(offHeightStr)){
            JOptionPane.showMessageDialog(null, "竖向间距只能为整数!","温馨提示",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        int offWidth = Integer.parseInt(offWidthStr);
        int offHeight = Integer.parseInt(offHeightStr);
        if(offHeight < 0 || offWidth < 0 || offWidth > 100 || offHeight > 100){
            JOptionPane.showMessageDialog(null, "间距只能在0-100之间!","温馨提示",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
