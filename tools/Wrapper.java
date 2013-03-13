import java.io.*;
public class Wrapper{
	public static void main(String argv[]){
		try{
			String []files=new File("../data/_indri_xml").list();
			File [] fs=new File("../data/_indri_xml").listFiles();
			String out="../data/_indri_xml_wrapped/";
			for (int i=0;i<files.length;i++){
				if (files[i].matches(".*DS_Store.*"))
					continue;
				BufferedReader br=new BufferedReader(new FileReader(fs[i]));
				BufferedWriter bw=new BufferedWriter(new FileWriter(new File(out+files[i].substring(files[i].length()-12,files[i].length()))));
				String line=null;
				bw.write("<root>");
				bw.newLine();
				while ((line=br.readLine())!=null){
					line=line.replaceAll("&","&amp;");
					//replace all unicode chars
					line = line.replaceAll("[\\u0000-\\u0008\\u000B\\u000C" 
                        + "\\u000E-\\u001F" 
                        + "\\uD800-\\uDFFF\\uFFFE\\uFFFF\\u00C5\\u00D4\\u00EC"
                        + "\\u00A8\\u00F4\\u00B4\\u00CC\\u2211]", "");
					int begin=-1;
					int end=-1;

					//get away the <  and >
					char [] c=new char[line.length()];
					line.getChars(0,line.length(),c,0);
					for (int k=0;k<c.length;k++){
						if (c[k]=='>'){
							if (begin==-1)
								begin=k;
						}

						if (c[k]=='<'){
							end=k;
						}
					}

					for (int k=begin+1;k<end-1;k++){
						if (c[k]=='<' || c[k]=='>')
							c[k]=' ';
					}

					line=new String(c);

					bw.write(line);
					bw.newLine();
				}
				bw.write("</root>");
				bw.newLine();
				bw.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}