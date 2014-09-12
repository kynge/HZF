package com.whty.qd.upay.common;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 文件处理工具类
 * 
 * @author ShawnXiao
 * 
 */
public class YTFileHelper {

	private Context mContext;
	
	private static final String MY_FILE_DIR = "HZF_Simple";
	
	

	public static YTFileHelper mSelf;



	public static YTFileHelper getInstance(Context context) {

		if (null == mSelf) {
			mSelf = new YTFileHelper(context);
		}
		return mSelf;

	}

	private YTFileHelper(Context context){
		mContext = context;
	}
	

	/**
	 * 检查是否有SD卡，并有读写权限
	 * 
	 * @return
	 */
	public static final boolean checkSDCard() {

		boolean result = false;
		String state = Environment.getExternalStorageState();

		if (state.toLowerCase().equals(Environment.MEDIA_MOUNTED.toLowerCase())) {
			result = true;
		}
		return result;

	}
	/**
	 * 将Url转化为Bitmap
	 * @param logoUrl
	 * @return
	 */
	public static Bitmap url2Bitmap(String logoUrl){
		InputStream is = null;
		Bitmap image=null;
		try{
			URL url = new URL(logoUrl);        
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();        
			conn.setReadTimeout(10000 /* milliseconds */);        
			conn.setConnectTimeout(15000 /* milliseconds */);        
			conn.setRequestMethod("GET");       
			conn.setDoInput(true);        
			conn.connect();        
			int response = conn.getResponseCode();
			if(response != 200){
				return null;        				
			}
			is = conn.getInputStream();
			image=BitmapFactory.decodeStream(is);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return image;
	}
	/**
	 * 创建一个手机文件
	 * 
	 * @param filename
	 * @return
	 */
	public File createFile(String filename) {
		File file = null;
		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				file = new File(rootPath + "/"+MY_FILE_DIR+"/" + filename);
			}
		} catch (Exception e) {
		}
		return file;
	}

	/**
	 * 创建一个手机文件
	 * 
	 * @param filename
	 *            文件名
	 * @return 布尔值
	 */
	public boolean isFileExist(String filename) {
		File file = null;
		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				file = new File(rootPath + "/"+MY_FILE_DIR+"/" + filename);
				if (file.exists()) {

					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 删除一个文件
	 * 
	 * @param filename
	 *            文件名
	 */
	public void deletExistFile(String filename) {
		File file = null;
		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				file = new File(rootPath + "/"+MY_FILE_DIR+"/" + filename);
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 读取文件中的数据
	 * 
	 * @param filename
	 *            文件名
	 * @return 字节数组数据
	 */
	public final byte[] readFile(String filename) {
		FileInputStream fis = null;
		byte[] bytes = null;
		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				File file = new File(rootPath + "/"+MY_FILE_DIR+"/" + filename);
				fis = new FileInputStream(file);
				bytes = readByByte(fis, -1);
			} else {
				fis = mContext.openFileInput(filename);
				bytes = readByByte(fis, -1);
			}
		} catch (Exception e) {
			bytes = null;
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception e) {
			}
		}

		return bytes;
	}
	
	
    public static byte[] readByByte(InputStream dis, long len) throws IOException {
    	byte[] result = null;
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bufferSize = 512 ;
        byte byteInput[] = new byte[bufferSize] ;
        int size = 0;
        if (len!=-1) {
            long askSize = 0;
            while (len > 0) {
                askSize = (len < (long)bufferSize) ? len : (long)bufferSize;
                if ((size = dis.read(byteInput, 0, (int)askSize)) != -1) {
                    len -= size;
                    buffer.write(byteInput, 0, size);
                } else {
                    break;
                }
            }
        } else {
            while ((size = dis.read(byteInput, 0, bufferSize)) != -1) {
                buffer.write(byteInput, 0, size);
            }
        }
        result = buffer.toByteArray();
        return result;
    }

	/**
	 * 保存文件
	 * 
	 * @param filename
	 *            要保存数据为文件的文件名
	 * @param data
	 *            要保存的字节数组数据
	 */
	public final void saveFile(String filename, byte[] data) {

		FileOutputStream fos = null;

		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				File file = new File(rootPath + "/"+MY_FILE_DIR);
				if (!file.exists()) {
					file.mkdir();
				}
				File png = new File(file.getPath() + "/" + filename);
				if (png.exists()) {
					png.delete();
				}
				fos = new FileOutputStream(png, true);
				fos.write(data);
				fos.close();
			} else {
				fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(data);
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (Exception e) {
				}
			}
		}

	}

	/**
	 * 将对象数据序列化为文件
	 * @param filename
	 * 要序列化为文件的文件名
	 * @param data
	 * 对象数据
	 */
	public final void serialObject(String filename, Object data) {

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				File file = new File(rootPath + "/"+MY_FILE_DIR);
				if (!file.exists()) {
					file.mkdir();
				}
				File png = new File(file.getPath() + "/" + filename);
				if (png.exists()) {
					png.delete();
				}
				fos = new FileOutputStream(png, true);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(data);
				oos.close();
				fos.close();
			} else {
				fos = mContext.openFileOutput(filename, Context.MODE_APPEND);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(data);
				oos.close();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null || oos != null) {
				try {
					oos.close();
					oos = null;
					fos.close();
					fos = null;
				} catch (Exception e) {
				}
			}
		}

	}

	/**
	 * 反序列化文件为对象
	 * @param filename
	 * 保存的文件名
	 * @return
	 * 对象数据
	 */
	public final Object deSerialObject(String filename) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Object data = null;
		try {
			if (checkSDCard()) {
				File rootDir = Environment.getExternalStorageDirectory();
				String rootPath = rootDir.getAbsolutePath();
				File file = new File(rootPath + "/"+MY_FILE_DIR+"/" + filename);
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				data = ois.readObject();
				ois.close();
				fis.close();
			} else {
				fis = mContext.openFileInput(filename);
				ois = new ObjectInputStream(fis);
				data = ois.readObject();
				ois.close();
				fis.close();
			}
		} catch (Exception e) {
			data = null;
		} finally {
			try {
				if (fis != null || ois != null) {
					ois.close();
					ois = null;
					fis.close();
					fis = null;
				}
			} catch (Exception e) {
			}
		}

		return data;
	}

}
