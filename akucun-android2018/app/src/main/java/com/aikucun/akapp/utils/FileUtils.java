package com.aikucun.akapp.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by jarry on 16/10/19.
 */

public class FileUtils
{

    /**
     * 检查文件是否存在
     *
     * @param name 文件名
     * @return 文件是否存在
     */
    public static boolean checkFileExists(String name)
    {
        boolean status;
        if (!name.equals(""))
        {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        }
        else
        {
            status = false;
        }
        return status;
    }

    /**
     * 检查路径是否存在
     *
     * @param path 文件路径
     * @return 文件路径是否存在
     */
    public static boolean checkFilePathExists(String path)
    {
        return new File(path).exists();
    }

    /**
     * 删除目录(包括：目录里的所有文件 包括子目录)
     *
     * @param filePath 文件路径
     * @return 是否成功删除
     */
    public static boolean deleteDirectory(String filePath)
    {
        boolean flag = false;
        // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
        {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory())
        {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        // 遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile())
            {
                // 删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                {
                    break;
                }
            }
            else
            {
                // 删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                {
                    break;
                }
            }
        }
        if (!flag)
        {
            return false;
        }
        // 删除当前空目录
        return dirFile.delete();
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 是否成功删除
     */
    public static boolean deleteFile(String fileName)
    {
        if (fileName.equals(""))
        {
            return false;
        }

        boolean status;
        SecurityManager checker = new SecurityManager();

        //            File path = Environment.getExternalStorageDirectory();
        File newPath = new File(fileName);
        checker.checkDelete(newPath.toString());
        if (newPath.isFile())
        {
            try
            {
                newPath.delete();
                status = true;
            }
            catch (SecurityException se)
            {
                se.printStackTrace();
                status = false;
            }
        }
        else
        {
            status = false;
        }

        return status;
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 获取图片压缩路径
     * @return
     */
    public static String getImgCompressPath() {
        String path = Environment.getExternalStorageDirectory() + "/aku/image/compress/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    /**
     * 删除转发目录下所有文件
     */
    public static void deleteForwardImgs(){
        final String dirPath = Environment.getExternalStorageDirectory() + "/akucun/";
        final File dir = new File(dirPath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteDirWihtFile(dir);
            }
        }).start();

    }

    private static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory() || dir.listFiles()== null || dir.listFiles().length==0)
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


}
