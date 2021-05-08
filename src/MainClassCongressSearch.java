
/**
 * @author Alexei Gladyshev 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.tika.Tika;


public class MainClassCongressSearch {

	public static void main(String[] args) throws IOException {

		int count = 1;	// first order of the document out of all the ones I will download
						// from the list of web addresses
		BufferedReader reader;
		String record_entire_day_2019_pdf_links = "src//cong record entire day 2019 pdf links.txt";
		
		try {
			// open the file with the links
			reader = new BufferedReader(new FileReader(record_entire_day_2019_pdf_links));
			String webAddress = reader.readLine();
			while (webAddress != null) {

				
				System.out.println("Heading to: " + webAddress);

				URL url1 = new URL(webAddress);
				
				String filedate = webAddress.substring(webAddress.length() - 14);
				// all of this is used to download the pdf file from the given website to local folder
				byte[] ba1 = new byte[1024];
				int baLength;
				String fileName = "doc_" + filedate; // файл сохраняется с числом в названии
				String localAddress = "downloaded//" + fileName;
				FileOutputStream fos1 = new FileOutputStream( localAddress );

				count++;
				
				try {

					// contacting the URL
					System.out.println("Connecting to: " + webAddress.toString() + "...");

					// read the pdf from the URL and save to a local file
					InputStream is1 = url1.openStream();

					while ((baLength = is1.read(ba1)) != -1) {
						fos1.write(ba1, 0, baLength);
					}
					fos1.flush();
					fos1.close();
					is1.close();
					
					
					// с этого момента программа читаете URL 
					Tika converter = new Tika();
					
					// может быть более эффективным, если я читал файл который уже скачался
					
					try {
						// создать новый файл в который мы записываем вырезки которые нас интересуют
						
						File f = new File("downloaded//" + fileName);
						converter.setMaxStringLength(-1);	// this is so the converter reads the entire file
						String parsed_string = converter.parseToString(f); // turns PDF into String at this step
						String temp_substring = "";
						
						System.out.println("Saving parts of the parsed string to text document");
						// need more information here for the console, such as what file I am currently working
						// with, and things like that. Make it easier to gather information about the progress.
						
						int keyword_index = 0;
						ArrayList<Integer> keyword_indexes = new ArrayList<Integer>();
						// keyword_indexes is an ArrayList of Integers filled by keyword_index
						final int KEYWORD_OFFSET = 600;
						
						String[] keywords = {"Russia", "Kremlin", "Moscow", "Putin", "Soviet", "Eurasia"};
						
						
						for (String keyword : keywords) {
							keyword_index = 0; // start from the beginning of the document each time
							do {
								// fringe case when we're starting to read from the beginning of document
								if (keyword_index == 0) {
									keyword_index = parsed_string.indexOf(keyword, keyword_index);
								} else {
									keyword_index = parsed_string.indexOf(keyword, keyword_index + keyword.length() );
								}
								if (keyword_index == -1)
									break;

								keyword_indexes.add(keyword_index);

							} while (keyword_index != -1); // continue until it can't find any more instances
						}
						
						// after this do while loop completes, we have an ArrayList of 
						// all our indexes of keywords
						
						Collections.sort(keyword_indexes);
						
						PrintWriter writer = new PrintWriter("result//" + keyword_indexes.size() + "_results–" + filedate.substring(0, filedate.length() - 4) + ".txt", "UTF-8");
						// в названии включено количество результатов
						
						// these 3 lines write the original text into the originals folder
						PrintWriter originalWriter = new PrintWriter("originals//" + "original_from_"+ filedate.substring(0, filedate.length() - 4) + ".txt", "UTF-8");
						originalWriter.print(parsed_string);
						originalWriter.close();
						
						writer.println("Данные взяты с документа: " + fileName );
						writer.println("Количество записей в этом документе: " + keyword_indexes.size());
						writer.print("Программы ищет ключевые слова в следующем порядке: ");
						
						for (String temp_word : keywords) {
							writer.print(temp_word + " ");
						}
						writer.println();
						
						int temp_count = 1;
						
						if (keyword_indexes.size() > 0) {
							for (int temp : keyword_indexes) {
								if (temp - KEYWORD_OFFSET < 0) {
									temp_substring = parsed_string.substring(0, temp + KEYWORD_OFFSET);
									writer.print(temp_substring);
									writer.println();
									writer.println("***************************************************");
								}
								else {
									temp_substring = parsed_string.substring(temp - KEYWORD_OFFSET, 
											temp + KEYWORD_OFFSET);
									writer.println(temp_count);
									writer.print(temp_substring);
									writer.println();
									writer.println("***************************************************");
								}
							temp_count++;
							}
						}
						System.out.println(keyword_indexes);
						System.out.println("Length of ArrayList: " + (keyword_indexes.size() ));

						writer.close();
						 
						
						
					} catch (Exception e) {
						System.err.print(e.getMessage());
					}
					
				} catch (ConnectException ce) {
					System.err.println("FAILED.\n[" + ce.getMessage() + "]\n");
				}
				
				// read next line
				webAddress = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("finished parsing given documents from '" + record_entire_day_2019_pdf_links + "'...");
	}

}

// в результатном файле количество упоминаний в названии
// текст полностью сохранялся , в отдельной папке
// важные количественные методы в науке
// количественные части лучше качественных
// какие приемущество политология
// Американцы 
// Шусенцов часто пишет на Валдае 

