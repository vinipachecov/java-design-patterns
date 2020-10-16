package Solid.DependencyInversion;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MessagePrinter {

	/**
	 * Problems
	 * - Tight coupling Format of the message can only be one
	 *
	 */

	/*Writes message to a file
	public void writeMessage(Message msg, String fileName) throws IOException {
		Formatter formatter = new JSONFormatter();//creates formatter
		try(PrintWriter writer = new PrintWriter(new FileWriter(fileName))) { //creates print writer
			writer.println(formatter.format(msg)); //formats and writes message
			writer.flush();
		}
	}*/

	/* Possible solution
	* - Move dependencies to array of parameters
	* This way we will decouple:
	* - The format of the message
	* - Who and will write the message and how it will
	* */

	//Writes message to a file
	public void writeMessage(Message msg, Formatter formatter, PrintWriter writer) throws IOException {
			writer.println(formatter.format(msg)); //formats and writes message
			writer.flush();
	}
}
