package dk.aau.d101f14.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class InformationCollector {
	
	public static void main(String[] args) {
		String errorText = "";
		ArrayList<String> errorList = new ArrayList<String>();
		int normalTermination = 0;
        int silentDataCorruption = 0;
        int tinyVMNullReferenceException = 0;
        int javaNullPointerException = 0;
        int tinyVMOutOfMemoryException = 0;
        int javaOutOfMemoryException = 0;
        int tinyVMDivisionByZeroException = 0;
        int tinyVMInvalidField = 0;
        int javaArrayIndexOutOfBoundsException = 0;
        int unknown = 0;
		
        //Modify this to call the right file
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "C:\\Users\\Rune\\workspace\\TestOfExecutables\\bin\\ExecutableTest\\run.jar");
        pb.redirectErrorStream();
        try {
            Process p = pb.start();
           
            p.waitFor();
            
            if(p.exitValue() == returnValues.NORMAL.getCode()){
            	//Normal termination
            	normalTermination++;
            }else if(p.exitValue() == returnValues.JAVAFAULT.getCode()){
            	//Something went wrong
            	errorText = readString(p.getErrorStream());
                
                if(errorText.contains("NullPointerException")){
                	javaNullPointerException++;
                }else if(errorText.contains("ArrayIndexOutOfBoundsException")){
                	javaArrayIndexOutOfBoundsException++;
                }else if(errorText.contains("OutOfMemoryException")){
                	javaOutOfMemoryException++;
                }else{
                	unknown++;
                	errorList.add(errorText);
                }
            }else if(p.exitValue() == returnValues.NULLREF.getCode()){
            	tinyVMNullReferenceException++;
            }else if(p.exitValue() == returnValues.OUTOFMEM.getCode()){
            	tinyVMOutOfMemoryException++;
            }else if(p.exitValue() == returnValues.DIVBYZERO.getCode()){
            	tinyVMDivisionByZeroException++;
            }else if(p.exitValue() == returnValues.FIELD.getCode()){
            	tinyVMInvalidField++;
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        
        System.out.println("Information of execution termination:\n");
        System.out.println("         Masked Error:\t\t\t\t "                  + normalTermination);
        System.out.println("         Silent Data Corruption:\t\t "            + silentDataCorruption);
        System.out.println("(TinyVM) Null Reference Exception:\t\t "          + tinyVMNullReferenceException);
        System.out.println("(Java)   Null Pointer Exception:\t\t "            + javaNullPointerException);
        System.out.println("(TinyVM) Out Of Memory Exception:\t\t "           + tinyVMOutOfMemoryException);
        System.out.println("(Java)   Out Of Memory Exception:\t\t "           + javaOutOfMemoryException);
        System.out.println("(TinyVM) Division By Zero Exception:\t\t "        + tinyVMDivisionByZeroException);
        System.out.println("(TinyVM) Invalid Field:\t\t\t\t "                 + tinyVMInvalidField);
        System.out.println("(Java)   Array Index Out Of Bounds Exception:\t " + javaArrayIndexOutOfBoundsException);
        System.out.println("         Unknown:\t\t\t\t "                       + unknown);
        
        System.out.println("\n\n Log messages for sources of 'unknown':");
    	for(int i = 0; i < errorList.size(); i++){
    		System.out.println(i + errorList.get(i));
    	}        
    }
	
	private static String readString(InputStream is){
		
		StringBuilder sb = new StringBuilder(64);
        int value = -1;
        try {
            while ((value = is.read()) != -1) {
                sb.append((char)value);
            }
        } catch (IOException exp) {
            exp.printStackTrace();
            sb.append(exp.getMessage());
        }
        return sb.toString();
	}
	
	
	public enum returnValues{
		NORMAL(0),JAVAFAULT(1),NULLREF(2),OUTOFMEM(3),DIVBYZERO(4),FIELD(5);
		
		private int errorCode;
		
		private returnValues(int id){
			errorCode = id;
		}
		
		public int getCode(){
			return errorCode;
		}
		
	}
}
