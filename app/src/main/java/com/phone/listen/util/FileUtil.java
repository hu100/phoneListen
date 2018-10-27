package com.phone.listen.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
    /**
     * 删除文件(包括目录)
     *
     * @param file
     */
    public static void deleteAll(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteAll(childFiles[i]);
            }
            file.delete();
        }
    }


    /**
     * 删除文件(不包括目录)
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
        }
    }

    public static void delete(File[] files) {

        for (int i = 0; files != null && i < files.length; i++) {
            if (files[i].isFile()) {
                files[i].delete();

            }

            if (files[i].isDirectory()) {
                File[] childFiles = files[i].listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    files[i].delete();

                }

                for (int j = 0; childFiles != null && j < childFiles.length; j++) {
                    delete(childFiles[j]);
                }
                files[i].delete();
            }
        }
    }

    public static void transImage(String fromFile, String toFile, float scale, int quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸比列
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            // 产生缩放后的Bitmap对象  
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file  
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();//记得释放资源，否则会内存溢出  
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 压缩图片
     *
     * @param bitmap
     * @param toFile  保存地址
     * @param scale   压缩比例
     * @param quality 质量 （0-100 100--最高质量）
     * @return
     */
    public static Bitmap transImage(Bitmap bitmap, String toFile, float scale, int quality) {
        try {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸  
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            // 产生缩放后的Bitmap对象  
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file  
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }

            //此处不能将其释放，否则会导致第二次抓拍失败，下面的代码要注释
            //  if(!bitmap.isRecycled()){
            //      bitmap.recycle();//记得释放资源，否则会内存溢出
            //  }
            return resizeBitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    //写入存储
    public static String writeToFile(String fileDir,String fileName, String content, boolean append) {
        File dir = new File(fileDir);
        if (!dir.exists()){
            dir.mkdirs();
        }
        String path = fileDir + "/" + fileName;
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file,append);
            byte[] bytes = content.getBytes();
            fos.write(bytes);
            fos.write("\r\n".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String readFromFile(File file) {
        String content = "";
        if (!file.exists()) {
            return "";
        } else {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(is);
                char[] input = new char[is.available()];
                reader.read(input);
                is.close();
                reader.close();
                content = new String(input).trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }
    }

    public static String readFromFile(String path) {
        String content = "";
        File file = new File(path);
        if (!file.exists()) {
            return "";
        } else {
            try {
                FileInputStream is = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(is);
                char[] input = new char[is.available()];
                reader.read(input);
                is.close();
                reader.close();
                content = new String(input).trim();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }
    }

}




