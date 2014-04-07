package com.uniks.fsmsim.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.uniks.fsmsim.data.TransitionValue;
import android.os.Environment;
import android.util.Log;


public class SaveFile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<SaveState> states	= new ArrayList<SaveFile.SaveState>();
	List<SaveTransition> transitions	= new ArrayList<SaveFile.SaveTransition>();
	private int inputCount;
	private int ouputCount;
	
	//0 = mealy / 1 = moore
	private int fsmType = 0;

	public int getFsmType(){
		return fsmType;
	}

	public List<SaveState> getStates() {
		return states;
	}
	public List<SaveTransition> getTransition() {
		return transitions;
	}
	public int getInputCount() {
		return inputCount;
	}
	public int getOuputCount() {
		return ouputCount;
	}

	public void addState(String name, String output, boolean isStart, boolean isEnd, float posX, float posY){
		states.add(new SaveState(name, output, isStart, isEnd, posX, posY));
	}
	
	public void addTransition(String from, String to, List<TransitionValue> valueList, float dragpoint_x, float dragpoint_y){
		transitions.add(new SaveTransition(from, to, valueList, dragpoint_x, dragpoint_y));
	}
	
	public boolean saveAll(String name, int inputCount, int outputCount, int fsmType){
		this.inputCount = inputCount;
		this.ouputCount = outputCount;
		this.fsmType = fsmType;
		if(saveObject(this, name)){

			return true;
		}
		return false;
	}
	
    public boolean saveObject(SaveFile sf, String name){	
        try
        {
           createDirIfNotExists("/fsmSave/");
           ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/fsmSave/"+name+".bin"))); //Select where you wish to save the file...
           
           oos.writeObject(sf); // write the class as an 'object'
           oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
           oos.close();// close the stream
           return true;
        }
        catch(Exception ex)
        {
           Log.v("Serialization Save Error : ",ex.getMessage());
           ex.printStackTrace();
           return false;
        }
   }
    
   public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }
    
   public static Object loadSerializedObject(File f)
   {
       try
       {
           ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
           Object o = ois.readObject();
           return o;
       }
       catch(Exception ex)
       {
       Log.v("Serialization Read Error : ",ex.getMessage());
           ex.printStackTrace();
       }
       return null;
   }
	
	public class SaveState implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String name, output;
		boolean isStart, isEnd;
		float posX, posY;
		
		public SaveState(String name, String output, boolean isStart, boolean isEnd, float posX, float posY){
			this.name = name;
			this.output = output;
			this.isStart = isStart;
			this.isEnd = isEnd;
			this.posX = posX;
			this.posY = posY;
		}
		public String getName(){
			return name;
		}
		public String getOutput() {
			return output;
		}
		public boolean isStart() {
			return isStart;
		}
		public boolean isEnd() {
			return isEnd;
		}
		public float getPosX() {
			return posX;
		}
		public float getPosY() {
			return posY;
		}
	}
	public class SaveTransition implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String from, to;
		float dragpoint_x;
		float dragpoint_y;
		
		private List<TransitionValue> valueList = new ArrayList<TransitionValue>();
		
		public SaveTransition(String from, String to, List<TransitionValue> valueList, float dragpoint_x, float dragpoint_y) {
			super();
			this.from = from;
			this.to = to;
			this.valueList = valueList;
			this.dragpoint_x = dragpoint_x;
			this.dragpoint_y = dragpoint_y;
		}

		public List<TransitionValue> getValueList() {
			return valueList;
		}

		public String getFrom() {
			return from;
		}

		public String getTo() {
			return to;
		}

		public float getDragpoint_x() {
			return dragpoint_x;
		}

		public float getDragpoint_y() {
			return dragpoint_y;
		}	
	}	
}
