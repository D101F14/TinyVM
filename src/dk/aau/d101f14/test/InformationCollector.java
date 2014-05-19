package dk.aau.d101f14.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class InformationCollector {
	
	public static void main(String[] args) {
		ArrayList<String> errorList = new ArrayList<String>();
		HashMap<String,Integer> faultList = new HashMap<String,Integer>();
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
        int masked = 0;
        int correctRecovery = 0;
		
        String pathToRootDirectoryForVM = args[0];
        String pathToVMJarFile = args[1];
        String pathToScript = args[2];
        int numberOfTimesToRun = Integer.parseInt(args[3]);
        String byteManPath = args[4];
        
        int progress = 0;
        int tempProgress = 0;
        
        for(int i = 0; i < numberOfTimesToRun;i++){
	        ProcessBuilder pb = new ProcessBuilder("java", "-javaagent:"+byteManPath +"\\lib\\byteman.jar=script:"+pathToScript, "-jar", pathToVMJarFile, pathToRootDirectoryForVM, ""+(int)(Math.random()*11300));
	        pb.redirectErrorStream();
	        try {
	        	tempProgress = i * 100 / numberOfTimesToRun;
	        	if(tempProgress/5 > progress/5){
	        		progress = tempProgress;
	        		System.out.println(progress + "%");
	        	}
	            Process p = pb.start();
	            
	            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(),"");
	            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(),"");
	            
	            errorGobbler.start();
	            outputGobbler.start();
	            
	            p.waitFor();
	            
	            errorGobbler.join();
	            outputGobbler.join();
	            
	            String errorText = errorGobbler.getString();
	            String output = outputGobbler.getString();	            
	            String returnText = "";
	            String expectedResult = "0123456789";
	            
	            if(output.contains("#")){
	            	returnText = output.split("#")[1].replace("\n", "");
	            }
	            
	            
	            String flip = identifyFault(output);
	            String fault = "";
	            int count = 0;
	            
	            if(p.exitValue() == returnValues.NORMAL.getCode()){
	            	if(returnText.contentEquals(expectedResult) && output.contains("ROLLBACK")){
	            		correctRecovery++;
	            		fault = "Recovery";
	            		errorList.add(fault+"\n"+output);
	            	}else if(returnText.contentEquals(expectedResult) && output.contains("After instruction") && !output.contains("ROLLBACK")){
	            		masked++;
	            		fault = "Masked";
	            		errorList.add("Masked:\n"+output);
	            	}else if(returnText.contentEquals(expectedResult) && !output.contains("After instruction") && !output.contains("ROLLBACK")){
	            		normalTermination++;
	            		fault = "No bitflip";
	            		errorList.add("No bitflip:\n"+output);
	            	}else if(!returnText.contentEquals(expectedResult) && output.contains("After instruction") && output.contains("ROLLBACK")){
	            		silentDataCorruption++;
	            		fault = "Silent Data Corruption";
	            		errorList.add("Silent Data Corruption:\n"+output);
	            	}else{
	            		System.out.println("No idea what went wrong here:");
	            		System.out.println(output);
	            	}
	            }else if(p.exitValue() == returnValues.JAVAFAULT.getCode()){           
	                if(errorText.contains("NullPointerException")){
	                	javaNullPointerException++;
	                	fault = "Java crash (Null pointer)";
	                	errorList.add("Java crash (Null pointer):\n"+output+"\n"+errorText);
	                }else if(errorText.contains("ArrayIndexOutOfBoundsException")){
	                	javaArrayIndexOutOfBoundsException++;
	                	fault = "Java crash (Array index out of bounds)";
	                	errorList.add("Java crash (Array index out of bounds):\n"+output+"\n"+errorText);
	                }else if(errorText.contains("OutOfMemoryException")){
	                	javaOutOfMemoryException++;
	                	fault = "Java crash (Out of memory)";
	                	errorList.add("Java crash (Out of memory):\n"+output+"\n"+errorText);
	                }else{
	                	unknown++;
	                	fault = "Unknown";
	                	errorList.add("Java crash (unknown):\n"+output+"\n"+errorText);
	                }
	            }else if(p.exitValue() == returnValues.NULLREF.getCode()){
	            	tinyVMNullReferenceException++;
	            	fault = "TinyVm unhandled exception (Null reference)";
	            	errorList.add("TinyVm unhandled exception (Null reference):\n"+output);
	            }else if(p.exitValue() == returnValues.OUTOFMEM.getCode()){
	            	tinyVMOutOfMemoryException++;
	            	fault = "TinyVm unhandled exception (Out of memory)";
	            	errorList.add("TinyVm unhandled exception (Out of memory):\n"+output);
	            }else if(p.exitValue() == returnValues.DIVBYZERO.getCode()){
	            	tinyVMDivisionByZeroException++;
	            	fault = "TinyVm unhandled exception (Division by zero)";
	            	errorList.add("TinyVm unhandled exception (Division by zero):\n"+output);
	            }else if(p.exitValue() == returnValues.FIELD.getCode()){
	            	tinyVMInvalidField++;
	            	fault = "TinyVm undefined field access";
	            	errorList.add("TinyVm undefined field access:\n"+output);
	            }
	            
	            
	            if(faultList.containsKey(flip+","+fault)){
	            	count = faultList.get(flip+","+fault).intValue();
	            }
	            faultList.put(flip+","+fault, new Integer(++count));
	            
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
        System.out.println("         No bitflip:\t\t\t\t "                    + normalTermination);
        System.out.println("         Recovery:\t\t\t\t "                      + correctRecovery);
        System.out.println("         Masked:\t\t\t\t "                        + masked);
        System.out.println("         Silent Data Corruption:\t\t "            + silentDataCorruption);
        System.out.println("(TinyVM) Null Reference Exception:\t\t "          + tinyVMNullReferenceException);
        System.out.println("(Java)   Null Pointer Exception:\t\t "            + javaNullPointerException);
        System.out.println("(TinyVM) Out Of Memory Exception:\t\t "           + tinyVMOutOfMemoryException);
        System.out.println("(Java)   Out Of Memory Exception:\t\t "           + javaOutOfMemoryException);
        System.out.println("(TinyVM) Division By Zero Exception:\t\t "        + tinyVMDivisionByZeroException);
        System.out.println("(TinyVM) Invalid Field:\t\t\t\t "                 + tinyVMInvalidField);
        System.out.println("(Java)   Array Index Out Of Bounds Exception:\t " + javaArrayIndexOutOfBoundsException);
        System.out.println("(Java)   Unknown:\t\t\t\t "                       + unknown);
        
        
        String os = "\nTermination by bitflip on the Operand Stack:\n";
        String os_r = "\nTermination by bitflip on the Operand Stack R:\n";
        String pc = "\nTermination by bitflip in the Program Counter:\n";
        String pc_r = "\nTermination by bitflip on the Program Counter R:\n";
        String lh = "\nTermination by bitflip in the Local Heap:\n";
        String lh_r = "\nTermination by bitflip in the Local Heap R:\n";
        String lv = "\nTermination by bitflip in the Local Variables:\n";
        String lv_r = "\nTermination by bitflip in the Local Variables R:\n";
        boolean printOS = false;
        boolean printOS_R = false;
        boolean printPC = false;
        boolean printPC_R = false;
        boolean printLH = false;
        boolean printLH_R = false;
        boolean printLV = false;
        boolean printLV_R = false;
        
        for(Entry<String, Integer> entry : faultList.entrySet()){
        	
        	String[] split = entry.getKey().split(",");
        	
        	String flip = split[0];
        	String fault = split[1];
        	
        	switch(flip){
        	case "OS":
        		os += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printOS = true;
        		break;
        	case "OS_R":
        		os_r += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printOS_R = true;
        		break;
        	case "PC":
        		pc += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printPC = true;
        		break;
        	case "PC_R":
        		pc_r += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printPC_R = true;
        		break;
        	case "LH":
        		lh += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printLH = true;
        		break;
        	case "LH_R":
        		lh_r += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printLH_R = true;
        		break;
        	case "LV":
        		lv += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printLV = true;
        		break;
        	case "LV_R":
        		lv_r += "\t"+fault+": "+entry.getValue().intValue() + "\n";
        		printLV_R = true;
        		break;
        	default:
        		break;
        	}        	
        }
        
        if(printOS)
        	System.out.println(os);
        if(printOS_R)
        	System.out.println(os_r);
        if(printPC)
        	System.out.println(pc);
        if(printPC_R)
        	System.out.println(pc_r);
        if(printLH)
        	System.out.println(lh);
        if(printLH_R)
        	System.out.println(lh_r);
        if(printLV)
        	System.out.println(lv);
        if(printLV_R)
        	System.out.println(lv_r);
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
	
	private static String identifyFault(String text){
		
		if(text.contains("After instruction") && text.contains("OS") && !text.contains("OS_R")){
			return "OS";
		}else if(text.contains("After instruction") && text.contains("OS_R")){
			return "OS_R";
		}else if(text.contains("After instruction") && text.contains("Program Counter") && !text.contains("Program Counter R")){
			return "PC";
		}else if(text.contains("After instruction") && text.contains("Program Counter R")){
			return "PC_R";
		}else if(text.contains("After instruction") && text.contains("Local Heap") && !text.contains("Local Heap R")){
			return "LH";
		}else if(text.contains("After instruction") && text.contains("Local Heap R")){
			return "LH_R";
		}else if(text.contains("After instruction") && text.contains("Local Variables") && !text.contains("Local Variables R")){
			return "LV";
		}else if(text.contains("After instruction") && text.contains("Local Variables R")){
			return "LV_R";
		}
		
		return "";
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
