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
		
        String pathToRootDirectoryForVM = args[0];
        String pathToVMJarFile = args[1];
        String pathToScript = args[2];
        int numberOfTimesToRun = Integer.parseInt(args[3]);
        String byteManPath = args[4];
        
        for(int i = 0; i < numberOfTimesToRun;i++){
	        ProcessBuilder pb = new ProcessBuilder("java", "-javaagent:"+byteManPath +"\\lib\\byteman.jar=script:"+pathToScript, "-jar", pathToVMJarFile, pathToRootDirectoryForVM);
	        pb.redirectErrorStream();
	        try {
	        	System.out.println((i * 100 / numberOfTimesToRun) + "%");
	            Process p = pb.start();
	           
	            p.waitFor();
	            
	            if(p.exitValue() == returnValues.NORMAL.getCode()){
	            	String text = readString(p.getInputStream());
	            	
	            	if(text.contains("0123456789"))
	            	{
	            		normalTermination++;
	            		errorList.add("Normal termination:\n"+text);
	            	}else{
	            		silentDataCorruption++;
	            		errorList.add("Silent data corruption:\n"+text);
	            	}
	            }else if(p.exitValue() == returnValues.JAVAFAULT.getCode()){
	            	errorText = readString(p.getErrorStream());
	                
	                if(errorText.contains("NullPointerException")){
	                	javaNullPointerException++;
	                	errorList.add("Java crash (Null pointer):\n"+readString(p.getInputStream())+"\n"+errorText);
	                }else if(errorText.contains("ArrayIndexOutOfBoundsException")){
	                	javaArrayIndexOutOfBoundsException++;
	                	errorList.add("Java crash (Array index out of bounds):\n"+readString(p.getInputStream())+"\n"+errorText);
	                }else if(errorText.contains("OutOfMemoryException")){
	                	javaOutOfMemoryException++;
	                	errorList.add("Java crash (Out of memory):\n"+readString(p.getInputStream())+"\n"+errorText);
	                }else{
	                	unknown++;
	                	errorList.add("Java crash (unknown):\n"+readString(p.getInputStream())+"\n"+errorText);
	                }
	            }else if(p.exitValue() == returnValues.NULLREF.getCode()){
	            	tinyVMNullReferenceException++;
	            	errorList.add("TinyVm unhandled exception (Null reference):\n"+readString(p.getInputStream()));
	            }else if(p.exitValue() == returnValues.OUTOFMEM.getCode()){
	            	tinyVMOutOfMemoryException++;
	            	errorList.add("TinyVm unhandled exception (Out of memory):\n"+readString(p.getInputStream()));
	            }else if(p.exitValue() == returnValues.DIVBYZERO.getCode()){
	            	tinyVMDivisionByZeroException++;
	            	errorList.add("TinyVm unhandled exception (Division by zero):\n"+readString(p.getInputStream()));
	            }else if(p.exitValue() == returnValues.FIELD.getCode()){
	            	tinyVMInvalidField++;
	            	errorList.add("TinyVm undefined field access:\n"+readString(p.getInputStream()));
	            }
	            p.destroy();
	        } catch (Exception exp) {
	            exp.printStackTrace();
	        }  
        }
        
        System.out.println("Output for the individual runs:");
    	for(int i = 0; i < errorList.size(); i++){
    		System.out.println("\nRun "+(i+1)+" - " + errorList.get(i));
    	}
        
        System.out.println("\nTotal count of termination status:\n");
        System.out.println("         Normal termination:\t\t\t "              + normalTermination);
        System.out.println("         Silent Data Corruption:\t\t "            + silentDataCorruption);
        System.out.println("(TinyVM) Null Reference Exception:\t\t "          + tinyVMNullReferenceException);
        System.out.println("(Java)   Null Pointer Exception:\t\t "            + javaNullPointerException);
        System.out.println("(TinyVM) Out Of Memory Exception:\t\t "           + tinyVMOutOfMemoryException);
        System.out.println("(Java)   Out Of Memory Exception:\t\t "           + javaOutOfMemoryException);
        System.out.println("(TinyVM) Division By Zero Exception:\t\t "        + tinyVMDivisionByZeroException);
        System.out.println("(TinyVM) Invalid Field:\t\t\t\t "                 + tinyVMInvalidField);
        System.out.println("(Java)   Array Index Out Of Bounds Exception:\t " + javaArrayIndexOutOfBoundsException);
        System.out.println("(Java)   Unknown:\t\t\t\t "                       + unknown);        
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
